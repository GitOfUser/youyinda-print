package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.ExpressOrder;

/**
 * 快递订单表Service接口
 */
public interface ExpressOrderService extends IService<ExpressOrder> {
    /**
     * 根据订单主表ID获取快递订单
     * @param orderId 订单主表ID
     * @return 快递订单信息
     */
    ExpressOrder getByOrderId(Long orderId);

    /**
     * 更新第三方订单信息
     * @param orderId 订单主表ID
     * @param thirdOrderNo 第三方订单号
     * @param thirdStatus 第三方订单状态
     * @return 是否更新成功
     */
    boolean updateThirdInfo(Long orderId, String thirdOrderNo, Integer thirdStatus);

    /**
     * 创建快递订单
     * @param userId 用户ID
     * @param request 订单创建请求
     * @return 快递订单VO
     */
    com.youyinda.vo.ExpressOrderVO createExpressOrder(Long userId, com.youyinda.dto.ExpressOrderCreateRequest request);

    /**
     * 获取快递订单列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param status 订单状态
     * @return 订单列表
     */
    com.baomidou.mybatisplus.core.metadata.IPage<com.youyinda.vo.ExpressOrderVO> listExpressOrders(Long userId, Integer page, Integer size, String status);

    /**
     * 获取快递订单详情
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 快递订单VO
     */
    com.youyinda.vo.ExpressOrderVO getExpressOrderDetail(Long userId, Long orderId);

    /**
     * 取消快递订单
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 是否取消成功
     */
    boolean cancelExpressOrder(Long userId, Long orderId);

    /**
     * 获取快递物流信息
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 物流信息
     */
    com.youyinda.vo.ExpressTrackVO getExpressTrack(Long userId, Long orderId);

    /**
     * 申请售后
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param reason 售后原因
     * @return 是否申请成功
     */
    boolean applyAfterSales(Long userId, Long orderId, String reason);
}
