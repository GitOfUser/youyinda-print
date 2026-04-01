package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.SysConfig;
import com.youyinda.mapper.SysConfigMapper;
import com.youyinda.service.SysConfigService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置表Service实现类
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    // 配置缓存
    private final Map<String, String> configCache = new HashMap<>();

    /**
     * 根据配置键获取配置值
     * @param configKey 配置键
     * @return 配置值
     */
    @Override
    public String getConfigValue(String configKey) {
        // 先从缓存获取
        if (configCache.containsKey(configKey)) {
            return configCache.get(configKey);
        }
        // 从数据库获取
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("config_key", configKey);
        queryWrapper.eq("is_active", 1);
        SysConfig config = baseMapper.selectOne(queryWrapper);
        if (config != null) {
            configCache.put(configKey, config.getConfigValue());
            return config.getConfigValue();
        }
        return null;
    }

    /**
     * 根据配置类型获取配置列表
     * @param configType 配置类型：1-系统配置，2-盈利规则，3-第三方接口
     * @return 配置列表
     */
    @Override
    public List<SysConfig> getByConfigType(Integer configType) {
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("config_type", configType);
        queryWrapper.eq("is_active", 1);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 刷新配置缓存
     */
    @Override
    public void refreshConfigCache() {
        configCache.clear();
        List<SysConfig> configList = baseMapper.selectList(new QueryWrapper<SysConfig>().eq("is_active", 1));
        for (SysConfig config : configList) {
            configCache.put(config.getConfigKey(), config.getConfigValue());
        }
    }
}
