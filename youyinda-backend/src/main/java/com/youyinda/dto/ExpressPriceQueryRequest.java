package com.youyinda.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ExpressPriceQueryRequest {

    // 价格试算(比价)接口遍历所有启用快递公司，expressCode 仅在下单/物流等单家场景必填，此处不强制
    private String expressCode;

    @NotBlank(message = "寄件省份不能为空")
    private String senderProvince;

    private String senderCity;

    @NotBlank(message = "收件省份不能为空")
    private String receiverProvince;

    private String receiverCity;

    @NotNull(message = "包裹重量不能为空")
    private BigDecimal weight;
}
