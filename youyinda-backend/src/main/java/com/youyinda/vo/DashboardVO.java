package com.youyinda.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 数据看板视图对象
 */
@Data
public class DashboardVO {

    private OrderStats orderStats;

    private RevenueStats revenueStats;

    private UserStats userStats;

    private BusinessRatio businessRatio;

    private TrendData trendData;

    @Data
    public static class OrderStats {
        private Long totalOrders;
        private Long todayOrders;
        private Long pendingOrders;
        private Long completedOrders;
    }

    @Data
    public static class RevenueStats {
        private BigDecimal totalRevenue;
        private BigDecimal todayRevenue;
        private BigDecimal totalProfit;
        private BigDecimal todayProfit;
    }

    @Data
    public static class UserStats {
        private Long totalUsers;
        private Long todayNewUsers;
        private Long weekNewUsers;
        private Long monthNewUsers;
    }

    @Data
    public static class BusinessRatio {
        private BigDecimal printRatio;
        private BigDecimal expressRatio;
    }

    @Data
    public static class TrendData {
        private String[] dates;
        private Long[] orderCounts;
        private BigDecimal[] revenues;
    }
}
