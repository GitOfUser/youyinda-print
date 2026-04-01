package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.OrderDetail;
import com.youyinda.mapper.OrderDetailMapper;
import com.youyinda.service.OrderDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单详情表Service实现类
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

    /**
     * 根据订单ID获取详情列表
     * @param orderId 订单ID
     * @return 详情列表
     */
    @Override
    public List<OrderDetail> getByOrderId(Long orderId) {
        QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        return baseMapper.selectList(queryWrapper);
    }
}
