package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.OrderMain;

import java.util.List;

/**
 * 订单主表Service接口
 */
public interface OrderMainService extends IService<OrderMain> {
    /**
     * 根据用户ID获取订单列表
     * @param userId 用户ID
     * @param orderType 订单类型：1-打印订单，2-快递订单
     * @param status 订单状态
     * @return 订单列表
     */
    List<OrderMain> getByUserId(Long userId, Integer orderType, Integer status);

    /**
     * 根据用户ID和订单类型获取订单列表
     * @param userId 用户ID
     * @param orderType 订单类型：1-打印订单，2-快递订单
     * @return 订单列表
     */
    List<OrderMain> getByUserIdAndType(Long userId, Integer orderType);

    /**
     * 根据订单号获取订单
     * @param orderNo 订单号
     * @return 订单信息
     */
    OrderMain getByOrderNo(String orderNo);

    /**
     * 生成订单号
     * @return 订单号
     */
    String generateOrderNo();

    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateStatus(Long orderId, Integer status);

    /**
     * 更新支付状态
     * @param orderId 订单ID
     * @param payStatus 支付状态
     * @param payTime 支付时间
     * @param payNonce 支付回调唯一标识
     * @return 是否更新成功
     */
    boolean updatePayStatus(Long orderId, Integer payStatus, java.util.Date payTime, String payNonce);
}
