package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.SysConfig;

import java.util.List;

/**
 * 系统配置表Service接口
 */
public interface SysConfigService extends IService<SysConfig> {
    /**
     * 根据配置键获取配置值
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置类型获取配置列表
     * @param configType 配置类型：1-系统配置，2-盈利规则，3-第三方接口
     * @return 配置列表
     */
    List<SysConfig> getByConfigType(Integer configType);

    /**
     * 刷新配置缓存
     */
    void refreshConfigCache();
}
