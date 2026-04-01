package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.UserAddress;

import java.util.List;

/**
 * 用户地址Service接口
 */
public interface UserAddressService extends IService<UserAddress> {
    /**
     * 根据用户ID获取地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    List<UserAddress> getByUserId(Long userId);

    /**
     * 根据用户ID获取默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    UserAddress getDefaultByUserId(Long userId);

    /**
     * 设置默认地址
     * @param userId 用户ID
     * @param addressId 地址ID
     */
    void setDefaultAddress(Long userId, Long addressId);
}
