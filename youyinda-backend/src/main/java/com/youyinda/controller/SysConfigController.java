package com.youyinda.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyinda.common.R;
import com.youyinda.entity.SysConfig;
import com.youyinda.mapper.SysConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/v1/sys-config")
public class SysConfigController {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @GetMapping
    public R<IPage<SysConfig>> listConfigs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String configType) {
        
        Page<SysConfig> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        
        if (configType != null && !configType.isEmpty()) {
            wrapper.like(SysConfig::getConfigType, configType);
        }
        
        wrapper.orderByDesc(SysConfig::getCreateTime);
        
        IPage<SysConfig> result = sysConfigMapper.selectPage(page, wrapper);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R<SysConfig> getConfig(@PathVariable Long id) {
        SysConfig config = sysConfigMapper.selectById(id);
        return R.ok(config);
    }

    @PostMapping
    public R<Void> saveConfig(@RequestBody SysConfig config) {
        sysConfigMapper.insert(config);
        return R.ok();
    }

    @PutMapping
    public R<Void> updateConfig(@RequestBody SysConfig config) {
        sysConfigMapper.updateById(config);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> deleteConfig(@PathVariable Long id) {
        sysConfigMapper.deleteById(id);
        return R.ok();
    }
}
