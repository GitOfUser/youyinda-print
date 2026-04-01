package com.youyinda.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ExpressPriceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String expressCode;

    private String expressName;

    private BigDecimal totalPrice;

    private BigDecimal firstWeightPrice;

    private BigDecimal continueWeightPrice;

    private String estimatedTime;

    private List<String> serviceTypes;
}
