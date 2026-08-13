package com.youyinda.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security配置类
 * 配置安全鉴权，包括接口白名单、权限规则等
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 关闭CSRF保护
                .csrf().disable()
                // 允许跨域请求
                .cors()
                .and()
                // 无状态会话
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 配置请求授权规则
                .authorizeRequests()
                // 白名单：不需要认证的接口
                .antMatchers(
                        "/v1/auth/**",                    // 认证相关接口
                        "/v1/public/**",                  // 公共接口
                        "/v1/pay/notify",                // 支付回调
                        "/v1/health/**",                 // 健康检查
                        "/actuator/**",                  // Actuator监控端点
                        "/druid/**",                     // Druid监控
                        "/static/**",                    // 静态资源
                        "/uploads/**"                    // 上传文件
                ).permitAll()
                // 其他接口需要认证
                .anyRequest().authenticated()
                .and()
                // 添加JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 配置异常处理
                .exceptionHandling()
                // 未认证的请求处理
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write("{\"code\": 401, \"msg\": \"未授权访问\", \"data\": null}");
                })
                // 权限不足的请求处理
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write("{\"code\": 403, \"msg\": \"权限不足\", \"data\": null}");
                });
    }
}
