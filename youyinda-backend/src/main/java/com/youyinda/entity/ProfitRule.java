package com.youyinda.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 盈利规则配置表
 */
@Data
@TableName("profit_rule")
public class ProfitRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("rule_name")
    private String ruleName;

    @TableField("rule_code")
    private String ruleCode;

    @TableField("business_type")
    private String businessType;

    @TableField("scene_code")
    private String sceneCode;

    @TableField("rule_type")
    private String ruleType;

    @TableField("base_price_field")
    private String basePriceField;

    @TableField("profit_type")
    private String profitType;

    @TableField("profit_value")
    private BigDecimal profitValue;

    @TableField("min_profit")
    private BigDecimal minProfit;

    @TableField("max_profit")
    private BigDecimal maxProfit;

    @TableField("is_enabled")
    private Integer isEnabled;

    @TableField("priority")
    private Integer priority;

    @TableField("description")
    private String description;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField("is_delete")
    @TableLogic
    private Integer isDelete;
}
