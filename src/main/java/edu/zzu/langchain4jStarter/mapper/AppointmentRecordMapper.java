package edu.zzu.langchain4jStarter.mapper;

import edu.zzu.langchain4jStarter.entity.AppointmentRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 挂号记录信息表 (替代并优化原appointment表) Mapper 接口
 * </p>
 *
 * @author YourName (或者根据实际情况填写作者名)
 * @since 2025-06-07
 */
@Mapper // 或者在启动类上使用 @MapperScan("edu.zzu.langchain4jStarter.mapper")
public interface AppointmentRecordMapper extends BaseMapper<AppointmentRecord> {
}