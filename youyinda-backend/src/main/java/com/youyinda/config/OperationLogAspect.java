package com.youyinda.config;

import com.youyinda.entity.AdminOperationLog;
import com.youyinda.service.AdminOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private AdminOperationLogService adminOperationLogService;

    @Pointcut("execution(* com.youyinda.controller.AdminController.*(..)) || " +
              "execution(* com.youyinda.controller.AdminOrderController.*(..)) || " +
              "execution(* com.youyinda.controller.AdminUserController.*(..)) || " +
              "execution(* com.youyinda.controller.AdminCouponController.*(..)) || " +
              "execution(* com.youyinda.controller.ProfitRuleController.*(..)) || " +
              "execution(* com.youyinda.controller.ThirdApiController.*(..)) || " +
              "execution(* com.youyinda.controller.SysConfigController.*(..))")
    public void adminOperation() {
    }

    @Around("adminOperation()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        AdminOperationLog logEntity = new AdminOperationLog();
        logEntity.setAdminName(getCurrentAdminName());
        logEntity.setOperationModule(getModuleFromRequest(request.getRequestURI()));
        logEntity.setOperationType(request.getMethod());
        logEntity.setRequestMethod(request.getMethod());
        logEntity.setRequestUrl(request.getRequestURI());
        logEntity.setIpAddress(getIpAddress(request));
        logEntity.setUserAgent(request.getHeader("User-Agent"));
        
        try {
            Object result = point.proceed();
            
            long endTime = System.currentTimeMillis();
            logEntity.setExecuteTime(endTime - startTime);
            logEntity.setResponseStatus(200);
            logEntity.setOperationDesc("操作成功");
            
            adminOperationLogService.save(logEntity);
            
            return result;
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            logEntity.setExecuteTime(endTime - startTime);
            logEntity.setResponseStatus(500);
            logEntity.setOperationDesc("操作失败：" + e.getMessage());
            
            adminOperationLogService.save(logEntity);
            
            throw e;
        }
    }

    private String getCurrentAdminName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return authentication.getName();
        }
        return "anonymous";
    }

    private String getModuleFromRequest(String uri) {
        if (uri.contains("/admin/")) {
            String[] parts = uri.split("/");
            if (parts.length > 3) {
                return parts[3];
            }
        }
        return "unknown";
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
