package com.youyinda.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 订单详情表实体类
 */
@Data
@TableName("order_detail")
public class OrderDetail {
    /**
     * 详情ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 产品类型：1-打印，2-快递
     */
    @TableField("product_type")
    private Integer productType;

    /**
     * 产品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 单价
     */
    @TableField("unit_price")
    private Double unitPrice;

    /**
     * 总价
     */
    @TableField("total_price")
    private Double totalPrice;

    /**
     * 文件哈希值
     */
    @TableField("file_hash")
    private String fileHash;

    /**
     * 文件URL
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 打印配置（JSON格式）
     */
    @TableField("print_config")
    private String printConfig;

    /**
     * 快递配置（JSON格式）
     */
    @TableField("express_config")
    private String expressConfig;

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
