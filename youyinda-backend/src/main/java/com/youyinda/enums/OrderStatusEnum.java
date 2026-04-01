package com.youyinda.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    PENDING_PAY("pending_pay", "待支付"),
    PAID("paid", "已支付"),
    PRINTING("printing", "打印中"),
    PRINTED("printed", "已打印"),
    SHIPPED("shipped", "已发货"),
    DELIVERED("delivered", "已签收"),
    COMPLETED("completed", "已完成"),
    CANCELLED("cancelled", "已取消"),
    REFUNDING("refunding", "退款中"),
    REFUNDED("refunded", "已退款");

    private final String code;
    private final String desc;

    OrderStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderStatusEnum getByCode(String code) {
        for (OrderStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}
