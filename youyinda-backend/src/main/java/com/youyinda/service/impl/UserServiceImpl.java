package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.User;
import com.youyinda.mapper.UserMapper;
import com.youyinda.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户Service实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 根据OpenID获取用户
     * @param openid 微信OpenID
     * @return 用户信息
     */
    @Override
    public User getByOpenid(String openid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     */
    @Override
    public void updateLastLoginTime(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginTime(new Date());
        baseMapper.updateById(user);
    }
}
