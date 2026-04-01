package com.youyinda.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 打印订单表实体类
 */
@Data
@TableName("print_order")
public class PrintOrder {
    /**
     * 打印订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单主表ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 页数
     */
    @TableField("page_count")
    private Integer pageCount;

    /**
     * 打印类型：1-黑白，2-彩色
     */
    @TableField("color_type")
    private Integer colorType;

    /**
     * 纸张类型：1-A4，2-A3，3-其他
     */
    @TableField("paper_type")
    private Integer paperType;

    /**
     * 装订类型：0-不装订，1-胶装，2-骑马订
     */
    @TableField("binding_type")
    private Integer bindingType;

    /**
     * 是否双面：0-单面，1-双面
     */
    @TableField("double_sided")
    private Integer doubleSided;

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
