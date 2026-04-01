package com.youyinda.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PrintPriceSyncRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String providerCode;

    private String paperType;

    private String colorType;

    private String singleDouble;

    private BigDecimal basePrice;

    private BigDecimal minOrderPrice;
}
