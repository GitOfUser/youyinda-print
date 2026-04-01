package com.youyinda.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.ThirdApiConfig;
import com.youyinda.exception.BusinessException;
import com.youyinda.mapper.ThirdApiConfigMapper;
import com.youyinda.service.ThirdApiConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ThirdApiConfigServiceImpl extends ServiceImpl<ThirdApiConfigMapper, ThirdApiConfig> implements ThirdApiConfigService {

    private static final String CACHE_PREFIX = "third:api:config:";

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        log.info("初始化第三方API配置缓存...");
        refreshCache();
    }

    @Override
    public Page<ThirdApiConfig> getConfigPage(Integer pageNum, Integer pageSize, String apiType, String providerCode) {
        Page<ThirdApiConfig> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ThirdApiConfig> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(apiType)) {
            wrapper.eq(ThirdApiConfig::getApiType, apiType);
        }
        if (StrUtil.isNotBlank(providerCode)) {
            wrapper.like(ThirdApiConfig::getProviderCode, providerCode);
        }
        wrapper.orderByDesc(ThirdApiConfig::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public ThirdApiConfig getConfigById(Long id) {
        return getById(id);
    }

    @Override
    public ThirdApiConfig getConfigByProviderCode(String providerCode) {
        if (redisTemplate != null) {
            String cacheKey = CACHE_PREFIX + providerCode;
            try {
                ThirdApiConfig config = (ThirdApiConfig) redisTemplate.opsForValue().get(cacheKey);
                if (config == null) {
                    LambdaQueryWrapper<ThirdApiConfig> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(ThirdApiConfig::getProviderCode, providerCode);
                    config = getOne(wrapper);
                    if (config != null) {
                        redisTemplate.opsForValue().set(cacheKey, config, 1, TimeUnit.HOURS);
                    }
                }
                return config;
            } catch (Exception e) {
                log.warn("Redis缓存操作失败: {}", e.getMessage());
                // 降级到数据库查询
                LambdaQueryWrapper<ThirdApiConfig> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ThirdApiConfig::getProviderCode, providerCode);
                return getOne(wrapper);
            }
        } else {
            // Redis不可用时，直接从数据库查询
            LambdaQueryWrapper<ThirdApiConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ThirdApiConfig::getProviderCode, providerCode);
            return getOne(wrapper);
        }
    }

    @Override
    public List<ThirdApiConfig> getEnabledConfigs(String apiType) {
        LambdaQueryWrapper<ThirdApiConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ThirdApiConfig::getApiType, apiType);
        wrapper.eq(ThirdApiConfig::getStatus, 1);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addConfig(ThirdApiConfig config) {
        ThirdApiConfig existConfig = getConfigByProviderCode(config.getProviderCode());
        if (existConfig != null) {
            throw new BusinessException("服务商编码已存在");
        }
        boolean result = save(config);
        if (result) {
            clearCache(config.getProviderCode());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateConfig(ThirdApiConfig config) {
        boolean result = updateById(config);
        if (result) {
            clearCache(config.getProviderCode());
            log.info("第三方API配置已更新并刷新缓存：{}", config.getProviderCode());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteConfig(Long id) {
        ThirdApiConfig config = getById(id);
        if (config != null) {
            boolean result = removeById(id);
            if (result) {
                clearCache(config.getProviderCode());
            }
            return result;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteConfig(List<Long> ids) {
        List<ThirdApiConfig> configs = listByIds(ids);
        boolean result = removeByIds(ids);
        if (result) {
            for (ThirdApiConfig config : configs) {
                clearCache(config.getProviderCode());
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean enableConfig(Long id) {
        ThirdApiConfig config = new ThirdApiConfig();
        config.setId(id);
        config.setStatus(1);
        boolean result = updateById(config);
        if (result) {
            ThirdApiConfig fullConfig = getById(id);
            clearCache(fullConfig.getProviderCode());
            log.info("第三方API配置已启用：{}", fullConfig.getProviderCode());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean disableConfig(Long id) {
        ThirdApiConfig config = new ThirdApiConfig();
        config.setId(id);
        config.setStatus(0);
        boolean result = updateById(config);
        if (result) {
            ThirdApiConfig fullConfig = getById(id);
            clearCache(fullConfig.getProviderCode());
            log.info("第三方API配置已禁用：{}", fullConfig.getProviderCode());
        }
        return result;
    }

    @Override
    public void refreshCache() {
        if (redisTemplate != null) {
            try {
                List<ThirdApiConfig> configs = list();
                for (ThirdApiConfig config : configs) {
                    String cacheKey = CACHE_PREFIX + config.getProviderCode();
                    redisTemplate.opsForValue().set(cacheKey, config, 1, TimeUnit.HOURS);
                }
                log.info("第三方API配置缓存刷新完成，共{}条配置", configs.size());
            } catch (Exception e) {
                log.warn("Redis缓存刷新失败: {}", e.getMessage());
            }
        } else {
            log.warn("Redis不可用，跳过缓存刷新");
        }
    }

    @Override
    public void clearCache(String providerCode) {
        if (redisTemplate != null) {
            try {
                String cacheKey = CACHE_PREFIX + providerCode;
                redisTemplate.delete(cacheKey);
            } catch (Exception e) {
                log.warn("Redis缓存清除失败: {}", e.getMessage());
            }
        }
    }
}
