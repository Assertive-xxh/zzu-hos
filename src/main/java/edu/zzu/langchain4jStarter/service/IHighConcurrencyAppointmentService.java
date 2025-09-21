package edu.zzu.langchain4jStarter.service;

import edu.zzu.langchain4jStarter.bean.AppointmentRequest;

public interface IHighConcurrencyAppointmentService {
    String makeAppointment(AppointmentRequest request);
}
