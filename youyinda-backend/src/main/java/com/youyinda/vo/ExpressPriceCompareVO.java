package com.youyinda.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 快递比价结果VO
 * 用于 price/calculate 接口返回多家快递公司的实时比价列表
 */
@Data
public class ExpressPriceCompareVO {

    /**
     * 快递公司编码
     */
    private String expressCode;

    /**
     * 快递公司名称
     */
    private String expressName;

    /**
     * 第三方基础运费
     */
    private BigDecimal thirdPartyBasePrice;

    /**
     * 平台最终售价
     */
    private BigDecimal finalPrice;

    /**
     * 平台盈利金额
     */
    private BigDecimal profitAmount;

    /**
     * 预计时效描述
     */
    private String estimatedTime;
}
