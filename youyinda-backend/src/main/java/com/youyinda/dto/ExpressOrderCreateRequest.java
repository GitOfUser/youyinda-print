package com.youyinda.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ExpressOrderCreateRequest {

    @NotNull(message = "寄件地址ID不能为空")
    private Long senderAddressId;

    @NotNull(message = "收件地址ID不能为空")
    private Long receiverAddressId;

    @NotBlank(message = "快递公司编码不能为空")
    private String expressCode;

    @NotNull(message = "包裹重量不能为空")
    private BigDecimal weight;

    private String goodsType;

    private String goodsDesc;

    private BigDecimal insuranceAmount;

    private Long couponId;

    @NotNull(message = "总金额不能为空")
    private BigDecimal totalAmount;

    private String remark;
}
