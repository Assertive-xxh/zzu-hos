package edu.zzu.langchain4jStarter.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import edu.zzu.langchain4jStarter.entity.AppointmentRecord;
import edu.zzu.langchain4jStarter.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap; // 新增导入
import java.util.Map;   // 新增导入

@Component
public class AppointmentTools {

    private static final Logger log = LoggerFactory.getLogger(AppointmentTools.class);
    private static final DateTimeFormatter USER_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD

    @Autowired
    private AppointmentService appointmentService;

    private static final Map<String, String> STATUS_TO_DB_MAP = new HashMap<>();
    private static final Map<String, String> DB_TO_DISPLAY_MAP = new HashMap<>();

    static {
        // 数据库存储值 -> 用户显示值
        DB_TO_DISPLAY_MAP.put("PENDING", "待处理");
        DB_TO_DISPLAY_MAP.put("CONFIRMED", "已确认");
        DB_TO_DISPLAY_MAP.put("CANCELLED", "已取消");
        DB_TO_DISPLAY_MAP.put("COMPLETED", "已完成");
        DB_TO_DISPLAY_MAP.put("MISSED", "已爽约");

        // 用户可能输入的或AI理解的中文状态/英文状态 -> 数据库存储值 (统一转为大写英文)
        STATUS_TO_DB_MAP.put("待处理", "PENDING");
        STATUS_TO_DB_MAP.put("已确认", "CONFIRMED");
        STATUS_TO_DB_MAP.put("已预约", "CONFIRMED");
        STATUS_TO_DB_MAP.put("预约成功", "CONFIRMED");
        STATUS_TO_DB_MAP.put("已取消", "CANCELLED");
        STATUS_TO_DB_MAP.put("取消预约", "CANCELLED");
        STATUS_TO_DB_MAP.put("已完成", "COMPLETED");
        STATUS_TO_DB_MAP.put("已结束", "COMPLETED");
        STATUS_TO_DB_MAP.put("已爽约", "MISSED");
        STATUS_TO_DB_MAP.put("爽约", "MISSED");
        // 也直接接受英文状态 (确保它们是大写的)
        STATUS_TO_DB_MAP.put("PENDING", "PENDING");
        STATUS_TO_DB_MAP.put("CONFIRMED", "CONFIRMED");
        STATUS_TO_DB_MAP.put("CANCELLED", "CANCELLED");
        STATUS_TO_DB_MAP.put("COMPLETED", "COMPLETED");
        STATUS_TO_DB_MAP.put("MISSED", "MISSED");
    }


