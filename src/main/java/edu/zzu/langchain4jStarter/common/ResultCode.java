package edu.zzu.langchain4jStarter.common;

import lombok.Getter;

/**
 * API 统一返回状态码
 */
@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILURE(500, "服务器内部错误"),
    
    // 用户相关
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    
    // 参数校验
    VALIDATE_FAILED(422, "参数检验失败"),

    // 业务逻辑
    APPOINTMENT_STOCK_NOT_ENOUGH(1001, "号源库存不足"),
    SYSTEM_BUSY(1002, "系统繁忙，请稍后再试");


    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
