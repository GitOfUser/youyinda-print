package com.youyinda.common.enums;

import lombok.Getter;

/**
 * 错误码枚举
 * 定义系统中所有的错误码
 */
@Getter
public enum ErrorCodeEnum {
    // 系统级错误
    SUCCESS(200, "成功"),
    SYSTEM_ERROR(500, "系统内部错误"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_SUPPORTED(405, "请求方法不支持"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "权限不足"),
    
    // 业务级错误
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_EXIST(1002, "用户已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    TOKEN_EXPIRED(1004, "token已过期"),
    TOKEN_INVALID(1005, "token无效"),
    
    // 微信相关错误
    WX_LOGIN_FAIL(2001, "微信登录失败"),
    WX_PAY_FAIL(2002, "微信支付失败"),
    WX_DECRYPT_FAIL(2003, "微信数据解密失败"),
    
    // 订单相关错误
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态错误"),
    ORDER_CREATE_FAIL(3003, "订单创建失败"),
    ORDER_CANCEL_FAIL(3004, "订单取消失败"),
    
    // 支付相关错误
    PAY_FAIL(4001, "支付失败"),
    PAY_TIMEOUT(4002, "支付超时"),
    REFUND_FAIL(4003, "退款失败"),
    
    // 第三方接口错误
    THIRD_PARTY_ERROR(5001, "第三方接口调用失败"),
    THIRD_PARTY_TIMEOUT(5002, "第三方接口超时"),
    
    // 文件相关错误
    FILE_UPLOAD_FAIL(6001, "文件上传失败"),
    FILE_SIZE_ERROR(6002, "文件大小超出限制"),
    FILE_TYPE_ERROR(6003, "文件类型不支持"),
    
    // 地址相关错误
    ADDRESS_NOT_FOUND(7001, "地址不存在"),
    ADDRESS_LIMIT_ERROR(7002, "地址数量超出限制"),
    
    // 价格相关错误
    PRICE_CALC_ERROR(8001, "价格计算错误"),
    PRICE_SYNC_ERROR(8002, "价格同步失败"),
    
    // 快递相关错误
    EXPRESS_COMPANY_NOT_FOUND(9001, "快递公司不存在"),
    EXPRESS_ORDER_FAIL(9002, "快递下单失败"),
    EXPRESS_TRACK_FAIL(9003, "物流查询失败");

    /**
     * 错误码
     */
    private final int code;
    
    /**
     * 错误消息
     */
    private final String msg;

    /**
     * 构造方法
     */
    ErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据错误码获取错误枚举
     */
    public static ErrorCodeEnum getByCode(int code) {
        for (ErrorCodeEnum errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return SYSTEM_ERROR;
    }
}
