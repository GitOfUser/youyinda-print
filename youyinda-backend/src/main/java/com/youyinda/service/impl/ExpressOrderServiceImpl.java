package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.ExpressOrder;
import com.youyinda.mapper.ExpressOrderMapper;
import com.youyinda.service.ExpressOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 快递订单表Service实现类
 */
@Service
public class ExpressOrderServiceImpl extends ServiceImpl<ExpressOrderMapper, ExpressOrder> implements ExpressOrderService {

    /**
     * 根据订单主表ID获取快递订单
     * @param orderId 订单主表ID
     * @return 快递订单信息
     */
    @Override
    public ExpressOrder getByOrderId(Long orderId) {
        QueryWrapper<ExpressOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 更新第三方订单信息
     * @param orderId 订单主表ID
     * @param thirdOrderNo 第三方订单号
     * @param thirdStatus 第三方订单状态
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateThirdInfo(Long orderId, String thirdOrderNo, Integer thirdStatus) {
        ExpressOrder expressOrder = new ExpressOrder();
        expressOrder.setOrderId(orderId);
        expressOrder.setThirdOrderNo(thirdOrderNo);
        expressOrder.setThirdStatus(thirdStatus);
        expressOrder.setUpdateTime(new Date());
        QueryWrapper<ExpressOrder> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("order_id", orderId);
        return baseMapper.update(expressOrder, updateWrapper) > 0;
    }
}
