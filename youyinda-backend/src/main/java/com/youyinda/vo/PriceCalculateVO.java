package com.youyinda.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 价格计算响应VO
 */
@Data
public class PriceCalculateVO {

    /**
     * 第三方基础价
     */
    private BigDecimal basePrice;

    /**
     * 单页价格
     */
    private BigDecimal unitPrice;

    /**
     * 计费页数
     */
    private Integer billingPages;

    /**
     * 装订费用
     */
    private BigDecimal bindingFee;

    /**
     * 增值费用
     */
    private BigDecimal valueAddedFee;

    /**
     * 快递费用
     */
    private BigDecimal expressFee;

    /**
     * 第三方基础总费用
     */
    private BigDecimal baseTotalPrice;

    /**
     * 最终总售价
     */
    private BigDecimal totalPrice;

    /**
     * 盈利金额
     */
    private BigDecimal profit;

    /**
     * 价格计算说明
     */
    private String calculationNotes;
}
