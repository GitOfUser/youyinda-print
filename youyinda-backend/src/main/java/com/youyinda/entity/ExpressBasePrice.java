package com.youyinda.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 快递基础价格表实体类
 */
@Data
@TableName("express_base_price")
public class ExpressBasePrice {
    /**
     * 价格ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 快递公司
     */
    @TableField("courier")
    private String courier;

    /**
     * 出发省份
     */
    @TableField("from_province")
    private String fromProvince;

    /**
     * 到达省份
     */
    @TableField("to_province")
    private String toProvince;

    /**
     * 首重（kg）
     */
    @TableField("first_weight")
    private Double firstWeight;

    /**
     * 首重价格
     */
    @TableField("first_price")
    private Double firstPrice;

    /**
     * 续重单位（kg）
     */
    @TableField("continue_weight")
    private Double continueWeight;

    /**
     * 续重价格
     */
    @TableField("continue_price")
    private Double continuePrice;

    /**
     * 重量上限（kg）
     */
    @TableField("weight_ceiling")
    private Double weightCeiling;

    /**
     * 最低固定盈利额
     */
    @TableField("min_profit")
    private Double minProfit;

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
