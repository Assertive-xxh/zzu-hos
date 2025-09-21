package edu.zzu.langchain4jStarter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 应用启动时初始化数据
 * 模拟向Redis中预置医生号源库存
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing appointment stock in Redis...");

        // 模拟医生ID为101的医生，在两个时间段的号源
        String stockKey1 = "appointment:stock:101:09:00-10:00";
        String stockKey2 = "appointment:stock:101:10:00-11:00";

        // 设置库存为100个
        redisTemplate.opsForValue().set(stockKey1, "100");
        redisTemplate.opsForValue().set(stockKey2, "100");

        System.out.println("Stock for doctor 101 initialized. " + stockKey1 + ": 100, " + stockKey2 + ": 100");
    }
}
