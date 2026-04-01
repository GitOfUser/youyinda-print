package com.youyinda.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 快递订单响应VO
 */
@Data
public class ExpressOrderVO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 订单总价
     */
    private BigDecimal totalPrice;

    /**
     * 支付状态
     */
    private String payStatus;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 下单时间
     */
    private Date createTime;

    /**
     * 快递公司
     */
    private String courier;

    /**
     * 快递单号
     */
    private String trackingNo;

    /**
     * 寄件地址
     */
    private UserAddressVO fromAddress;

    /**
     * 收件地址
     */
    private UserAddressVO toAddress;

    /**
     * 重量（kg）
     */
    private Double weight;

    /**
     * 物品类型
     */
    private String goodsType;

    /**
     * 备注
     */
    private String remark;
}
