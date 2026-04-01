package com.youyinda.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.AdminOperationLog;
import com.youyinda.vo.DashboardVO;

public interface AdminOperationLogService extends IService<AdminOperationLog> {

    IPage<AdminOperationLog> listLogs(Integer pageNum, Integer pageSize, String module, String adminName);

    void logOperation(AdminOperationLog log);

    DashboardVO getDashboardData();
}
