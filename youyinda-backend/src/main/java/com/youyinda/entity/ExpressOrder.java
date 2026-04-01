package com.youyinda.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 快递订单表实体类
 */
@Data
@TableName("express_order")
public class ExpressOrder {
    /**
     * 快递订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单主表ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 寄件人姓名
     */
    @TableField("sender_name")
    private String senderName;

    /**
     * 寄件人手机号（加密存储）
     */
    @TableField("sender_phone")
    private String senderPhone;

    /**
     * 寄件人省份
     */
    @TableField("sender_province")
    private String senderProvince;

    /**
     * 寄件人城市
     */
    @TableField("sender_city")
    private String senderCity;

    /**
     * 寄件人区县
     */
    @TableField("sender_district")
    private String senderDistrict;

    /**
     * 寄件人详细地址
     */
    @TableField("sender_detail")
    private String senderDetail;

    /**
     * 收件人姓名
     */
    @TableField("receiver_name")
    private String receiverName;

    /**
     * 收件人手机号（加密存储）
     */
    @TableField("receiver_phone")
    private String receiverPhone;

    /**
     * 收件人省份
     */
    @TableField("receiver_province")
    private String receiverProvince;

    /**
     * 收件人城市
     */
    @TableField("receiver_city")
    private String receiverCity;

    /**
     * 收件人区县
     */
    @TableField("receiver_district")
    private String receiverDistrict;

    /**
     * 收件人详细地址
     */
    @TableField("receiver_detail")
    private String receiverDetail;

    /**
     * 重量（kg）
     */
    @TableField("weight")
    private Double weight;

    /**
     * 体积（m³）
     */
    @TableField("volume")
    private Double volume;

    /**
     * 物品类型
     */
    @TableField("goods_type")
    private String goodsType;

    /**
     * 第三方订单号
     */
    @TableField("third_order_no")
    private String thirdOrderNo;

    /**
     * 第三方订单状态
     */
    @TableField("third_status")
    private Integer thirdStatus;

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
