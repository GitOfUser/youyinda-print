package com.youyinda.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrintPriceCalcVO {

    private BigDecimal unitPrice;

    private Integer effectivePages;

    private BigDecimal printCost;

    private BigDecimal bindingFee;

    private BigDecimal valueAddedFee;

    private BigDecimal totalAmount;

    private BigDecimal profitAmount;

    private BigDecimal thirdPartyCost;
}
