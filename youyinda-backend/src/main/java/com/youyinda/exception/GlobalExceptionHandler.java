package com.youyinda.exception;

import com.youyinda.common.BusinessException;
import com.youyinda.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 controller 层抛出的 com.youyinda.common.BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理历史遗留的 com.youyinda.exception.BusinessException（service 层使用）
     */
    @ExceptionHandler(com.youyinda.exception.BusinessException.class)
    public R<Void> handleLegacyBusinessException(com.youyinda.exception.BusinessException e) {
        log.error("业务异常(旧)：{}", e.getMsg());
        return R.fail(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(","));
        log.error("参数校验异常：{}", message);
        return R.fail(400, message);
    }

    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(","));
        log.error("参数绑定异常：{}", message);
        return R.fail(400, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public R<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(","));
        log.error("参数校验异常：{}", message);
        return R.fail(400, message);
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        return R.fail("系统异常，请联系管理员");
    }
}
