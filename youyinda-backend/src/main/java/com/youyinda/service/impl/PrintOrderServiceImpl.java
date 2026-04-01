package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.PrintOrder;
import com.youyinda.mapper.PrintOrderMapper;
import com.youyinda.service.PrintOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 打印订单表Service实现类
 */
@Service
public class PrintOrderServiceImpl extends ServiceImpl<PrintOrderMapper, PrintOrder> implements PrintOrderService {

    /**
     * 根据订单主表ID获取打印订单
     * @param orderId 订单主表ID
     * @return 打印订单信息
     */
    @Override
    public PrintOrder getByOrderId(Long orderId) {
        QueryWrapper<PrintOrder> queryWrapper = new QueryWrapper<>();
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
        PrintOrder printOrder = new PrintOrder();
        printOrder.setOrderId(orderId);
        printOrder.setThirdOrderNo(thirdOrderNo);
        printOrder.setThirdStatus(thirdStatus);
        printOrder.setUpdateTime(new Date());
        QueryWrapper<PrintOrder> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("order_id", orderId);
        return baseMapper.update(printOrder, updateWrapper) > 0;
    }
}
