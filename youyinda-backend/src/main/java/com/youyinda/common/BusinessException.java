package com.youyinda.common;

import com.youyinda.common.enums.ErrorCodeEnum;
import lombok.Getter;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常
 */
@Getter
public class BusinessException extends RuntimeException {
    /**
     * 错误码
     */
    private final int code;

    /**
     * 构造方法
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造方法（使用错误码枚举）
     */
    public BusinessException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMsg());
        this.code = errorCodeEnum.getCode();
    }

    /**
     * 构造方法（使用错误码枚举和自定义消息）
     */
    public BusinessException(ErrorCodeEnum errorCodeEnum, String message) {
        super(message);
        this.code = errorCodeEnum.getCode();
    }

    /**
     * 构造方法（使用错误码枚举和自定义消息）
     */
    public BusinessException(ErrorCodeEnum errorCodeEnum, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCodeEnum.getCode();
    }

    /**
     * 构造方法（使用错误码枚举）
     */
    public BusinessException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(errorCodeEnum.getMsg(), cause);
        this.code = errorCodeEnum.getCode();
    }
}
