package com.youyinda.common;

import com.youyinda.common.enums.ErrorCodeEnum;
import lombok.Data;

/**
 * 统一响应结果类
 * 包含code、msg、data三个核心字段
 */
@Data
public class R<T> {
    /**
     * 响应码
     */
    private int code;
    
    /**
     * 响应消息
     */
    private String msg;
    
    /**
     * 响应数据
     */
    private T data;

    /**
     * 私有构造方法
     */
    private R() {
    }

    /**
     * 私有构造方法
     */
    private R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> R<T> success() {
        return new R<>(200, "成功", null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> R<T> success(T data) {
        return new R<>(200, "成功", data);
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> R<T> success(String msg, T data) {
        return new R<>(200, msg, data);
    }

    /**
     * 失败响应
     */
    public static <T> R<T> error(int code, String msg) {
        return new R<>(code, msg, null);
    }

    /**
     * 失败响应（使用错误码枚举）
     */
    public static <T> R<T> error(ErrorCodeEnum errorCodeEnum) {
        return new R<>(errorCodeEnum.getCode(), errorCodeEnum.getMsg(), null);
    }

    /**
     * 失败响应（使用错误码枚举和自定义消息）
     */
    public static <T> R<T> error(ErrorCodeEnum errorCodeEnum, String msg) {
        return new R<>(errorCodeEnum.getCode(), msg, null);
    }

    /**
     * 失败响应（带数据）
     */
    public static <T> R<T> error(int code, String msg, T data) {
        return new R<>(code, msg, data);
    }
}
