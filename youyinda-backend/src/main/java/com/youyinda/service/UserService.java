package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.User;

/**
 * 用户Service接口
 */
public interface UserService extends IService<User> {
    /**
     * 根据OpenID获取用户
     * @param openid 微信OpenID
     * @return 用户信息
     */
    User getByOpenid(String openid);

    /**
     * 根据手机号获取用户
     * @param phone 手机号
     * @return 用户信息
     */
    User getByPhone(String phone);

    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     */
    void updateLastLoginTime(Long userId);
}
