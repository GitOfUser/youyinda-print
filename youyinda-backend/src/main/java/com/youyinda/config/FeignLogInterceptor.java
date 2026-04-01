package com.youyinda.config;

import cn.hutool.core.util.IdUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignLogInterceptor implements RequestInterceptor {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public void apply(RequestTemplate template) {
        String traceId = IdUtil.fastSimpleUUID();
        template.header(TRACE_ID_HEADER, traceId);
        log.debug("Feign请求添加追踪ID: {}, URL: {}", traceId, template.url());
    }
}