    @Tool(name = "查询是否有号源", value = "根据科室名称、日期、时间和可选的医生姓名查询是否有号源。")
    public boolean queryAvailability(
            @P("要查询的科室名称") String departmentName,
            @P("要查询的日期 (格式: YYYY-MM-DD)") String dateStr,
            @P("要查询的时间段 (例如: 上午, 下午, 09:00-10:00)") String timeSlot,
            @P(value = "要查询的医生姓名 (如果指定特定医生)", required = false) String doctorName) {

        log.info("执行查询是否有号源: 科室='{}', 日期='{}', 时间段='{}', 医生='{}'",
                departmentName, dateStr, timeSlot, StringUtils.hasText(doctorName) ? doctorName : "未指定");
        if (!StringUtils.hasText(departmentName) || !StringUtils.hasText(dateStr) || !StringUtils.hasText(timeSlot)) {
            log.warn("查询号源失败：科室、日期或时间段不能为空。");
            return false;
        }
        try {
            LocalDate.parse(dateStr, USER_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.warn("查询号源失败：日期格式不正确 ('{}'), 需要 YYYY-MM-DD.", dateStr);
            return false;
        }
        // 实际的号源查询逻辑会在这里。
        // 根据用户要求，默认总是有号源。
        boolean isAvailable = true;
        log.info("号源查询结果: {}", isAvailable ? "有号源" : "无号源");
        return isAvailable;
    }

    @Tool(name = "预约挂号", value = "根据用户提供的预约信息进行挂号。如果日期或时间段未指定，系统会尝试自动选择就近的可用时段。并返回挂号单详情。")
    public String bookAppointment(AppointmentRecord appointmentRecordInput) {
        log.info("尝试预约挂号，原始输入信息: {}", appointmentRecordInput);

        if (appointmentRecordInput == null) {
            return "预约失败：未提供预约信息。";
        }
        // 1. 核心信息校验
        if (!StringUtils.hasText(appointmentRecordInput.getUsername()) ||
                !StringUtils.hasText(appointmentRecordInput.getIdCard()) ||
                !StringUtils.hasText(appointmentRecordInput.getDepartmentName())) {
            return "预约失败：缺少必要的预约信息（姓名、身份证号、科室）。";
        }

        LocalDate finalDate = appointmentRecordInput.getAppointmentDate();
        String finalTimeSlot = appointmentRecordInput.getAppointmentTimeSlot();
        boolean dateAutoSelected = false;
        boolean timeAutoSelected = false;
        String doctorNameToUse = appointmentRecordInput.getDoctorName();

        // 2. 日期自动选择 (如果未提供)
        if (finalDate == null) {
            dateAutoSelected = true;
            LocalDate today = LocalDate.now();
            log.info("尝试为科室 '{}' 自动选择日期...", appointmentRecordInput.getDepartmentName());
            if (queryAvailability(appointmentRecordInput.getDepartmentName(), today.format(USER_DATE_FORMATTER), "上午", doctorNameToUse) ||
                    queryAvailability(appointmentRecordInput.getDepartmentName(), today.format(USER_DATE_FORMATTER), "下午", doctorNameToUse)) {
                finalDate = today;
                log.info("自动选择日期为今天: {}", finalDate.format(USER_DATE_FORMATTER));
            } else {
                LocalDate tomorrow = today.plusDays(1);
                if (queryAvailability(appointmentRecordInput.getDepartmentName(), tomorrow.format(USER_DATE_FORMATTER), "上午", doctorNameToUse) ||
                        queryAvailability(appointmentRecordInput.getDepartmentName(), tomorrow.format(USER_DATE_FORMATTER), "下午", doctorNameToUse)) {
                    finalDate = tomorrow;
                    log.info("自动选择日期为明天: {}", finalDate.format(USER_DATE_FORMATTER));
                } else {
                    log.warn("自动日期选择失败，近两日 '{}' 科室均无号源。", appointmentRecordInput.getDepartmentName());
                    return String.format("预约失败：系统未能自动为您在近两日内找到 '%s' 科室的可用日期。请尝试指定日期或稍后再试。", appointmentRecordInput.getDepartmentName());
                }
            }
        }

        // 3. 时间段自动选择 (如果未提供，此时finalDate已确定)
        if (!StringUtils.hasText(finalTimeSlot)) {
            timeAutoSelected = true;
            log.info("尝试为科室 '{}' 在日期 '{}' 自动选择时间段...", appointmentRecordInput.getDepartmentName(), finalDate.format(USER_DATE_FORMATTER));
            if (queryAvailability(appointmentRecordInput.getDepartmentName(), finalDate.format(USER_DATE_FORMATTER), "上午", doctorNameToUse)) {
                finalTimeSlot = "上午";
                log.info("自动选择时间段为: 上午");
            } else if (queryAvailability(appointmentRecordInput.getDepartmentName(), finalDate.format(USER_DATE_FORMATTER), "下午", doctorNameToUse)) {
                finalTimeSlot = "下午";
                log.info("自动选择时间段为: 下午");
            } else {
                log.warn("自动时间段选择失败，日期 '{}' 科室 '{}' 上下午均无号源。", finalDate.format(USER_DATE_FORMATTER), appointmentRecordInput.getDepartmentName());
                return String.format("预约失败：系统未能自动为您在 %s 找到 '%s' 科室的可用时间段（上午/下午）。请尝试指定时间段或更换日期。",
                        finalDate.format(USER_DATE_FORMATTER), appointmentRecordInput.getDepartmentName());
            }
        }

        appointmentRecordInput.setAppointmentDate(finalDate);
        appointmentRecordInput.setAppointmentTimeSlot(finalTimeSlot);

        // 4. 最终号源确认
        if (!queryAvailability(appointmentRecordInput.getDepartmentName(),
                finalDate.format(USER_DATE_FORMATTER),
                finalTimeSlot,
                doctorNameToUse)) {
            log.warn("最终号源确认失败：科室='{}', 日期='{}', 时间段='{}', 医生='{}'",
                    appointmentRecordInput.getDepartmentName(), finalDate.format(USER_DATE_FORMATTER), finalTimeSlot, doctorNameToUse);
            return String.format("预约失败：您选择的科室 '%s' 在日期 '%s' 时间段 '%s' %s最终确认无号源。请重试。",
                    appointmentRecordInput.getDepartmentName(),
                    finalDate.format(USER_DATE_FORMATTER),
                    finalTimeSlot,
                    StringUtils.hasText(doctorNameToUse) ? "医生 '" + doctorNameToUse + "' " : "");
        }

        // 5. 查找是否已存在有效的重复预约
        AppointmentRecord queryForExisting = new AppointmentRecord();
        queryForExisting.setUsername(appointmentRecordInput.getUsername());
        queryForExisting.setIdCard(appointmentRecordInput.getIdCard());
        queryForExisting.setDepartmentName(appointmentRecordInput.getDepartmentName());
        queryForExisting.setAppointmentDate(finalDate);
        queryForExisting.setAppointmentTimeSlot(finalTimeSlot);
        if (StringUtils.hasText(doctorNameToUse)) {
            queryForExisting.setDoctorName(doctorNameToUse);
        }
        AppointmentRecord existingAppointment = appointmentService.getOne(queryForExisting);
        if (existingAppointment != null && !"CANCELLED".equals(existingAppointment.getAppointmentStatus())) {
            return String.format("预约失败：您在 %s %s 的 %s %s已有有效预约 (ID: %d)。请勿重复预约。",
                    existingAppointment.getAppointmentDate().format(USER_DATE_FORMATTER),
                    existingAppointment.getAppointmentTimeSlot(),
                    existingAppointment.getDepartmentName(),
                    StringUtils.hasText(existingAppointment.getDoctorName()) ? "医生 '" + existingAppointment.getDoctorName() + "' " : "",
                    existingAppointment.getId());
        }

        // 6. 创建新的预约记录
        appointmentRecordInput.setId(null); // 确保ID为null，以便数据库自增

        // 处理预约状态
        String userInputStatus = appointmentRecordInput.getAppointmentStatus();
        String dbStatus = "PENDING"; // 默认数据库状态

        if (StringUtils.hasText(userInputStatus)) {
            String mappedStatus = STATUS_TO_DB_MAP.get(userInputStatus.toUpperCase()); // 尝试英文大写
            if (mappedStatus == null) {
                mappedStatus = STATUS_TO_DB_MAP.get(userInputStatus); // 尝试原始输入（可能是中文）
            }
            if (mappedStatus != null) {
                dbStatus = mappedStatus;
            } else {
                log.warn("无法识别的预约状态 '{}'，将使用默认状态 PENDING。", userInputStatus);
            }
        }
        appointmentRecordInput.setAppointmentStatus(dbStatus);

        boolean saveSuccess = appointmentService.save(appointmentRecordInput);

        if (saveSuccess) {
            StringBuilder autoSelectMessagePart = new StringBuilder();
            if (dateAutoSelected) {
                autoSelectMessagePart.append(String.format("已为您自动选择日期为 %s。 ", finalDate.format(USER_DATE_FORMATTER)));
            }
            if (timeAutoSelected) {
                autoSelectMessagePart.append(String.format("已为您自动选择时间段为 %s。 ", finalTimeSlot));
            }
            log.info("预约成功，记录ID: {}", appointmentRecordInput.getId());
            String displayStatus = DB_TO_DISPLAY_MAP.getOrDefault(appointmentRecordInput.getAppointmentStatus(), appointmentRecordInput.getAppointmentStatus());

            return String.format("预约成功！%s挂号单详情：\n患者姓名: %s\n身份证号: %s\n预约科室: %s\n预约日期: %s\n预约时间段: %s%s\n预约状态: %s\n预约ID: %d",
                    autoSelectMessagePart.toString(),
                    appointmentRecordInput.getUsername(),
                    appointmentRecordInput.getIdCard(),
                    appointmentRecordInput.getDepartmentName(),
                    finalDate.format(USER_DATE_FORMATTER),
                    finalTimeSlot,
                    StringUtils.hasText(doctorNameToUse) ? String.format("\n预约医生: %s", doctorNameToUse) : "",
                    displayStatus,
                    appointmentRecordInput.getId());
        } else {
            log.error("预约保存到数据库失败，信息: {}", appointmentRecordInput);
            return "预约失败：系统处理时发生错误，请稍后再试。";
        }
    }

    @Tool(name = "取消预约", value = "根据用户提供的预约ID或详细预约信息（姓名、身份证号、科室、日期、时间段）来取消已有的挂号预约。")
    public String cancelAppointment(AppointmentRecord appointmentDetailsToCancel) {
        log.info("尝试取消预约，输入信息: {}", appointmentDetailsToCancel);
        if (appointmentDetailsToCancel == null) {
            return "取消失败：未提供任何用于取消预约的信息。";
        }

        AppointmentRecord appointmentToCancel = null;

        if (appointmentDetailsToCancel.getId() != null && appointmentDetailsToCancel.getId() > 0) {
            appointmentToCancel = appointmentService.getById(appointmentDetailsToCancel.getId());
            if (appointmentToCancel == null) {
                return String.format("取消失败：未找到ID为 %d 的预约记录。", appointmentDetailsToCancel.getId());
            }
        } else {
            if (!StringUtils.hasText(appointmentDetailsToCancel.getUsername()) ||
                    !StringUtils.hasText(appointmentDetailsToCancel.getIdCard()) ||
                    !StringUtils.hasText(appointmentDetailsToCancel.getDepartmentName()) ||
                    appointmentDetailsToCancel.getAppointmentDate() == null ||
                    !StringUtils.hasText(appointmentDetailsToCancel.getAppointmentTimeSlot())) {
                return "取消失败：若不提供预约ID，则必须提供完整的预约详情（姓名、身份证号、科室、预约日期和时间段）才能定位预约。";
            }
            AppointmentRecord queryForCancel = new AppointmentRecord();
            queryForCancel.setUsername(appointmentDetailsToCancel.getUsername());
            queryForCancel.setIdCard(appointmentDetailsToCancel.getIdCard());
            queryForCancel.setDepartmentName(appointmentDetailsToCancel.getDepartmentName());
            queryForCancel.setAppointmentDate(appointmentDetailsToCancel.getAppointmentDate());
            queryForCancel.setAppointmentTimeSlot(appointmentDetailsToCancel.getAppointmentTimeSlot());
            if (StringUtils.hasText(appointmentDetailsToCancel.getDoctorName())) {
                queryForCancel.setDoctorName(appointmentDetailsToCancel.getDoctorName());
            }
            appointmentToCancel = appointmentService.getOne(queryForCancel);
            if (appointmentToCancel == null) {
                return "取消失败：未找到与您提供信息完全匹配的预约记录。请检查信息是否准确。";
            }
        }

        log.info("为取消操作执行号源查询（通常仅为流程或记录目的）...");
        boolean isSlotGenerallyAvailable = queryAvailability(
                appointmentToCancel.getDepartmentName(),
                appointmentToCancel.getAppointmentDate().format(USER_DATE_FORMATTER),
                appointmentToCancel.getAppointmentTimeSlot(),
                appointmentToCancel.getDoctorName()
        );
        log.info("取消操作前的号源查询结果: {}", isSlotGenerallyAvailable ? "该时段通常有号源" : "该时段通常无号源（或查询失败）");

        String currentDbStatus = appointmentToCancel.getAppointmentStatus();
        String currentDisplayStatus = DB_TO_DISPLAY_MAP.getOrDefault(currentDbStatus, currentDbStatus);

        if ("CANCELLED".equals(currentDbStatus)) {
            return String.format("操作提醒：您的预约（ID: %d, 科室: %s, 日期: %s %s）已经处于%s状态。",
                    appointmentToCancel.getId(),
                    appointmentToCancel.getDepartmentName(),
                    appointmentToCancel.getAppointmentDate().format(USER_DATE_FORMATTER),
                    appointmentToCancel.getAppointmentTimeSlot(),
                    currentDisplayStatus);
        }
        if ("COMPLETED".equals(currentDbStatus)) {
            return String.format("操作失败：您的预约（ID: %d, 科室: %s, 日期: %s %s）已%s，无法取消。",
                    appointmentToCancel.getId(),
                    appointmentToCancel.getDepartmentName(),
                    appointmentToCancel.getAppointmentDate().format(USER_DATE_FORMATTER),
                    appointmentToCancel.getAppointmentTimeSlot(),
                    currentDisplayStatus);
        }

        appointmentToCancel.setAppointmentStatus("CANCELLED"); // 设置为数据库应存储的英文状态
        boolean updateSuccess = appointmentService.updateById(appointmentToCancel);

        if (updateSuccess) {
            log.info("预约取消成功，记录ID: {}", appointmentToCancel.getId());
            String newDisplayStatus = DB_TO_DISPLAY_MAP.getOrDefault(appointmentToCancel.getAppointmentStatus(), appointmentToCancel.getAppointmentStatus());
            return String.format("预约取消成功！详情：患者姓名 '%s', 原预约科室 '%s', 原预约日期 '%s', 原预约时间段 '%s'%s。预约ID: %d。当前状态: %s。",
                    appointmentToCancel.getUsername(),
                    appointmentToCancel.getDepartmentName(),
                    appointmentToCancel.getAppointmentDate().format(USER_DATE_FORMATTER),
                    appointmentToCancel.getAppointmentTimeSlot(),
                    StringUtils.hasText(appointmentToCancel.getDoctorName()) ? String.format(", 原预约医生 '%s'", appointmentToCancel.getDoctorName()) : "",
                    appointmentToCancel.getId(),
                    newDisplayStatus);
        } else {
            log.error("预约取消更新失败，记录ID: {}", appointmentToCancel.getId());
            return "取消失败：系统处理时发生错误，请稍后再试。";
        }
    }
}