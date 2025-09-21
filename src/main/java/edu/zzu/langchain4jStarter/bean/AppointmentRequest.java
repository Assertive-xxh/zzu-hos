package edu.zzu.langchain4jStarter.bean;

import lombok.Data;

@Data
public class AppointmentRequest {
    private Long userId;
    private Long doctorId;
    private String timeSlot;
}
