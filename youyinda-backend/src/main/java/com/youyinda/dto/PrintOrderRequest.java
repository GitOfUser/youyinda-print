package com.youyinda.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PrintOrderRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private String fileUrl;

    private String paperType;

    private String colorType;

    private String singleDouble;

    private Integer pageCount;

    private Integer copies;

    private String bindingType;

    private String remark;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;
}
