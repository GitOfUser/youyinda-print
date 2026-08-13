package com.youyinda.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
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
     * 认证头规则：Authorization: Bearer <base64(appId:appSecret)>
     * appId/appSecret 优先从当前请求上下文的 provider 属性中获取，
     * 若上下文未设置则回退到 third-party 配置中的默认服务商。
     */
    @Bean
    public RequestInterceptor requestInterceptor(ThirdPartyApiProperties properties) {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 统一 JSON 请求头
                template.header("Content-Type", "application/json");

                // 从请求上下文动态获取当前服务商编码
                String appId = null;
                String appSecret = null;
                try {
                    ServletRequestAttributes attributes =
                            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attributes != null) {
                        HttpServletRequest request = attributes.getRequest();
                        String providerCode = request.getHeader("X-Provider-Code");
                        if (providerCode == null) {
                            Object attr = request.getAttribute("providerCode");
                            providerCode = attr == null ? null : attr.toString();
                        }
                        if (providerCode != null) {
                            ThirdPartyApiProperties.ProviderConfig config =
                                    findProvider(properties, providerCode);
                            if (config != null) {
                                appId = config.getAppId();
                                appSecret = config.getAppSecret();
                            }
                        }
                    }
                } catch (Exception e) {
                    // 上下文获取失败，回退到默认服务商
                }

                // 回退：使用默认（第一个）服务商配置
                if (appId == null && properties != null) {
                    ThirdPartyApiProperties.ProviderConfig config = firstProvider(properties);
                    if (config != null) {
                        appId = config.getAppId();
                        appSecret = config.getAppSecret();
                    }
                }

                // 注入 Bearer 认证头
                if (appId != null && appSecret != null) {
                    String token = Base64.getEncoder()
                            .encodeToString((appId + ":" + appSecret).getBytes(StandardCharsets.UTF_8));
                    template.header("Authorization", "Bearer " + token);
                }
            }

            private ThirdPartyApiProperties.ProviderConfig findProvider(
                    ThirdPartyApiProperties properties, String providerCode) {
                if (properties.getPrint() != null && properties.getPrint().getProviders() != null) {
                    for (ThirdPartyApiProperties.ProviderConfig c : properties.getPrint().getProviders()) {
                        if (providerCode.equals(c.getCode())) {
                            return c;
                        }
                    }
                }
                if (properties.getExpress() != null && properties.getExpress().getProviders() != null) {
                    for (ThirdPartyApiProperties.ProviderConfig c : properties.getExpress().getProviders()) {
                        if (providerCode.equals(c.getCode())) {
                            return c;
                        }
                    }
                }
                return null;
            }

            private ThirdPartyApiProperties.ProviderConfig firstProvider(ThirdPartyApiProperties properties) {
                if (properties.getPrint() != null) {
                    List<ThirdPartyApiProperties.ProviderConfig> list = properties.getPrint().getProviders();
                    if (list != null && !list.isEmpty()) {
                        return list.get(0);
                    }
                }
                if (properties.getExpress() != null) {
                    List<ThirdPartyApiProperties.ProviderConfig> list = properties.getExpress().getProviders();
                    if (list != null && !list.isEmpty()) {
                        return list.get(0);
                    }
                }
                return null;
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
     * 第三方 HTTP 调用客户端
     * 统一连接超时与读取超时（3秒），供 ThirdApiHttpClient 注入使用
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        return new RestTemplate(factory);
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
