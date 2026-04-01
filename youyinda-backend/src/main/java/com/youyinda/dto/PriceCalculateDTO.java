package com.youyinda.dto;

import lombok.Data;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 价格计算请求DTO
 */
@Data
public class PriceCalculateDTO {

    /**
     * 业务类型：print-打印, express-快递
     */
    @NotNull(message = "业务类型不能为空")
    private String businessType;

    // 打印相关参数
    /**
     * 纸张类型
     */
    private String paperType;

    /**
     * 颜色类型：黑白/彩色
     */
    private String colorType;

    /**
     * 打印面数：单面/双面
     */
    private String printSide;

    /**
     * 打印页数
     */
    @Min(value = 1, message = "打印页数至少为1")
    private Integer pages;

    /**
     * 装订类型
     */
    private String bindingType;

    /**
     * 增值服务
     */
    private String valueAddedService;

    // 快递相关参数
    /**
     * 快递公司
     */
    private String courier;

    /**
     * 寄件省份
     */
    private String fromProvince;

    /**
     * 寄件城市
     */
    private String fromCity;

    /**
     * 收件省份
     */
    private String toProvince;

    /**
     * 收件城市
     */
    private String toCity;

    /**
     * 重量（kg）
     */
    @DecimalMin(value = "0.1", message = "重量至少为0.1kg")
    private Double weight;

    /**
     * 快递类型：标准快递/加急快递等
     */
    private String expressType;
}
