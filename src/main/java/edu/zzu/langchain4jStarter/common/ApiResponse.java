package edu.zzu.langchain4jStarter.common;

import lombok.Getter;
import lombok.Setter;

/**
 * API 统一返回格式
 * @param <T>
 */
@Getter
@Setter
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     * @param resultCode 错误码
     */
    public static <T> ApiResponse<T> failed(ResultCode resultCode) {
        return new ApiResponse<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     */
    public static <T> ApiResponse<T> failed(String message) {
        return new ApiResponse<>(ResultCode.FAILURE.getCode(), message, null);
    }
    
    /**
     * 参数验证失败返回结果
     */
    public static <T> ApiResponse<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }
}
