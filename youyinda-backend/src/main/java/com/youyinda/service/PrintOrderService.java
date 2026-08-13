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

    /**
     * 创建打印订单（多场景打印全链路：计价 → 第三方下单 → 落库）
     * @param userId 用户ID
     * @param request 打印订单创建请求
     * @return 打印订单VO
     */
    com.youyinda.vo.PrintOrderVO createPrintOrder(Long userId, com.youyinda.dto.PrintOrderCreateRequest request);
}
