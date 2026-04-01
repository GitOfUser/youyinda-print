package com.youyinda.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class PrintPriceCalcRequest {

    @NotNull(message = "第三方单页基础价不能为空")
    @DecimalMin(value = "0", message = "第三方单页基础价不能小于0")
    private BigDecimal thirdPartyUnitPrice;

    @NotNull(message = "总页数不能为空")
    @DecimalMin(value = "0", message = "总页数不能小于0")
    private Integer totalPages;

    private Boolean isDoubleSide;

    @NotNull(message = "盈利比例不能为空")
    private BigDecimal profitRate;

    @NotNull(message = "单页最低限价不能为空")
    @DecimalMin(value = "0", message = "单页最低限价不能小于0")
    private BigDecimal minUnitPrice;

    private BigDecimal bindingFee;

    private BigDecimal valueAddedFee;
}
