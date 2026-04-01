package com.youyinda.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 优惠券信息表
 */
@Data
@TableName("coupon_info")
public class CouponInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("coupon_name")
    private String couponName;

    @TableField("coupon_type")
    private String couponType;

    @TableField("coupon_value")
    private String couponValue;

    @TableField("min_amount")
    private String minAmount;

    @TableField("max_discount")
    private String maxDiscount;

    @TableField("total_count")
    private Integer totalCount;

    @TableField("remain_count")
    private Integer remainCount;

    @TableField("per_limit")
    private Integer perLimit;

    @TableField("start_time")
    private Date startTime;

    @TableField("end_time")
    private Date endTime;

    @TableField("business_scope")
    private String businessScope;

    @TableField("status")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField("is_delete")
    @TableLogic
    private Integer isDelete;
}
