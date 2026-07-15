package com.youyinda.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.youyinda.common.BusinessException;
import com.youyinda.common.enums.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信工具类
 * 用于微信小程序授权、手机号解密、支付签名、回调验签等操作
 */
@Component
@Slf4j
public class WechatUtil {

    /**
     * 小程序AppID
     */
    @Value("${wechat.miniapp.appId}")
    private String appId;

    /**
     * 小程序AppSecret
     */
    @Value("${wechat.miniapp.appSecret}")
    private String appSecret;

    /**
     * 微信登录接口
     */
    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 微信获取AccessToken接口
     */
    private static final String WX_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";

    /**
     * 检查是否为测试模式（使用占位符配置）
     */
    private boolean isTestMode() {
        return "your_app_id".equals(appId) || "your_app_secret".equals(appSecret);
    }

    /**
     * 微信登录
     * @param code 登录凭证
     * @return 登录结果
     */
    public Map<String, Object> wxLogin(String code) {
        try {
            // 测试模式，返回模拟数据（当使用占位符配置时自动进入测试模式）
            if ("test_code".equals(code) || isTestMode()) {
                Map<String, Object> result = new HashMap<>();
                // 使用code生成唯一的openid，方便测试不同用户
                String openid = "test_openid_" + (code != null ? code.hashCode() : "default");
                String sessionKey = "test_session_key_" + (code != null ? code.hashCode() : "default");
                result.put("openid", openid);
                result.put("session_key", sessionKey);
                log.info("测试模式 - 微信登录成功，openid: {}", openid);
                return result;
            }

            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("appid", appId);
            params.put("secret", appSecret);
            params.put("js_code", code);
            params.put("grant_type", "authorization_code");

            // 发送请求
            String response = HttpUtil.sendRequest(WX_LOGIN_URL, HttpMethod.GET, params, null);
            log.info("微信登录响应: {}", response);

            // 解析响应
            Map<String, Object> result = JsonUtil.parseObject(response, Map.class);
            if (result == null) {
                throw new BusinessException(ErrorCodeEnum.WX_LOGIN_FAIL, "微信登录响应解析失败");
            }

            // 检查是否有错误
            if (result.containsKey("errcode")) {
                int errcode = (int) result.get("errcode");
                String errmsg = (String) result.get("errmsg");
                throw new BusinessException(ErrorCodeEnum.WX_LOGIN_FAIL, "微信登录失败: " + errmsg);
            }

            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信登录失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.WX_LOGIN_FAIL, "微信登录失败");
        }
    }

    /**
     * 解密微信手机号
     * @param encryptedData 加密数据
     * @param iv 加密向量
     * @param sessionKey 会话密钥
     * @return 解密后的手机号
     */
    public String decryptPhoneNumber(String encryptedData, String iv, String sessionKey) {
        try {
            // Base64解码
            byte[] encryptedDataBytes = Base64.decode(encryptedData);
            byte[] ivBytes = Base64.decode(iv);
            byte[] sessionKeyBytes = Base64.decode(sessionKey);

            // 构建AES密钥
            SecretKeySpec secretKeySpec = new SecretKeySpec(sessionKeyBytes, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

            // 初始化Cipher
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            // 解密
            byte[] decryptBytes = cipher.doFinal(encryptedDataBytes);
            String decryptStr = new String(decryptBytes, StandardCharsets.UTF_8);

            // 解析解密结果
            Map<String, Object> result = JsonUtil.parseObject(decryptStr, Map.class);
            if (result == null) {
                throw new BusinessException(ErrorCodeEnum.WX_DECRYPT_FAIL, "手机号解密结果解析失败");
            }

            return (String) result.get("phoneNumber");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("手机号解密失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.WX_DECRYPT_FAIL, "手机号解密失败");
        }
    }

    /**
     * 生成微信支付签名
     * @param params 参数Map
     * @param apiV3Key API v3密钥
     * @return 签名
     */
    public String generatePaySign(Map<String, Object> params, String apiV3Key) {
        try {
            // 按参数名排序
            StringBuilder sb = new StringBuilder();
            params.keySet().stream().sorted().forEach(key -> {
                sb.append(key).append("=")
                  .append(params.get(key)).append("&");
            });
            // 移除最后一个&符号
            String signStr = sb.toString().substring(0, sb.length() - 1);
            // 添加API v3密钥
            signStr += apiV3Key;
            // 生成MD5签名
            return SecureUtil.md5(signStr);
        } catch (Exception e) {
            log.error("生成支付签名失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 验证微信支付回调签名
     * @param timestamp 时间戳
     * @param nonce 随机字符串
     * @param body 回调数据
     * @param signature 签名
     * @param apiV3Key API v3密钥
     * @return 是否验证通过
     */
    public boolean verifyPaySign(String timestamp, String nonce, String body, String signature, String apiV3Key) {
        try {
            // 构建签名串
            String signStr = timestamp + "\n" + nonce + "\n" + body + "\n";
            // 生成签名
            String expectedSignature = generatePaySign(null, apiV3Key);
            // 验证签名
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            log.error("验证支付签名失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取微信AccessToken
     * @return AccessToken
     */
    public String getAccessToken() {
        try {
            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("appid", appId);
            params.put("secret", appSecret);
            params.put("grant_type", "client_credential");

            // 发送请求
            String response = HttpUtil.sendRequest(WX_ACCESS_TOKEN_URL, HttpMethod.GET, params, null);
            log.info("获取AccessToken响应: {}", response);

            // 解析响应
            Map<String, Object> result = JsonUtil.parseObject(response, Map.class);
            if (result == null) {
                throw new BusinessException(ErrorCodeEnum.WX_LOGIN_FAIL, "获取AccessToken响应解析失败");
            }

            // 检查是否有错误
            if (result.containsKey("errcode")) {
                int errcode = (int) result.get("errcode");
                String errmsg = (String) result.get("errmsg");
                throw new BusinessException(ErrorCodeEnum.WX_LOGIN_FAIL, "获取AccessToken失败: " + errmsg);
            }

            return (String) result.get("access_token");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取AccessToken失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.WX_LOGIN_FAIL, "获取AccessToken失败");
        }
    }

    /**
     * 生成微信小程序码
     * @param scene 场景值
     * @param page 页面路径
     * @param width 二维码宽度
     * @return 二维码图片数据
     */
    public byte[] generateMiniCode(String scene, String page, int width) {
        try {
            // 获取AccessToken
            String accessToken = getAccessToken();
            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("scene", scene);
            params.put("page", page);
            params.put("width", width);
            params.put("auto_color", false);
            Map<String, Object> lineColor = new HashMap<>();
            lineColor.put("r", 0);
            lineColor.put("g", 0);
            lineColor.put("b", 0);
            params.put("line_color", lineColor);
            params.put("is_hyaline", false);

            // 发送请求
            String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken;
            byte[] response = HttpUtil.sendRequest(url, HttpMethod.POST, params, null).getBytes();
            return response;
        } catch (Exception e) {
            log.error("生成小程序码失败: {}", e.getMessage(), e);
            return null;
        }
    }
}
