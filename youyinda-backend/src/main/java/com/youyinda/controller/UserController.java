package com.youyinda.controller;

import com.youyinda.common.BusinessException;
import com.youyinda.common.R;
import com.youyinda.entity.User;
import com.youyinda.service.UserService;
import com.youyinda.util.JwtUtil;
import com.youyinda.vo.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     * @return 用户信息
     */
    @GetMapping("/info")
    public R<UserInfoVO> getUserInfo() {
        // 获取当前用户ID
        Long userId = JwtUtil.getUserIdFromToken();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }

        // 查询用户信息
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(400, "用户不存在");
        }

        // 构建返回结果
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        // 手机号脱敏
        if (user.getPhone() != null && user.getPhone().length() >= 11) {
            userInfoVO.setPhone(user.getPhone().substring(0, 3) + "****" + user.getPhone().substring(7));
        }

        return R.success(userInfoVO);
    }

    /**
     * 更新用户信息
     * @param userInfoVO 用户信息
     * @return 更新结果
     */
    @PutMapping("/info")
    public R<?> updateUserInfo(@RequestBody UserInfoVO userInfoVO) {
        // 获取当前用户ID
        Long userId = JwtUtil.getUserIdFromToken();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }

        // 查询用户信息
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(400, "用户不存在");
        }

        // 更新用户信息
        if (userInfoVO.getNickName() != null) {
            user.setNickName(userInfoVO.getNickName());
        }
        if (userInfoVO.getAvatarUrl() != null) {
            user.setAvatarUrl(userInfoVO.getAvatarUrl());
        }
        if (userInfoVO.getGender() != null) {
            user.setGender(userInfoVO.getGender());
        }
        user.setUpdateTime(new Date());

        userService.updateById(user);

        return R.success();
    }
}
