package com.youyinda.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youyinda.common.R;
import com.youyinda.entity.AdminOperationLog;
import com.youyinda.service.AdminOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/v1/logs")
public class OperationLogController {

    @Autowired
    private AdminOperationLogService adminOperationLogService;

    @GetMapping
    public R<IPage<AdminOperationLog>> listLogs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String adminName) {
        
        IPage<AdminOperationLog> page = adminOperationLogService.listLogs(pageNum, pageSize, module, adminName);
        return R.ok(page);
    }
}
