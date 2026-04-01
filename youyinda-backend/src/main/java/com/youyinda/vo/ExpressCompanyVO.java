package com.youyinda.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressCompanyVO {

    private Long id;
    private String expressCode;
    private String expressName;
    private String expressLogo;
    private String timelinessDesc;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;
    private Integer supportInsurance;
    private BigDecimal insuranceRate;
    private Integer supportCancel;
    private Integer sortOrder;
}
