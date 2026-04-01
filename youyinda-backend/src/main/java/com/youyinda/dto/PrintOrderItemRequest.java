package com.youyinda.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class PrintOrderItemRequest {

    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    @NotBlank(message = "纸张类型不能为空")
    private String paperType;

    @NotBlank(message = "色彩类型不能为空")
    private String colorType;

    @NotBlank(message = "单双面不能为空")
    private String singleDouble;

    @NotNull(message = "份数不能为空")
    private Integer copies;

    private String bindingType;

    @NotNull(message = "数量不能为空")
    private Integer quantity;

    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    @NotNull(message = "小计不能为空")
    private BigDecimal totalPrice;

    private String specJson;
}
