package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.OrderMain;
import com.youyinda.mapper.OrderMainMapper;
import com.youyinda.service.OrderMainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 订单主表Service实现类
 */
@Service
public class OrderMainServiceImpl extends ServiceImpl<OrderMainMapper, OrderMain> implements OrderMainService {

    /**
     * 根据用户ID获取订单列表
     * @param userId 用户ID
     * @param orderType 订单类型：1-打印订单，2-快递订单
     * @param status 订单状态
     * @return 订单列表
     */
    @Override
    public List<OrderMain> getByUserId(Long userId, Integer orderType, Integer status) {
        QueryWrapper<OrderMain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if (orderType != null) {
            queryWrapper.eq("order_type", orderType);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据用户ID和订单类型获取订单列表
     * @param userId 用户ID
     * @param orderType 订单类型：1-打印订单，2-快递订单
     * @return 订单列表
     */
    @Override
    public List<OrderMain> getByUserIdAndType(Long userId, Integer orderType) {
        QueryWrapper<OrderMain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("order_type", orderType);
        queryWrapper.orderByDesc("create_time");
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据订单号获取订单
     * @param orderNo 订单号
     * @return 订单信息
     */
    @Override
    public OrderMain getByOrderNo(String orderNo) {
        QueryWrapper<OrderMain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 生成订单号
     * @return 订单号
     */
    @Override
    public String generateOrderNo() {
        // 订单号格式：年月日时分秒+6位随机数
        StringBuilder sb = new StringBuilder();
        sb.append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        sb.append(String.format("%06d", (int) (Math.random() * 1000000)));
        return sb.toString();
    }

    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 新状态
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long orderId, Integer status) {
        OrderMain order = new OrderMain();
        order.setId(orderId);
        order.setStatus(status);
        order.setUpdateTime(new Date());
        return baseMapper.updateById(order) > 0;
    }

    /**
     * 更新支付状态
     * @param orderId 订单ID
     * @param payStatus 支付状态
     * @param payTime 支付时间
     * @param payNonce 支付回调唯一标识
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePayStatus(Long orderId, Integer payStatus, Date payTime, String payNonce) {
        OrderMain order = new OrderMain();
        order.setId(orderId);
        order.setPayStatus(payStatus);
        order.setPayTime(payTime);
        order.setPayNonce(payNonce);
        order.setUpdateTime(new Date());
        if (payStatus == 1) { // 已支付
            order.setStatus(2); // 待打印/待揽收
        }
        return baseMapper.updateById(order) > 0;
    }
}
