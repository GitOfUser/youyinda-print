package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.UserInfo;
import com.youyinda.mapper.UserInfoMapper;
import com.youyinda.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Override
    public UserInfo getByOpenid(String openid) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getOpenid, openid);
        return this.getOne(wrapper);
    }

    @Override
    public UserInfo registerOrUpdate(String openid, String unionid, String nickname, String avatar) {
        UserInfo existUser = getByOpenid(openid);
        Date now = new Date();

        if (existUser != null) {
            existUser.setNickname(nickname);
            existUser.setAvatar(avatar);
            if (unionid != null) {
                existUser.setUnionid(unionid);
            }
            existUser.setLastLoginTime(now);
            this.updateById(existUser);
            log.info("用户登录更新：userId={}", existUser.getId());
            return existUser;
        } else {
            UserInfo newUser = new UserInfo();
            newUser.setOpenid(openid);
            newUser.setUnionid(unionid);
            newUser.setNickname(nickname);
            newUser.setAvatar(avatar);
            newUser.setGender(0);
            newUser.setBalance(BigDecimal.ZERO);
            newUser.setTotalPoints(0);
            newUser.setStatus(1);
            newUser.setLastLoginTime(now);
            newUser.setIsDelete(0);
            this.save(newUser);
            log.info("新用户注册：userId={}", newUser.getId());
            return newUser;
        }
    }

    @Override
    public void updatePhone(Long userId, String phone) {
        UserInfo userInfo = this.getById(userId);
        if (userInfo != null) {
            userInfo.setPhone(phone);
            this.updateById(userInfo);
            log.info("用户手机号更新：userId={}", userId);
        }
    }
}
