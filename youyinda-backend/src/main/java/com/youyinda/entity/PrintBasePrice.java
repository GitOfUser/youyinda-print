package com.youyinda.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 打印基础价格表实体类
 */
@Data
@TableName("print_base_price")
public class PrintBasePrice {
    /**
     * 价格ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 纸张类型：A4，A3，其他
     */
    @TableField("paper_type")
    private String paperType;

    /**
     * 颜色类型：黑白，彩色
     */
    @TableField("color_type")
    private String colorType;

    /**
     * 打印面数：单面，双面
     */
    @TableField("print_side")
    private String printSide;

    /**
     * 基础价格
     */
    @TableField("base_price")
    private Double basePrice;

    /**
     * 最低限价
     */
    @TableField("min_price")
    private Double minPrice;

    /**
     * 盈利比例（%）
     */
    @TableField("profit_ratio")
    private Double profitRatio;

    /**
     * 第三方服务商
     */
    @TableField("third_provider")
    private String thirdProvider;

    /**
     * 服务商代码
     */
    @TableField("provider_code")
    private String providerCode;

    /**
     * 单双面：single-单面，double-双面
     */
    @TableField("single_double")
    private String singleDouble;

    /**
     * 最低订单价格
     */
    @TableField("min_order_price")
    private Double minOrderPrice;

    /**
     * 状态：0-未激活，1-激活
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否激活：0-未激活，1-激活
     */
    @TableField("is_active")
    private Integer isActive;

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
