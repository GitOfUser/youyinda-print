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
public class PrintOrderItemVO {

    private Long id;
    private String itemName;
    private String fileUrl;
    private String fileName;
    private Integer pageCount;
    private String paperType;
    private String colorType;
    private String singleDouble;
    private Integer copies;
    private String bindingType;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
