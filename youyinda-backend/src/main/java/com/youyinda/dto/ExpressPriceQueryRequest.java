package com.youyinda.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ExpressPriceQueryRequest {

    @NotBlank(message = "快递公司编码不能为空")
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
