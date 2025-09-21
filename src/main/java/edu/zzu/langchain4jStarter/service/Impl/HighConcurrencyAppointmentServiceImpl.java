package edu.zzu.langchain4jStarter.service.Impl;

import edu.zzu.langchain4jStarter.bean.AppointmentRequest;
import edu.zzu.langchain4jStarter.service.IHighConcurrencyAppointmentService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class HighConcurrencyAppointmentServiceImpl implements IHighConcurrencyAppointmentService {

    private static final Logger log = LoggerFactory.getLogger(HighConcurrencyAppointmentServiceImpl.class);
    private static final String APPOINTMENT_STOCK_KEY = "appointment:stock:";
    private static final String APPOINTMENT_LOCK_KEY = "appointment:lock:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private KafkaTemplate<String, AppointmentRequest> kafkaTemplate;

    @Override
    public String makeAppointment(AppointmentRequest request) {
        // 1. 检查Redis中是否有余号
        String stockKey = APPOINTMENT_STOCK_KEY + request.getDoctorId() + ":" + request.getTimeSlot();
        String stock = redisTemplate.opsForValue().get(stockKey);

        if (stock == null || Integer.parseInt(stock) <= 0) {
            return "抱歉，该时段已无号源。";
        }

        // 2. 使用Redisson获取分布式锁，防止超卖
        RLock lock = redissonClient.getLock(APPOINTMENT_LOCK_KEY + request.getDoctorId() + ":" + request.getTimeSlot());
        try {
            // 尝试在10秒内获取锁，锁的有效期为60秒
            boolean isLocked = lock.tryLock(10, 60, TimeUnit.SECONDS);
            if (!isLocked) {
                return "系统繁忙，请稍后再试。";
            }

            // 再次检查库存
            stock = redisTemplate.opsForValue().get(stockKey);
            if (stock == null || Integer.parseInt(stock) <= 0) {
                return "抱歉，该时段已无号源。";
            }

            // 3. 扣减Redis库存
            redisTemplate.opsForValue().decrement(stockKey);

            // 4. 发送消息到Kafka进行异步处理（例如创建订单、写入数据库）
            kafkaTemplate.send("appointment-topic", request);

            log.info("用户 {} 成功预约医生 {} 的 {} 时段，请求已发送至Kafka。", request.getUserId(), request.getDoctorId(), request.getTimeSlot());

            return "预约成功，正在处理中...";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁时被中断", e);
            return "系统异常，请稍后再试。";
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
