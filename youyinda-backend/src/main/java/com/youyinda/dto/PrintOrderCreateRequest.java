package com.youyinda.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PrintOrderCreateRequest {

    @NotNull(message = "用户地址ID不能为空")
    private Long addressId;

    private Long couponId;

    @NotNull(message = "总金额不能为空")
    private BigDecimal totalAmount;

    private String remark;

    @NotEmpty(message = "打印文件列表不能为空")
    @Valid
    private List<PrintOrderItemRequest> items;
}
