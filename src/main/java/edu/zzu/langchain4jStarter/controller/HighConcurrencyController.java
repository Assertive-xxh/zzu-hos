package edu.zzu.langchain4jStarter.controller;

import edu.zzu.langchain4jStarter.bean.AppointmentRequest;
import edu.zzu.langchain4jStarter.service.IHighConcurrencyAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointment")
public class HighConcurrencyController {

    @Autowired
    private IHighConcurrencyAppointmentService appointmentService;

    @PostMapping("/make")
    public String makeAppointment(@RequestBody AppointmentRequest request) {
        // 模拟2000 QPS的场景，这里只是一个入口
        // 实际的并发控制和处理在Service层实现
        return appointmentService.makeAppointment(request);
    }
}
