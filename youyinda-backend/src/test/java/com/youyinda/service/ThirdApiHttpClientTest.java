package com.youyinda.service;

import com.alibaba.fastjson.JSON;
import com.youyinda.common.BusinessException;
import com.youyinda.common.enums.ErrorCodeEnum;
import com.youyinda.dto.ThirdApiResponse;
import com.youyinda.entity.ThirdApiConfig;
import com.youyinda.service.impl.ThirdApiConfigServiceImpl;
import com.youyinda.util.ThirdApiHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 第三方 HTTP 客户端单元测试
 * 覆盖签名头构建、响应解析、超时处理
 */
@ExtendWith(MockitoExtension.class)
class ThirdApiHttpClientTest {

    @Mock
    private ThirdApiConfigServiceImpl thirdApiConfigService;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ThirdApiHttpClient thirdApiHttpClient;

    private ThirdApiConfig config;

    @BeforeEach
    void setUp() {
        config = new ThirdApiConfig();
        config.setProviderCode("liuliuyin");
        config.setProviderName("66印");
        config.setApiUrl("https://api.66yin.com");
        config.setAppId("app_123");
        config.setAppSecret("secret_456");
        config.setTimeout(3000);
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("构建签名请求头-包含appId/timestamp/sign/Authorization")
    void testBuildSignedRequest() {
        // 通过反射调用私有 buildHeaders 验证签名头
        HttpHeaders headers = invokeBuildHeaders(config);

        assertNotNull(headers);
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals("app_123", headers.getFirst("appId"));
        assertNotNull(headers.getFirst("timestamp"));
        assertNotNull(headers.getFirst("sign"));
        // 校验 sign = MD5(appId + timestamp + appSecret)
        String expectedSign = md5("app_123" + headers.getFirst("timestamp") + "secret_456");
        assertEquals(expectedSign, headers.getFirst("sign"));
        // 校验 Bearer 鉴权头
        String expectedToken = Base64.getEncoder().encodeToString("app_123:secret_456".getBytes(StandardCharsets.UTF_8));
        assertEquals("Bearer " + expectedToken, headers.getFirst("Authorization"));
    }

    @Test
    @DisplayName("解析第三方响应-正确还原data泛型对象")
    void testParseResponse() {
        when(thirdApiConfigService.getConfigByProviderCode("liuliuyin")).thenReturn(config);
        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", "THIRD_ORDER_001");
        data.put("status", "CREATED");
        ThirdApiResponse<Map<String, Object>> resp = new ThirdApiResponse<>();
        resp.setCode(200);
        resp.setMsg("success");
        resp.setData(data);
        String body = JSON.toJSONString(resp);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(body, HttpStatus.OK));

        ThirdApiResponse<HashMap> result =
                thirdApiHttpClient.post("liuliuyin", "/api/print/order/create", data, HashMap.class);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("THIRD_ORDER_001", result.getData().get("orderNo"));
    }

    @Test
    @DisplayName("第三方调用超时-抛出超时业务异常")
    void testTimeout() {
        when(thirdApiConfigService.getConfigByProviderCode("liuliuyin")).thenReturn(config);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new ResourceAccessException("Read timed out"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> thirdApiHttpClient.post("liuliuyin", "/api/print/order/create", new Object(), Object.class));
        assertEquals(ErrorCodeEnum.THIRD_PARTY_TIMEOUT.getCode(), ex.getCode());
        assertTrue(ex.getMessage().contains("超时"));
    }

    /**
     * 通过反射调用私有 buildHeaders 方法
     */
    private HttpHeaders invokeBuildHeaders(ThirdApiConfig cfg) {
        try {
            java.lang.reflect.Method m = ThirdApiHttpClient.class.getDeclaredMethod("buildHeaders", ThirdApiConfig.class);
            m.setAccessible(true);
            return (HttpHeaders) m.invoke(thirdApiHttpClient, cfg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
