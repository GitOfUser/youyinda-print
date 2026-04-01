package com.youyinda.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ExpressOrderRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private String expressCode;

    private String senderName;

    private String senderPhone;

    private String senderProvince;

    private String senderCity;

    private String senderDistrict;

    private String senderAddress;

    private String receiverName;

    private String receiverPhone;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverAddress;

    private BigDecimal weight;

    private BigDecimal length;

    private BigDecimal width;

    private BigDecimal height;

    private String goodsName;

    private Integer goodsCount;

    private String remark;
}
