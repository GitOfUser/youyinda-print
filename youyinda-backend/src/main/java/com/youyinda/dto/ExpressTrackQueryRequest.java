package com.youyinda.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExpressTrackQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private String providerOrderNo;

    private String trackingNo;

    private String expressCode;
}
