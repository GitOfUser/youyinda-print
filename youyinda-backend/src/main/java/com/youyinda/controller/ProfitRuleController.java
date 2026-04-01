package com.youyinda.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyinda.common.R;
import com.youyinda.entity.ProfitRule;
import com.youyinda.mapper.ProfitRuleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/v1/profit-rules")
public class ProfitRuleController {

    @Autowired
    private ProfitRuleMapper profitRuleMapper;

    @GetMapping
    public R<IPage<ProfitRule>> listRules(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String businessType,
            @RequestParam(required = false) String ruleCode) {
        
        Page<ProfitRule> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ProfitRule> wrapper = new LambdaQueryWrapper<>();
        
        if (businessType != null && !businessType.isEmpty()) {
            wrapper.eq(ProfitRule::getBusinessType, businessType);
        }
        if (ruleCode != null && !ruleCode.isEmpty()) {
            wrapper.eq(ProfitRule::getRuleCode, ruleCode);
        }
        
        wrapper.orderByDesc(ProfitRule::getCreateTime);
        
        IPage<ProfitRule> result = profitRuleMapper.selectPage(page, wrapper);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R<ProfitRule> getRule(@PathVariable Long id) {
        ProfitRule rule = profitRuleMapper.selectById(id);
        return R.ok(rule);
    }

    @PostMapping
    public R<Void> saveRule(@RequestBody ProfitRule rule) {
        profitRuleMapper.insert(rule);
        return R.ok();
    }

    @PutMapping
    public R<Void> updateRule(@RequestBody ProfitRule rule) {
        profitRuleMapper.updateById(rule);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> deleteRule(@PathVariable Long id) {
        profitRuleMapper.deleteById(id);
        return R.ok();
    }
}
