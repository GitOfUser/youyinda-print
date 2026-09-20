package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.*;
import com.youyinda.enums.OrderStatusEnum;
import com.youyinda.mapper.AdminOperationLogMapper;
import com.youyinda.mapper.SysConfigMapper;
import com.youyinda.service.AdminOperationLogService;
import com.youyinda.service.OrderMainService;
import com.youyinda.service.UserInfoService;
import com.youyinda.vo.DashboardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class AdminOperationLogServiceImpl extends ServiceImpl<AdminOperationLogMapper, AdminOperationLog> implements AdminOperationLogService {

    @Autowired
    private OrderMainService orderMainService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public IPage<AdminOperationLog> listLogs(Integer pageNum, Integer pageSize, String module, String adminName) {
        Page<AdminOperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AdminOperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (module != null && !module.isEmpty()) {
            wrapper.eq(AdminOperationLog::getOperationModule, module);
        }
        if (adminName != null && !adminName.isEmpty()) {
            wrapper.eq(AdminOperationLog::getAdminName, adminName);
        }
        
        wrapper.orderByDesc(AdminOperationLog::getCreateTime);
        
        return page(page, wrapper);
    }

    @Override
    public void logOperation(AdminOperationLog log) {
        save(log);
    }

    @Override
    public DashboardVO getDashboardData() {
        DashboardVO dashboard = new DashboardVO();

        dashboard.setOrderStats(calculateOrderStats());
        dashboard.setRevenueStats(calculateRevenueStats());
        dashboard.setUserStats(calculateUserStats());
        dashboard.setBusinessRatio(calculateBusinessRatio());
        dashboard.setTrendData(getTrendData(7));

        return dashboard;
    }

    @Override
    public DashboardVO.TrendData getTrendData(int days) {
        DashboardVO.TrendData trendData = new DashboardVO.TrendData();
        SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat labelSdf = new SimpleDateFormat("MM-dd");
        int size = Math.max(1, Math.min(days, 30));
        String[] dates = new String[size];
        Long[] orderCounts = new Long[size];
        BigDecimal[] revenues = new BigDecimal[size];

        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH, -(size - 1));
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < size; i++) {
            Calendar dayStart = (Calendar) start.clone();
            dayStart.add(Calendar.DAY_OF_MONTH, i);
            Calendar dayEnd = (Calendar) dayStart.clone();
            dayEnd.add(Calendar.DAY_OF_MONTH, 1);
            dates[i] = labelSdf.format(dayStart.getTime());

            List<OrderMain> dayOrders = orderMainService.lambdaQuery()
                    .eq(OrderMain::getIsDelete, 0)
                    .ge(OrderMain::getCreateTime, daySdf.format(dayStart.getTime()) + " 00:00:00")
                    .lt(OrderMain::getCreateTime, daySdf.format(dayEnd.getTime()) + " 00:00:00")
                    .list();
            orderCounts[i] = (long) dayOrders.size();
            BigDecimal rev = BigDecimal.ZERO;
            for (OrderMain o : dayOrders) {
                if (o.getActualPrice() != null && o.getStatus() != null && o.getStatus() != 6) {
                    rev = rev.add(BigDecimal.valueOf(o.getActualPrice()));
                }
            }
            revenues[i] = rev;
        }

        trendData.setDates(dates);
        trendData.setOrderCounts(orderCounts);
        trendData.setRevenues(revenues);
        return trendData;
    }

    private DashboardVO.OrderStats calculateOrderStats() {
        DashboardVO.OrderStats stats = new DashboardVO.OrderStats();

        LambdaQueryWrapper<OrderMain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderMain::getIsDelete, 0);
        stats.setTotalOrders(orderMainService.count(wrapper));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String today = sdf.format(new Date());
        LambdaQueryWrapper<OrderMain> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(OrderMain::getIsDelete, 0).ge(OrderMain::getCreateTime, today);
        stats.setTodayOrders(orderMainService.count(todayWrapper));

        LambdaQueryWrapper<OrderMain> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(OrderMain::getIsDelete, 0).in(OrderMain::getStatus, Arrays.asList(1, 2, 3, 4));
        stats.setPendingOrders(orderMainService.count(pendingWrapper));

        LambdaQueryWrapper<OrderMain> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(OrderMain::getIsDelete, 0).eq(OrderMain::getStatus, 5);
        stats.setCompletedOrders(orderMainService.count(completedWrapper));

        return stats;
    }

    private DashboardVO.RevenueStats calculateRevenueStats() {
        DashboardVO.RevenueStats stats = new DashboardVO.RevenueStats();
        BigDecimal total = sumRevenue(null);
        BigDecimal today = sumRevenue(new Date());
        stats.setTotalRevenue(total);
        stats.setTodayRevenue(today);
        BigDecimal profitRatio = getProfitRatio();
        stats.setTotalProfit(total.multiply(profitRatio).setScale(2, RoundingMode.HALF_UP));
        stats.setTodayProfit(today.multiply(profitRatio).setScale(2, RoundingMode.HALF_UP));
        return stats;
    }

    private BigDecimal sumRevenue(Date from) {
        List<OrderMain> list = orderMainService.lambdaQuery()
                .eq(OrderMain::getIsDelete, 0)
                .notIn(OrderMain::getStatus, Collections.singletonList(6))
                .ge(from != null, OrderMain::getCreateTime,
                        from == null ? null : new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(from))
                .list();
        BigDecimal sum = BigDecimal.ZERO;
        for (OrderMain o : list) {
            if (o.getActualPrice() != null) {
                sum = sum.add(BigDecimal.valueOf(o.getActualPrice()));
            }
        }
        return sum;
    }

    private BigDecimal getProfitRatio() {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, "profit_ratio").last("LIMIT 1"));
        if (config != null && config.getConfigValue() != null) {
            try {
                return new BigDecimal(config.getConfigValue()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            } catch (Exception ignored) {
                // ignore
            }
        }
        return new BigDecimal("0.10");
    }

    private DashboardVO.UserStats calculateUserStats() {
        DashboardVO.UserStats stats = new DashboardVO.UserStats();
        stats.setTotalUsers(userInfoService.count());
        stats.setTodayNewUsers(countUsersSinceDays(0));
        stats.setWeekNewUsers(countUsersSinceDays(7));
        stats.setMonthNewUsers(countUsersSinceDays(30));
        return stats;
    }

    private Long countUsersSinceDays(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return userInfoService.lambdaQuery().ge(UserInfo::getCreateTime, cal.getTime()).count();
    }

    private DashboardVO.BusinessRatio calculateBusinessRatio() {
        DashboardVO.BusinessRatio ratio = new DashboardVO.BusinessRatio();
        List<OrderMain> orders = orderMainService.lambdaQuery().eq(OrderMain::getIsDelete, 0).list();
        long print = orders.stream().filter(o -> o.getOrderType() != null && o.getOrderType() == 1).count();
        long express = orders.stream().filter(o -> o.getOrderType() != null && o.getOrderType() == 2).count();
        long total = print + express;
        if (total == 0) {
            ratio.setPrintRatio(BigDecimal.valueOf(50.00));
            ratio.setExpressRatio(BigDecimal.valueOf(50.00));
        } else {
            ratio.setPrintRatio(BigDecimal.valueOf(print * 100.0 / total).setScale(2, RoundingMode.HALF_UP));
            ratio.setExpressRatio(BigDecimal.valueOf(express * 100.0 / total).setScale(2, RoundingMode.HALF_UP));
        }
        return ratio;
    }
}
