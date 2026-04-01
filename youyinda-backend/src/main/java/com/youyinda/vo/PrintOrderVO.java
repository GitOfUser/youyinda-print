package com.youyinda.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 打印订单响应VO
 */
@Data
public class PrintOrderVO {

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
     * 订单详情
     */
    private List<PrintOrderDetailVO> details;

    /**
     * 收货地址
     */
    private UserAddressVO address;

    /**
     * 备注
     */
    private String remark;
}
