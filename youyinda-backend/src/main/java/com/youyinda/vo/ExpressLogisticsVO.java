package com.youyinda.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressLogisticsVO {

    private String expressCode;
    private String expressName;
    private String trackingNo;
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private BigDecimal weight;
    private String goodsType;
    private String goodsDesc;
    private BigDecimal insuranceAmount;
    private String logisticsStatus;
    private String logisticsStatusDesc;
    private List<ExpressTrackVO.TrackInfo> trackList;
}
