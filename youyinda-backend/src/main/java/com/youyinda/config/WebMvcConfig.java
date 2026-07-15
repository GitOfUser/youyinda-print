package com.youyinda.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc配置类
 * 配置跨域、拦截器、静态资源映射等
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Type", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // 上传文件映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    /**
     * 拦截器配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 这里可以添加自定义的拦截器，比如登录验证、权限检查等
        // 示例：
        // registry.addInterceptor(new LoginInterceptor())
        //         .addPathPatterns("/api/**")
        //         .excludePathPatterns("/api/auth/**", "/api/public/**");
    }
}
