package com.youyinda.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrintCancelOrderRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private String providerOrderNo;

    private String cancelReason;
}
