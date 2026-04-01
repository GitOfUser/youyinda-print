package com.youyinda.dto;

import lombok.Data;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 快递订单请求DTO
 */
@Data
public class ExpressOrderDTO {

    /**
     * 寄件地址ID
     */
    @NotNull(message = "寄件地址ID不能为空")
    private Long fromAddressId;

    /**
     * 收件地址ID
     */
    @NotNull(message = "收件地址ID不能为空")
    private Long toAddressId;

    /**
     * 快递公司
     */
    @NotBlank(message = "快递公司不能为空")
    private String courier;

    /**
     * 快递类型：标准快递/加急快递等
     */
    private String expressType;

    /**
     * 重量（kg）
     */
    @DecimalMin(value = "0.1", message = "重量至少为0.1kg")
    private Double weight;

    /**
     * 物品类型
     */
    private String goodsType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 支付方式：wechat-微信支付
     */
    @NotBlank(message = "支付方式不能为空")
    private String payType;
}
