package edu.zzu.langchain4jStarter.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.zzu.langchain4jStarter.entity.AppointmentRecord;
import edu.zzu.langchain4jStarter.mapper.AppointmentRecordMapper;
import edu.zzu.langchain4jStarter.service.AppointmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; // 用于检查字符串是否为空

import java.time.LocalDate;


@Service
public class AppointmentRecordServiceImpl extends ServiceImpl<AppointmentRecordMapper, AppointmentRecord> implements AppointmentService {

    /**
     * 根据传入的挂号记录信息查询数据库中是否存在完全匹配的记录。
     * 主要用于检查重复预约或查询特定预约。
     *
     * @param appointmentRecord 包含查询条件的挂号记录对象。
     *                        至少需要 username, idCard, departmentName, appointmentDate, appointmentTimeSlot 来确定一个唯一的预约。
     * @return 如果找到匹配的记录，则返回该记录；否则返回 null。
     */
    // @Override // 如果此方法在AppointmentRecordService接口中定义了
    public AppointmentRecord getOne(AppointmentRecord appointmentRecord) {
        if (appointmentRecord == null) {
            return null;
        }

        LambdaQueryWrapper<AppointmentRecord> queryWrapper = new LambdaQueryWrapper<>();

        // 关键匹配条件：确保这些字段都有值才加入查询，避免空指针或无效查询
        if (StringUtils.hasText(appointmentRecord.getUsername())) {
            queryWrapper.eq(AppointmentRecord::getUsername, appointmentRecord.getUsername());
        } else {
            // 如果关键信息缺失，无法准确定位，可以根据业务逻辑决定是否直接返回null或抛出异常
            log.warn("查询预约记录时，用户名为空。");
            return null;
        }

        if (StringUtils.hasText(appointmentRecord.getIdCard())) {
            queryWrapper.eq(AppointmentRecord::getIdCard, appointmentRecord.getIdCard());
        } else {
            log.warn("查询预约记录时，身份证号为空。");
            return null;
        }

        if (StringUtils.hasText(appointmentRecord.getDepartmentName())) {
            queryWrapper.eq(AppointmentRecord::getDepartmentName, appointmentRecord.getDepartmentName());
        } else {
            log.warn("查询预约记录时，科室名称为空。");
            return null;
        }

        if (appointmentRecord.getAppointmentDate() != null) {
            queryWrapper.eq(AppointmentRecord::getAppointmentDate, appointmentRecord.getAppointmentDate());
        } else {
            log.warn("查询预约记录时，预约日期为空。");
            return null;
        }

        if (StringUtils.hasText(appointmentRecord.getAppointmentTimeSlot())) {
            queryWrapper.eq(AppointmentRecord::getAppointmentTimeSlot, appointmentRecord.getAppointmentTimeSlot());
        } else {
            log.warn("查询预约记录时，预约时间段为空。");
            return null;
        }

        // 可选的匹配条件：如果传入对象中医生姓名不为空，也作为查询条件
        if (StringUtils.hasText(appointmentRecord.getDoctorName())) {
            queryWrapper.eq(AppointmentRecord::getDoctorName, appointmentRecord.getDoctorName());
        }

        // 通常，一个用户在同一天同一个科室同一个时间段只能有一个有效预约
        // 可以根据业务需求，排除已取消或已完成的状态
        // queryWrapper.ne(AppointmentRecord::getAppointmentStatus, "CANCELLED");
        // queryWrapper.ne(AppointmentRecord::getAppointmentStatus, "COMPLETED");


        // baseMapper 是 ServiceImpl 中自动注入的，对应 AppointmentRecordMapper
        return baseMapper.selectOne(queryWrapper);
    }

    // 如果您的Service接口中定义的是 getOne(AppointmentRecord record)，则方法名应保持一致
    // @Override
    // public AppointmentRecord getOne(AppointmentRecord appointmentRecord) {
    //     return getOneByDetails(appointmentRecord);
    // }
}