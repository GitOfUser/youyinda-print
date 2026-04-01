package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.OrderLogistics;
import com.youyinda.mapper.OrderLogisticsMapper;
import com.youyinda.service.OrderLogisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderLogisticsServiceImpl extends ServiceImpl<OrderLogisticsMapper, OrderLogistics> implements OrderLogisticsService {

    @Override
    public OrderLogistics getByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderLogistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderLogistics::getOrderId, orderId);
        return this.getOne(wrapper);
    }

    @Override
    public OrderLogistics getByTrackingNo(String trackingNo) {
        LambdaQueryWrapper<OrderLogistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderLogistics::getTrackingNo, trackingNo);
        return this.getOne(wrapper);
    }

    @Override
    public void syncLogisticsStatus(Long orderId) {
        log.info("同步物流状态：orderId={}", orderId);
    }
}
