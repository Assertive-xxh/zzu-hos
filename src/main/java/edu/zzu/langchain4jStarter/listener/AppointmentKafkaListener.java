package edu.zzu.langchain4jStarter.listener;

import edu.zzu.langchain4jStarter.bean.AppointmentRequest;
import edu.zzu.langchain4jStarter.entity.AppointmentRecord;
import edu.zzu.langchain4jStarter.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AppointmentKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(AppointmentKafkaListener.class);

    @Autowired
    private AppointmentService appointmentService; // 假设已存在用于操作数据库的Service

    @KafkaListener(topics = "appointment-topic", groupId = "zzu-hospital-assist-group")
    public void handleAppointmentRequest(AppointmentRequest request) {
        log.info("接收到Kafka消息，开始处理预约请求: {}", request);
        try {
            // 模拟将预约信息写入数据库
            AppointmentRecord record = new AppointmentRecord();
            record.setUserId(request.getUserId());
            record.setDoctorId(request.getDoctorId());
            record.setAppointmentTimeSlot(request.getTimeSlot());
            record.setAppointmentStatus("CONFIRMED");
            record.setCreateTime(LocalDateTime.now());
            
            // 这里应该调用MyBatis Plus的Mapper来插入数据
            // appointmentService.save(record);

            log.info("预约记录已成功写入数据库: {}", record);
        } catch (Exception e) {
            log.error("处理预约请求失败: {}", request, e);
            // 此处可以添加补偿逻辑，例如将失败的请求记录到死信队列
        }
    }
}
