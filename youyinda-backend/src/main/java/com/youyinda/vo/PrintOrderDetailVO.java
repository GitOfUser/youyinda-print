package com.youyinda.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 打印订单详情响应VO
 */
@Data
public class PrintOrderDetailVO {

    /**
     * 详情ID
     */
    private Long id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 打印页数
     */
    private Integer pages;

    /**
     * 打印份数
     */
    private Integer copies;

    /**
     * 纸张类型
     */
    private String paperType;

    /**
     * 颜色类型
     */
    private String colorType;

    /**
     * 打印面数
     */
    private String printSide;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 小计
     */
    private BigDecimal subtotal;
}
