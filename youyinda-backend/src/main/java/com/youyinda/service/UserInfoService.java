package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.UserInfo;

public interface UserInfoService extends IService<UserInfo> {

    UserInfo getByOpenid(String openid);

    UserInfo registerOrUpdate(String openid, String unionid, String nickname, String avatar);

    void updatePhone(Long userId, String phone);
}
