package com.youyinda.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 系统配置表实体类
 */
@Data
@TableName("sys_config")
public class SysConfig {
    /**
     * 配置ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 配置键
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置类型：1-系统配置，2-盈利规则，3-第三方接口
     */
    @TableField("config_type")
    private Integer configType;

    /**
     * 配置描述
     */
    @TableField("description")
    private String description;

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
