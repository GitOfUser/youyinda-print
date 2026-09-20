package com.youyinda.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyinda.common.R;
import com.youyinda.entity.ExpressBasePrice;
import com.youyinda.entity.PrintBasePrice;
import com.youyinda.mapper.ExpressBasePriceMapper;
import com.youyinda.mapper.PrintBasePriceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端价格配置控制器（打印价格 + 快递价格）
 */
@Slf4j
@RestController
@RequestMapping("/admin/v1/prices")
public class AdminPriceController {

    @Autowired
    private PrintBasePriceMapper printBasePriceMapper;

    @Autowired
    private ExpressBasePriceMapper expressBasePriceMapper;

    // ==================== 打印价格 ====================

    @GetMapping("/print")
    public R<IPage<PrintBasePrice>> listPrintPrices(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String paperType,
            @RequestParam(required = false) String colorType) {
        Page<PrintBasePrice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PrintBasePrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(paperType != null && !paperType.isEmpty(), PrintBasePrice::getPaperType, paperType)
                .eq(colorType != null && !colorType.isEmpty(), PrintBasePrice::getColorType, colorType)
                .orderByAsc(PrintBasePrice::getId);
        return R.ok(printBasePriceMapper.selectPage(page, wrapper));
    }

    @GetMapping("/print/{id}")
    public R<PrintBasePrice> getPrintPrice(@PathVariable Long id) {
        return R.ok(printBasePriceMapper.selectById(id));
    }

    @PostMapping("/print")
    public R<Void> savePrintPrice(@RequestBody PrintBasePrice price) {
        if (price.getStatus() == null) {
            price.setStatus(1);
        }
        if (price.getIsActive() == null) {
            price.setIsActive(1);
        }
        if (price.getThirdProvider() == null || price.getThirdProvider().isEmpty()) {
            price.setThirdProvider("yilianyun");
        }
        printBasePriceMapper.insert(price);
        return R.ok();
    }

    @PutMapping("/print")
    public R<Void> updatePrintPrice(@RequestBody PrintBasePrice price) {
        printBasePriceMapper.updateById(price);
        return R.ok();
    }

    @DeleteMapping("/print/{id}")
    public R<Void> deletePrintPrice(@PathVariable Long id) {
        printBasePriceMapper.deleteById(id);
        return R.ok();
    }

    // ==================== 快递价格 ====================

    @GetMapping("/express")
    public R<IPage<ExpressBasePrice>> listExpressPrices(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String courier) {
        Page<ExpressBasePrice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ExpressBasePrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(courier != null && !courier.isEmpty(), ExpressBasePrice::getCourier, courier)
                .orderByAsc(ExpressBasePrice::getId);
        return R.ok(expressBasePriceMapper.selectPage(page, wrapper));
    }

    @GetMapping("/express/{id}")
    public R<ExpressBasePrice> getExpressPrice(@PathVariable Long id) {
        return R.ok(expressBasePriceMapper.selectById(id));
    }

    @PostMapping("/express")
    public R<Void> saveExpressPrice(@RequestBody ExpressBasePrice price) {
        if (price.getIsActive() == null) {
            price.setIsActive(1);
        }
        if (price.getFromProvince() == null || price.getFromProvince().isEmpty()) {
            price.setFromProvince("广东省");
        }
        if (price.getToProvince() == null || price.getToProvince().isEmpty()) {
            price.setToProvince("广东省");
        }
        if (price.getWeightCeiling() == null) {
            price.setWeightCeiling(30.0);
        }
        if (price.getThirdProvider() == null || price.getThirdProvider().isEmpty()) {
            price.setThirdProvider("kuaidi100");
        }
        expressBasePriceMapper.insert(price);
        return R.ok();
    }

    @PutMapping("/express")
    public R<Void> updateExpressPrice(@RequestBody ExpressBasePrice price) {
        expressBasePriceMapper.updateById(price);
        return R.ok();
    }

    @DeleteMapping("/express/{id}")
    public R<Void> deleteExpressPrice(@PathVariable Long id) {
        expressBasePriceMapper.deleteById(id);
        return R.ok();
    }
}
