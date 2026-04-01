package com.youyinda.util;

import cn.hutool.json.JSONUtil;
import com.youyinda.common.BusinessException;
import com.youyinda.common.enums.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * JSON工具类
 * 用于JSON序列化和反序列化
 */
@Slf4j
public class JsonUtil {

    /**
     * 将对象转换为JSON字符串
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJsonString(Object obj) {
        try {
            return JSONUtil.toJsonStr(obj);
        } catch (Exception e) {
            log.error("JSON序列化失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "JSON序列化失败");
        }
    }

    /**
     * 将JSON字符串转换为对象
     * @param jsonStr JSON字符串
     * @param clazz 目标类
     * @param <T> 泛型
     * @return 对象
     */
    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        try {
            return JSONUtil.toBean(jsonStr, clazz);
        } catch (Exception e) {
            log.error("JSON反序列化失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "JSON反序列化失败");
        }
    }

    /**
     * 将JSON字符串转换为Map
     * @param jsonStr JSON字符串
     * @return Map
     */
    public static Map<String, Object> parseObject(String jsonStr) {
        try {
            return JSONUtil.parseObj(jsonStr);
        } catch (Exception e) {
            log.error("JSON反序列化失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "JSON反序列化失败");
        }
    }

    /**
     * 将对象转换为Map
     * @param obj 对象
     * @return Map
     */
    public static Map<String, Object> toMap(Object obj) {
        try {
            return JSONUtil.parseObj(obj);
        } catch (Exception e) {
            log.error("对象转Map失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "对象转Map失败");
        }
    }
}
