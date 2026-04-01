package com.youyinda.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyinda.common.R;
import com.youyinda.entity.UserInfo;
import com.youyinda.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/v1/users")
public class AdminUserController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping
    public R<IPage<UserInfo>> listUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String phone) {
        
        Page<UserInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (nickname != null && !nickname.isEmpty()) {
            wrapper.like(UserInfo::getNickname, nickname);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.eq(UserInfo::getPhone, phone);
        }
        
        wrapper.orderByDesc(UserInfo::getCreateTime);
        
        IPage<UserInfo> result = userInfoService.page(page, wrapper);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R<UserInfo> getUser(@PathVariable Long id) {
        UserInfo user = userInfoService.getById(id);
        return R.ok(user);
    }

    @PutMapping("/{id}/status")
    public R<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        UserInfo user = userInfoService.getById(id);
        user.setStatus(status);
        userInfoService.updateById(user);
        return R.ok();
    }
}
