package com.youyinda.dto;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 打印订单请求DTO
 */
@Data
public class PrintOrderDTO {

    /**
     * 收货地址ID
     */
    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;

    /**
     * 打印文件列表
     */
    @NotNull(message = "打印文件列表不能为空")
    private List<PrintFileDTO> files;

    /**
     * 纸张类型
     */
    @NotBlank(message = "纸张类型不能为空")
    private String paperType;

    /**
     * 颜色类型：黑白/彩色
     */
    @NotBlank(message = "颜色类型不能为空")
    private String colorType;

    /**
     * 打印面数：单面/双面
     */
    @NotBlank(message = "打印面数不能为空")
    private String printSide;

    /**
     * 装订类型
     */
    private String bindingType;

    /**
     * 增值服务
     */
    private String valueAddedService;

    /**
     * 备注
     */
    private String remark;

    /**
     * 支付方式：wechat-微信支付
     */
    @NotBlank(message = "支付方式不能为空")
    private String payType;
}
