package com.youyinda.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyinda.common.R;
import com.youyinda.entity.ThirdApiConfig;
import com.youyinda.service.ThirdApiConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/third-api-config")
public class ThirdApiConfigController {

    @Autowired
    private ThirdApiConfigService thirdApiConfigService;

    @GetMapping("/page")
    public R<Page<ThirdApiConfig>> getConfigPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String apiType,
            @RequestParam(required = false) String providerCode) {
        Page<ThirdApiConfig> page = thirdApiConfigService.getConfigPage(pageNum, pageSize, apiType, providerCode);
        return R.ok(page);
    }

    @GetMapping("/{id}")
    public R<ThirdApiConfig> getConfigById(@PathVariable Long id) {
        ThirdApiConfig config = thirdApiConfigService.getConfigById(id);
        return R.ok(config);
    }

    @GetMapping("/provider/{providerCode}")
    public R<ThirdApiConfig> getConfigByProviderCode(@PathVariable String providerCode) {
        ThirdApiConfig config = thirdApiConfigService.getConfigByProviderCode(providerCode);
        return R.ok(config);
    }

    @GetMapping("/enabled/{apiType}")
    public R<List<ThirdApiConfig>> getEnabledConfigs(@PathVariable String apiType) {
        List<ThirdApiConfig> configs = thirdApiConfigService.getEnabledConfigs(apiType);
        return R.ok(configs);
    }

    @PostMapping
    public R<Boolean> addConfig(@Validated @RequestBody ThirdApiConfig config) {
        Boolean result = thirdApiConfigService.addConfig(config);
        return R.ok(result);
    }

    @PutMapping
    public R<Boolean> updateConfig(@Validated @RequestBody ThirdApiConfig config) {
        Boolean result = thirdApiConfigService.updateConfig(config);
        return R.ok(result);
    }

    @DeleteMapping("/{id}")
    public R<Boolean> deleteConfig(@PathVariable Long id) {
        Boolean result = thirdApiConfigService.deleteConfig(id);
        return R.ok(result);
    }

    @DeleteMapping("/batch")
    public R<Boolean> batchDeleteConfig(@RequestBody List<Long> ids) {
        Boolean result = thirdApiConfigService.batchDeleteConfig(ids);
        return R.ok(result);
    }

    @PostMapping("/enable/{id}")
    public R<Boolean> enableConfig(@PathVariable Long id) {
        Boolean result = thirdApiConfigService.enableConfig(id);
        return R.ok(result);
    }

    @PostMapping("/disable/{id}")
    public R<Boolean> disableConfig(@PathVariable Long id) {
        Boolean result = thirdApiConfigService.disableConfig(id);
        return R.ok(result);
    }

    @PostMapping("/refresh")
    public R<Void> refreshCache() {
        thirdApiConfigService.refreshCache();
        return R.ok();
    }
}
