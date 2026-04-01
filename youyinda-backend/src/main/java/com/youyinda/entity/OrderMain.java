package com.youyinda.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 订单主表实体类
 */
@Data
@TableName("order_main")
public class OrderMain {
    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 订单类型：1-打印订单，2-快递订单
     */
    @TableField("order_type")
    private Integer orderType;

    /**
     * 总金额
     */
    @TableField("total_price")
    private Double totalPrice;

    /**
     * 实际支付金额
     */
    @TableField("actual_price")
    private Double actualPrice;

    /**
     * 订单状态：1-待支付，2-待打印/待揽收，3-待发货/运输中，4-待收货/派送中，5-已完成，6-已取消，7-售后中
     */
    @TableField("status")
    private Integer status;

    /**
     * 支付状态：1-未支付，2-已支付，3-支付失败，4-已退款
     */
    @TableField("pay_status")
    private Integer payStatus;

    /**
     * 支付方式：wechat-微信支付
     */
    @TableField("pay_type")
    private String payType;

    /**
     * 支付时间
     */
    @TableField("pay_time")
    private Date payTime;

    /**
     * 支付回调唯一标识
     */
    @TableField("pay_nonce")
    private String payNonce;

    /**
     * 快递公司
     */
    @TableField("courier")
    private String courier;

    /**
     * 快递单号
     */
    @TableField("tracking_no")
    private String trackingNo;

    /**
     * 收货地址ID
     */
    @TableField("address_id")
    private Long addressId;

    /**
     * 寄件地址ID
     */
    @TableField("from_address_id")
    private Long fromAddressId;

    /**
     * 重量（kg）
     */
    @TableField("weight")
    private Double weight;

    /**
     * 物品类型
     */
    @TableField("goods_type")
    private String goodsType;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 软删除：0-未删除，1-已删除
     */
    @TableField("is_delete")
    @TableLogic
    private Integer isDelete;
}
