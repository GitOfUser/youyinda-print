package com.youyinda.controller;

import com.youyinda.common.R;
import com.youyinda.service.AdminOperationLogService;
import com.youyinda.vo.DashboardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/v1/dashboard")
public class DashboardController {

    @Autowired
    private AdminOperationLogService adminOperationLogService;

    @GetMapping("/stats")
    public R<DashboardVO> getDashboardStats() {
        DashboardVO dashboard = adminOperationLogService.getDashboardData();
        return R.ok(dashboard);
    }
}
