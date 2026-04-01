package com.youyinda.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Feign配置类
 * 配置OpenFeign的全局配置、超时配置、重试配置等
 */
@Configuration
public class FeignConfig {

    /**
     * 请求拦截器
     * 用于添加认证信息、统一请求头等
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 这里可以添加统一的请求头，比如Authorization、Content-Type等
                template.header("Content-Type", "application/json");
                // 如果需要添加认证信息，可以从上下文中获取token
                // String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
                // template.header("Authorization", "Bearer " + token);
            }
        };
    }

    /**
     * 重试配置
     */
    @Bean
    public Retryer feignRetryer() {
        // 重试间隔1秒，最大重试次数3次，最大重试时间10秒
        return new Retryer.Default(1000, TimeUnit.SECONDS.toMillis(1), 3);
    }

    /**
     * 错误解码器
     * 用于统一处理Feign调用的错误
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            // 这里可以根据响应状态码和内容，自定义异常处理
            // 示例：
            // if (response.status() == 401) {
            //     return new UnauthorizedException("未授权访问");
            // }
            // if (response.status() == 404) {
            //     return new NotFoundException("资源不存在");
            // }
            return new Exception("Feign调用失败: " + response.status() + " " + response.reason());
        };
    }
}
