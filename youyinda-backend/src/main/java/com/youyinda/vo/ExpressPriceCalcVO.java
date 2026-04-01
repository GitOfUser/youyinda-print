package com.youyinda.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExpressPriceCalcVO {

    private BigDecimal thirdPartyBasePrice;

    private BigDecimal finalPrice;

    private BigDecimal profitAmount;
}
