package com.competition.backend.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "通用返回结果")
public class Result<T> {
    @Schema(description = "状态码")
    private Integer code;

    @Schema(description = "返回消息")
    private String message;

    @Schema(description = "返回数据")
    private T data;

    // 成功返回结果
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    // 失败返回结果
    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage("操作失败");
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // 参数验证失败返回结果
    public static <T> Result<T> validateFailed() {
        Result<T> result = new Result<>();
        result.setCode(400);
        result.setMessage("参数验证失败");
        return result;
    }

    public static <T> Result<T> validateFailed(String message) {
        Result<T> result = new Result<>();
        result.setCode(400);
        result.setMessage(message);
        return result;
    }

    // 未登录返回结果
    public static <T> Result<T> unauthorized(T data) {
        Result<T> result = new Result<>();
        result.setCode(401);
        result.setMessage("暂未登录或token已经过期");
        result.setData(data);
        return result;
    }

    // 未授权返回结果
    public static <T> Result<T> forbidden(T data) {
        Result<T> result = new Result<>();
        result.setCode(403);
        result.setMessage("没有相关权限");
        result.setData(data);
        return result;
    }
} 