package com.youyinda.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ExpressPriceCalcRequest {

    @NotNull(message = "首重不能为空")
    @DecimalMin(value = "0.01", message = "首重必须大于0")
    private BigDecimal firstWeight;

    @NotNull(message = "首重基础价不能为空")
    @DecimalMin(value = "0", message = "首重基础价不能小于0")
    private BigDecimal firstWeightPrice;

    @NotNull(message = "续重单位重量不能为空")
    @DecimalMin(value = "0.01", message = "续重单位重量必须大于0")
    private BigDecimal continueWeight;

    @NotNull(message = "续重基础价不能为空")
    @DecimalMin(value = "0", message = "续重基础价不能小于0")
    private BigDecimal continueWeightPrice;

    @NotNull(message = "包裹总重量不能为空")
    @DecimalMin(value = "0", message = "包裹总重量不能小于0")
    private BigDecimal totalWeight;

    @NotNull(message = "盈利比例不能为空")
    private BigDecimal profitRate;

    @NotNull(message = "最低固定盈利额不能为空")
    private BigDecimal minFixedProfit;
}
