package com.youyinda.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyinda.entity.ThirdApiConfig;

import java.util.List;

public interface ThirdApiConfigService {

    Page<ThirdApiConfig> getConfigPage(Integer pageNum, Integer pageSize, String apiType, String providerCode);

    ThirdApiConfig getConfigById(Long id);

    ThirdApiConfig getConfigByProviderCode(String providerCode);

    List<ThirdApiConfig> getEnabledConfigs(String apiType);

    Boolean addConfig(ThirdApiConfig config);

    Boolean updateConfig(ThirdApiConfig config);

    Boolean deleteConfig(Long id);

    Boolean batchDeleteConfig(List<Long> ids);

    Boolean enableConfig(Long id);

    Boolean disableConfig(Long id);

    void refreshCache();

    void clearCache(String providerCode);
}
