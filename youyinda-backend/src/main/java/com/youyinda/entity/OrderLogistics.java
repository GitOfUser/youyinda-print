package com.youyinda.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("order_logistics")
public class OrderLogistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("order_no")
    private String orderNo;

    @TableField("express_code")
    private String expressCode;

    @TableField("express_name")
    private String expressName;

    @TableField("tracking_no")
    private String trackingNo;

    @TableField("sender_name")
    private String senderName;

    @TableField("sender_phone")
    private String senderPhone;

    @TableField("sender_province")
    private String senderProvince;

    @TableField("sender_city")
    private String senderCity;

    @TableField("sender_district")
    private String senderDistrict;

    @TableField("sender_address")
    private String senderAddress;

    @TableField("receiver_name")
    private String receiverName;

    @TableField("receiver_phone")
    private String receiverPhone;

    @TableField("receiver_province")
    private String receiverProvince;

    @TableField("receiver_city")
    private String receiverCity;

    @TableField("receiver_district")
    private String receiverDistrict;

    @TableField("receiver_address")
    private String receiverAddress;

    @TableField("weight")
    private BigDecimal weight;

    @TableField("goods_type")
    private String goodsType;

    @TableField("goods_desc")
    private String goodsDesc;

    @TableField("insurance_amount")
    private BigDecimal insuranceAmount;

    @TableField("logistics_status")
    private String logisticsStatus;

    @TableField("track_json")
    private String trackJson;

    @TableField("third_order_no")
    private String thirdOrderNo;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField("is_delete")
    @TableLogic
    private Integer isDelete;
}
