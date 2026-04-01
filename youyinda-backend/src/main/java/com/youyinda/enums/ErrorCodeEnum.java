package com.youyinda.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    USER_NOT_EXIST(1001, "用户不存在"),
    USER_ALREADY_EXIST(1002, "用户已存在"),
    LOGIN_FAILED(1003, "登录失败"),
    TOKEN_INVALID(1004, "Token无效"),
    TOKEN_EXPIRED(1005, "Token已过期"),

    ORDER_NOT_EXIST(2001, "订单不存在"),
    ORDER_STATUS_ERROR(2002, "订单状态错误"),
    PAY_FAILED(2003, "支付失败"),
    INSUFFICIENT_BALANCE(2004, "余额不足"),

    COUPON_NOT_EXIST(3001, "优惠券不存在"),
    COUPON_EXPIRED(3002, "优惠券已过期"),
    COUPON_USED(3003, "优惠券已使用"),

    THIRD_PARTY_ERROR(5001, "第三方服务异常"),
    PRICE_CALCULATE_ERROR(5002, "价格计算错误");

    private final Integer code;
    private final String msg;

    ErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
