package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.*;
import com.youyinda.enums.OrderStatusEnum;
import com.youyinda.mapper.AdminOperationLogMapper;
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
        dashboard.setTrendData(calculateTrendData());
        
        return dashboard;
    }

    private DashboardVO.OrderStats calculateOrderStats() {
        DashboardVO.OrderStats stats = new DashboardVO.OrderStats();
        
        LambdaQueryWrapper<OrderMain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderMain::getIsDelete, 0);
        
        Long total = orderMainService.count(wrapper);
        stats.setTotalOrders(total);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        
        stats.setTodayOrders(0L);
        stats.setPendingOrders(0L);
        stats.setCompletedOrders(0L);
        
        return stats;
    }

    private DashboardVO.RevenueStats calculateRevenueStats() {
        DashboardVO.RevenueStats stats = new DashboardVO.RevenueStats();
        
        stats.setTotalRevenue(BigDecimal.ZERO);
        stats.setTodayRevenue(BigDecimal.ZERO);
        stats.setTotalProfit(BigDecimal.ZERO);
        stats.setTodayProfit(BigDecimal.ZERO);
        
        return stats;
    }

    private DashboardVO.UserStats calculateUserStats() {
        DashboardVO.UserStats stats = new DashboardVO.UserStats();
        
        Long totalUsers = userInfoService.count();
        stats.setTotalUsers(totalUsers);
        stats.setTodayNewUsers(0L);
        stats.setWeekNewUsers(0L);
        stats.setMonthNewUsers(0L);
        
        return stats;
    }

    private DashboardVO.BusinessRatio calculateBusinessRatio() {
        DashboardVO.BusinessRatio ratio = new DashboardVO.BusinessRatio();
        
        ratio.setPrintRatio(BigDecimal.valueOf(50.00));
        ratio.setExpressRatio(BigDecimal.valueOf(50.00));
        
        return ratio;
    }

    private DashboardVO.TrendData calculateTrendData() {
        DashboardVO.TrendData trendData = new DashboardVO.TrendData();
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Calendar calendar = Calendar.getInstance();
        
        String[] dates = new String[7];
        Long[] orderCounts = new Long[7];
        BigDecimal[] revenues = new BigDecimal[7];
        
        for (int i = 6; i >= 0; i--) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            dates[6 - i] = sdf.format(calendar.getTime());
            orderCounts[6 - i] = 0L;
            revenues[6 - i] = BigDecimal.ZERO;
        }
        
        trendData.setDates(dates);
        trendData.setOrderCounts(orderCounts);
        trendData.setRevenues(revenues);
        
        return trendData;
    }
}
