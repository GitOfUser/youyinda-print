package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.OrderLogistics;

public interface OrderLogisticsService extends IService<OrderLogistics> {

    OrderLogistics getByOrderId(Long orderId);

    OrderLogistics getByTrackingNo(String trackingNo);

    void syncLogisticsStatus(Long orderId);
}
