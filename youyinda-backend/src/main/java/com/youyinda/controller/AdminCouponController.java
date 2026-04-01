package com.youyinda.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyinda.common.R;
import com.youyinda.entity.CouponInfo;
import com.youyinda.mapper.CouponMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/v1/coupons")
public class AdminCouponController {

    @Autowired
    private CouponMapper couponMapper;

    @GetMapping
    public R<IPage<CouponInfo>> listCoupons(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String couponName) {
        
        Page<CouponInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CouponInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (couponName != null && !couponName.isEmpty()) {
            wrapper.like(CouponInfo::getCouponName, couponName);
        }
        
        wrapper.orderByDesc(CouponInfo::getCreateTime);
        
        IPage<CouponInfo> result = couponMapper.selectPage(page, wrapper);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R<CouponInfo> getCoupon(@PathVariable Long id) {
        CouponInfo coupon = couponMapper.selectById(id);
        return R.ok(coupon);
    }

    @PostMapping
    public R<Void> saveCoupon(@RequestBody CouponInfo coupon) {
        couponMapper.insert(coupon);
        return R.ok();
    }

    @PutMapping
    public R<Void> updateCoupon(@RequestBody CouponInfo coupon) {
        couponMapper.updateById(coupon);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> deleteCoupon(@PathVariable Long id) {
        couponMapper.deleteById(id);
        return R.ok();
    }
}
