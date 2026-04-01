package com.youyinda.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.youyinda.common.BusinessException;
import com.youyinda.common.enums.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * HTTP工具类
 * 用于发送HTTP请求
 */
@Slf4j
public class HttpUtil {

    /**
     * 发送HTTP请求
     * @param url 请求URL
     * @param method 请求方法
     * @param params 请求参数
     * @param headers 请求头
     * @return 响应结果
     */
    public static String sendRequest(String url, HttpMethod method, Map<String, Object> params, Map<String, String> headers) {
        try {
            log.info("发送HTTP请求: {}, 方法: {}, 参数: {}", url, method, params);

            HttpRequest request;
            switch (method) {
                case GET:
                    request = HttpRequest.get(url);
                    if (params != null && !params.isEmpty()) {
                        request.form(params);
                    }
                    break;
                case POST:
                    request = HttpRequest.post(url);
                    if (params != null && !params.isEmpty()) {
                        request.body(JsonUtil.toJsonString(params));
                        request.header("Content-Type", "application/json");
                    }
                    break;
                case PUT:
                    request = HttpRequest.put(url);
                    if (params != null && !params.isEmpty()) {
                        request.body(JsonUtil.toJsonString(params));
                        request.header("Content-Type", "application/json");
                    }
                    break;
                case DELETE:
                    request = HttpRequest.delete(url);
                    if (params != null && !params.isEmpty()) {
                        request.form(params);
                    }
                    break;
                default:
                    throw new BusinessException(ErrorCodeEnum.PARAM_ERROR, "不支持的HTTP方法");
            }

            // 添加请求头
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    request.header(entry.getKey(), entry.getValue());
                }
            }

            // 发送请求
            HttpResponse response = request.timeout(10000).execute();
            String result = response.body();

            log.info("HTTP请求响应: {}, 状态码: {}", result, response.getStatus());

            // 检查响应状态码
            if (response.getStatus() >= 200 && response.getStatus() < 300) {
                return result;
            } else {
                throw new BusinessException(ErrorCodeEnum.THIRD_PARTY_ERROR, "HTTP请求失败: " + response.getStatus() + " " + result);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("发送HTTP请求失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.THIRD_PARTY_ERROR, "HTTP请求失败");
        }
    }

    /**
     * 发送GET请求
     * @param url 请求URL
     * @param params 请求参数
     * @return 响应结果
     */
    public static String get(String url, Map<String, Object> params) {
        return sendRequest(url, HttpMethod.GET, params, null);
    }

    /**
     * 发送POST请求
     * @param url 请求URL
     * @param params 请求参数
     * @return 响应结果
     */
    public static String post(String url, Map<String, Object> params) {
        return sendRequest(url, HttpMethod.POST, params, null);
    }

    /**
     * 发送PUT请求
     * @param url 请求URL
     * @param params 请求参数
     * @return 响应结果
     */
    public static String put(String url, Map<String, Object> params) {
        return sendRequest(url, HttpMethod.PUT, params, null);
    }

    /**
     * 发送DELETE请求
     * @param url 请求URL
     * @param params 请求参数
     * @return 响应结果
     */
    public static String delete(String url, Map<String, Object> params) {
        return sendRequest(url, HttpMethod.DELETE, params, null);
    }
}
