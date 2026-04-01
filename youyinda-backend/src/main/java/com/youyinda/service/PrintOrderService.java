package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.PrintOrder;

/**
 * 打印订单表Service接口
 */
public interface PrintOrderService extends IService<PrintOrder> {
    /**
     * 根据订单主表ID获取打印订单
     * @param orderId 订单主表ID
     * @return 打印订单信息
     */
    PrintOrder getByOrderId(Long orderId);

    /**
     * 更新第三方订单信息
     * @param orderId 订单主表ID
     * @param thirdOrderNo 第三方订单号
     * @param thirdStatus 第三方订单状态
     * @return 是否更新成功
     */
    boolean updateThirdInfo(Long orderId, String thirdOrderNo, Integer thirdStatus);
}
