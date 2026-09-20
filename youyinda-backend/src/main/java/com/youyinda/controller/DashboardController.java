package com.youyinda.controller;

import com.youyinda.common.R;
import com.youyinda.entity.OrderMain;
import com.youyinda.entity.UserInfo;
import com.youyinda.service.AdminOperationLogService;
import com.youyinda.service.OrderMainService;
import com.youyinda.service.UserInfoService;
import com.youyinda.vo.DashboardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin/v1/dashboard")
public class DashboardController {

    @Autowired
    private AdminOperationLogService adminOperationLogService;

    @Autowired
    private OrderMainService orderMainService;

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/stats")
    public R<DashboardVO> getDashboardStats() {
        DashboardVO dashboard = adminOperationLogService.getDashboardData();
        return R.ok(dashboard);
    }

    @GetMapping("/chart")
    public R<DashboardVO.TrendData> getDashboardChart(@RequestParam(defaultValue = "7") Integer days) {
        int d = (days == null || days < 1) ? 7 : Math.min(days, 30);
        return R.ok(adminOperationLogService.getTrendData(d));
    }

    @GetMapping("/recent-orders")
    public R<List<Map<String, Object>>> getRecentOrders() {
        List<OrderMain> orders = orderMainService.lambdaQuery()
                .eq(OrderMain::getIsDelete, 0)
                .orderByDesc(OrderMain::getCreateTime)
                .last("LIMIT 10")
                .list();
        Set<Long> userIds = orders.stream()
                .map(OrderMain::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> nicknameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            userInfoService.lambdaQuery().in(UserInfo::getId, userIds).list()
                    .forEach(u -> nicknameMap.put(u.getId(), u.getNickname()));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> result = new ArrayList<>();
        for (OrderMain o : orders) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("orderNo", o.getOrderNo());
            m.put("orderType", o.getOrderType());
            m.put("userId", o.getUserId());
            m.put("nickname", o.getUserId() == null ? null : nicknameMap.getOrDefault(o.getUserId(), "用户" + o.getUserId()));
            m.put("status", o.getStatus());
            m.put("actualPrice", o.getActualPrice());
            m.put("createTime", o.getCreateTime() == null ? null : sdf.format(o.getCreateTime()));
            result.add(m);
        }
        return R.ok(result);
    }
}
