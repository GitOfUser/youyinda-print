package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.UserAddress;
import com.youyinda.mapper.UserAddressMapper;
import com.youyinda.service.UserAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户地址Service实现类
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    /**
     * 根据用户ID获取地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    @Override
    public List<UserAddress> getByUserId(Long userId) {
        QueryWrapper<UserAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("is_default");
        queryWrapper.orderByDesc("create_time");
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据用户ID获取默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    @Override
    public UserAddress getDefaultByUserId(Long userId) {
        QueryWrapper<UserAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_default", 1);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 设置默认地址
     * @param userId 用户ID
     * @param addressId 地址ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long addressId) {
        // 先将用户所有地址设为非默认
        UserAddress update = new UserAddress();
        update.setIsDefault(0);
        QueryWrapper<UserAddress> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("user_id", userId);
        baseMapper.update(update, updateWrapper);

        // 将指定地址设为默认
        UserAddress defaultAddress = new UserAddress();
        defaultAddress.setId(addressId);
        defaultAddress.setIsDefault(1);
        baseMapper.updateById(defaultAddress);
    }
}
