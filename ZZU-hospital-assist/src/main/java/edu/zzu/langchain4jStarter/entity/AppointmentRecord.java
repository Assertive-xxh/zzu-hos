package edu.zzu.langchain4jStarter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 挂号记录信息表 (替代并优化原appointment表)
 * </p>
 *
 * @author YourName (或者根据实际情况填写作者名)
 * @since 2025-06-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("appointment_records")
public class AppointmentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 挂号记录唯一ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 患者姓名
     */
    @TableField("username")
    private String username;

    /**
     * 患者身份证号
     */
    @TableField("id_card")
    private String idCard;

    /**
     * 联系电话 (推荐填写，便于联系)
     */
    @TableField("phone_number")
    private String phoneNumber;

    /**
     * 预约科室名称
     */
    @TableField("department_name")
    private String departmentName;

    /**
     * 预约医生姓名 (可选)
     */
    @TableField("doctor_name")
    private String doctorName;

    /**
     * 预约日期 (使用DATE类型更规范)
     */
    @TableField("appointment_date")
    private LocalDate appointmentDate;

    /**
     * 预约时间段 (例如 "09:00-09:30", "上午", "下午")
     */
    @TableField("appointment_time_slot")
    private String appointmentTimeSlot;

    /**
     * 预约状态 (PENDING:待处理/待确认, CONFIRMED:已确认, CANCELLED:已取消, COMPLETED:已完成, MISSED:爽约)
     * 在实际应用中，可以考虑使用Java Enum类型并配合MyBatis-Plus的枚举处理器
     */
    @TableField("appointment_status")
    private String appointmentStatus; // 或者定义一个 AppointmentStatusEnum

    /**
     * 预约来源 (例如: ONLINE, PHONE, ONSITE)
     */
    @TableField("source")
    private String source;

    /**
     * 备注信息 (患者或操作员填写的额外说明)
     */
    @TableField("notes")
    private String notes;

    /**
     * 记录创建时间
     * 如果使用MyBatis-Plus，可以配置自动填充: @TableField(fill = FieldFill.INSERT)
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 记录最后更新时间
     * 如果使用MyBatis-Plus，可以配置自动填充: @TableField(fill = FieldFill.INSERT_UPDATE)
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

}