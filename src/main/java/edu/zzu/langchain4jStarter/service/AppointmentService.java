package edu.zzu.langchain4jStarter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.zzu.langchain4jStarter.entity.AppointmentRecord;

public interface AppointmentService extends IService<AppointmentRecord> {

    AppointmentRecord getOne(AppointmentRecord record);
}
