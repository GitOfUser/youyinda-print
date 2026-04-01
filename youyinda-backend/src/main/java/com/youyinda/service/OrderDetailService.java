package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.OrderDetail;

import java.util.List;

/**
 * 订单详情表Service接口
 */
public interface OrderDetailService extends IService<OrderDetail> {
    /**
     * 根据订单ID获取详情列表
     * @param orderId 订单ID
     * @return 详情列表
     */
    List<OrderDetail> getByOrderId(Long orderId);
}
