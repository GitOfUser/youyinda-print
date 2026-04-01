package com.youyinda.config;

import com.youyinda.common.BusinessException;
import com.youyinda.common.R;
import com.youyinda.common.enums.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * 全局统一异常处理器
 * 处理自定义业务异常、系统异常，返回标准响应格式
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("[业务异常] URL: {}, 错误信息: {}", request.getRequestURI(), e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("[参数校验异常] URL: {}, 错误信息: {}", request.getRequestURI(), e.getMessage());
        
        // 提取字段错误信息
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        return R.error(ErrorCodeEnum.PARAM_ERROR.getCode(), errorMsg);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public R<?> handleBindException(BindException e, HttpServletRequest request) {
        log.error("[绑定异常] URL: {}, 错误信息: {}", request.getRequestURI(), e.getMessage());
        
        // 提取字段错误信息
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        return R.error(ErrorCodeEnum.PARAM_ERROR.getCode(), errorMsg);
    }

    /**
     * 处理方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("[方法不支持异常] URL: {}, 错误信息: {}", request.getRequestURI(), e.getMessage());
        return R.error(ErrorCodeEnum.METHOD_NOT_SUPPORTED.getCode(), "请求方法不支持");
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public R<?> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.error("[404异常] URL: {}, 错误信息: {}", request.getRequestURI(), e.getMessage());
        return R.error(ErrorCodeEnum.NOT_FOUND.getCode(), "接口不存在");
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e, HttpServletRequest request) {
        log.error("[系统异常] URL: {}, 错误信息: {}", request.getRequestURI(), e.getMessage(), e);
        return R.error(ErrorCodeEnum.SYSTEM_ERROR.getCode(), "系统内部错误");
    }
}
