package com.youyinda.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyinda.common.R;
import com.youyinda.entity.ThirdApiConfig;
import com.youyinda.mapper.ThirdApiConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/v1/third-api")
public class ThirdApiController {

    @Autowired
    private ThirdApiConfigMapper thirdApiConfigMapper;

    @GetMapping
    public R<IPage<ThirdApiConfig>> listConfigs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String apiType) {
        
        Page<ThirdApiConfig> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ThirdApiConfig> wrapper = new LambdaQueryWrapper<>();
        
        if (apiType != null && !apiType.isEmpty()) {
            wrapper.eq(ThirdApiConfig::getApiType, apiType);
        }
        
        wrapper.orderByDesc(ThirdApiConfig::getCreateTime);
        
        IPage<ThirdApiConfig> result = thirdApiConfigMapper.selectPage(page, wrapper);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R<ThirdApiConfig> getConfig(@PathVariable Long id) {
        ThirdApiConfig config = thirdApiConfigMapper.selectById(id);
        return R.ok(config);
    }

    @PostMapping
    public R<Void> saveConfig(@RequestBody ThirdApiConfig config) {
        thirdApiConfigMapper.insert(config);
        return R.ok();
    }

    @PutMapping
    public R<Void> updateConfig(@RequestBody ThirdApiConfig config) {
        thirdApiConfigMapper.updateById(config);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> deleteConfig(@PathVariable Long id) {
        thirdApiConfigMapper.deleteById(id);
        return R.ok();
    }

    @PostMapping("/sync/{id}")
    public R<Void> syncPrice(@PathVariable Long id) {
        log.info("同步第三方价格：configId={}", id);
        return R.ok();
    }
}
