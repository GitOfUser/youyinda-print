package com.youyinda.util;

import com.alibaba.fastjson.JSON;
import com.youyinda.common.BusinessException;
import com.youyinda.common.enums.ErrorCodeEnum;
import com.youyinda.dto.ThirdApiResponse;
import com.youyinda.entity.ThirdApiConfig;
import com.youyinda.service.ThirdApiConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * 第三方 API HTTP 调用客户端
 * 封装基于 RestTemplate 的通用调用逻辑，支持：
 * 1. 根据 providerCode 从 ThirdApiConfigService 动态获取服务商配置（URL / 密钥 / 超时）
 * 2. 自动注入签名头：sign = MD5(appId + timestamp + appSecret)
 * 3. POST JSON 请求，统一解析为 ThirdApiResponse
 */
@Slf4j
@Component
public class ThirdApiHttpClient {

    @Resource
    private ThirdApiConfigService thirdApiConfigService;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 发起 POST JSON 请求
     * @param providerCode 服务商编码（如 liuliuyin / kuaidi100）
     * @param path 接口路径（如 /api/print/order/create）
     * @param body 请求体（会被序列化为 JSON）
     * @param clazz 响应 data 的泛型类型
     * @param <T> 响应数据类型
     * @return 第三方统一响应包装
     */
    public <T> ThirdApiResponse<T> post(String providerCode, String path, Object body, Class<T> clazz) {
        ThirdApiConfig config = thirdApiConfigService.getConfigByProviderCode(providerCode);
        if (config == null) {
            throw new BusinessException(ErrorCodeEnum.THIRD_PARTY_ERROR, "未找到服务商配置：" + providerCode);
        }
        String url = buildUrl(config.getApiUrl(), path);
        HttpHeaders headers = buildHeaders(config);
        HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(body), headers);

        try {
            log.info("[第三方调用] provider={}, url={}", providerCode, url);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new BusinessException(ErrorCodeEnum.THIRD_PARTY_ERROR,
                        "第三方接口返回异常：" + response.getStatusCode());
            }
            return JSON.parseObject(response.getBody(),
                    new com.alibaba.fastjson.TypeReference<ThirdApiResponse<T>>(clazz) {});
        } catch (ResourceAccessException e) {
            log.error("[第三方调用超时] provider={}, url={}", providerCode, url, e);
            throw new BusinessException(ErrorCodeEnum.THIRD_PARTY_TIMEOUT, "第三方接口调用超时");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("[第三方调用失败] provider={}, url={}", providerCode, url, e);
            throw new BusinessException(ErrorCodeEnum.THIRD_PARTY_ERROR, "第三方接口调用失败：" + e.getMessage());
        }
    }

    /**
     * 构建完整 URL
     */
    private String buildUrl(String apiUrl, String path) {
        if (apiUrl == null) {
            throw new BusinessException(ErrorCodeEnum.THIRD_PARTY_ERROR, "服务商 API 地址未配置");
        }
        String base = apiUrl.endsWith("/") ? apiUrl.substring(0, apiUrl.length() - 1) : apiUrl;
        String p = path.startsWith("/") ? path : "/" + path;
        return base + p;
    }

    /**
     * 构建请求头（含签名与时间戳）
     * 签名算法：MD5(appId + timestamp + appSecret) → sign 参数（header 传递）
     */
    private HttpHeaders buildHeaders(ThirdApiConfig config) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        long timestamp = System.currentTimeMillis();
        String sign = md5(config.getAppId() + timestamp + config.getAppSecret());
        headers.add("appId", config.getAppId());
        headers.add("timestamp", String.valueOf(timestamp));
        headers.add("sign", sign);
        // 兼容 Bearer 鉴权（部分服务商使用）
        if (config.getAppId() != null && config.getAppSecret() != null) {
            String token = java.util.Base64.getEncoder().encodeToString(
                    (config.getAppId() + ":" + config.getAppSecret()).getBytes(StandardCharsets.UTF_8));
            headers.add("Authorization", "Bearer " + token);
        }
        return headers;
    }

    /**
     * MD5 签名（小写十六进制）
     */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(ErrorCodeEnum.THIRD_PARTY_ERROR, "签名算法异常");
        }
    }

    /**
     * 生成签名（供外部复用，如回调校验）
     * @param params 参与签名的参数（自动按 key 排序后拼接）
     * @param appSecret 服务商密钥
     */
    public static String generateSign(Map<String, String> params, String appSecret) {
        Map<String, String> sorted = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String raw = sb.toString();
        if (raw.endsWith("&")) {
            raw = raw.substring(0, raw.length() - 1);
        }
        return md5Static(raw + appSecret);
    }

    private static String md5Static(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 算法不可用", e);
        }
    }
}
