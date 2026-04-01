package com.youyinda.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("express_company")
public class ExpressCompany implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("express_code")
    private String expressCode;

    @TableField("express_name")
    private String expressName;

    @TableField("express_logo")
    private String expressLogo;

    @TableField("timeliness_desc")
    private String timelinessDesc;

    @TableField("min_weight")
    private BigDecimal minWeight;

    @TableField("max_weight")
    private BigDecimal maxWeight;

    @TableField("support_insurance")
    private Integer supportInsurance;

    @TableField("insurance_rate")
    private BigDecimal insuranceRate;

    @TableField("support_cancel")
    private Integer supportCancel;

    @TableField("sort_order")
    private Integer sortOrder;

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
