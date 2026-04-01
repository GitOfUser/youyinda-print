package com.youyinda.controller;

import com.youyinda.common.BusinessException;
import com.youyinda.common.R;
import com.youyinda.dto.UserAddressDTO;
import com.youyinda.entity.UserAddress;
import com.youyinda.service.UserAddressService;
import com.youyinda.util.JwtUtil;
import com.youyinda.vo.UserAddressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户地址控制器
 */
@RestController
@RequestMapping("/api/v1/user/address")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 获取地址列表
     * @return 地址列表
     */
    @GetMapping("/list")
    public R<List<UserAddressVO>> getAddressList() {
        // 获取当前用户ID
        Long userId = JwtUtil.getUserIdFromToken();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }

        // 查询地址列表
        List<UserAddress> addressList = userAddressService.getByUserId(userId);

        // 构建返回结果
        List<UserAddressVO> addressVOList = new ArrayList<>();
        for (UserAddress address : addressList) {
            UserAddressVO addressVO = new UserAddressVO();
            BeanUtils.copyProperties(address, addressVO);
            // 手机号脱敏
            if (address.getPhone() != null && address.getPhone().length() >= 11) {
                addressVO.setPhone(address.getPhone().substring(0, 3) + "****" + address.getPhone().substring(7));
            }
            addressVOList.add(addressVO);
        }

        return R.success(addressVOList);
    }

    /**
     * 获取默认地址
     * @return 默认地址
     */
    @GetMapping("/default")
    public R<UserAddressVO> getDefaultAddress() {
        // 获取当前用户ID
        Long userId = JwtUtil.getUserIdFromToken();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }

        // 查询默认地址
        UserAddress address = userAddressService.getDefaultByUserId(userId);
        if (address == null) {
            return R.success(null);
        }

        // 构建返回结果
        UserAddressVO addressVO = new UserAddressVO();
        BeanUtils.copyProperties(address, addressVO);
        // 手机号脱敏
        if (address.getPhone() != null && address.getPhone().length() >= 11) {
            addressVO.setPhone(address.getPhone().substring(0, 3) + "****" + address.getPhone().substring(7));
        }

        return R.success(addressVO);
    }

    /**
     * 添加地址
     * @param addressDTO 地址信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public R<?> addAddress(@Valid @RequestBody UserAddressDTO addressDTO) {
        // 获取当前用户ID
        Long userId = JwtUtil.getUserIdFromToken();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }

        // 创建地址
        UserAddress address = new UserAddress();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(userId);
        address.setCreateTime(new Date());
        address.setUpdateTime(new Date());
        address.setIsDelete(0);

        // 如果是默认地址，先将其他地址设为非默认
        if (addressDTO.getIsDefault() == 1) {
            userAddressService.setDefaultAddress(userId, null);
        }

        userAddressService.save(address);

        return R.success();
    }

    /**
     * 更新地址
     * @param addressDTO 地址信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public R<?> updateAddress(@Valid @RequestBody UserAddressDTO addressDTO) {
        // 获取当前用户ID
        Long userId = JwtUtil.getUserIdFromToken();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }

        // 查询地址
        UserAddress address = userAddressService.getById(addressDTO.getId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(400, "地址不存在");
        }

        // 更新地址
        BeanUtils.copyProperties(addressDTO, address);
        address.setUpdateTime(new Date());

        // 如果是默认地址，先将其他地址设为非默认
        if (addressDTO.getIsDefault() == 1) {
            userAddressService.setDefaultAddress(userId, addressDTO.getId());
        }

        userAddressService.updateById(address);

        return R.success();
    }

    /**
     * 删除地址
     * @param id 地址ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public R<?> deleteAddress(@PathVariable Long id) {
        // 获取当前用户ID
        Long userId = JwtUtil.getUserIdFromToken();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }

        // 查询地址
        UserAddress address = userAddressService.getById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(400, "地址不存在");
        }

        // 删除地址
        userAddressService.removeById(id);

        return R.success();
    }

    /**
     * 设置默认地址
     * @param id 地址ID
     * @return 设置结果
     */
    @PutMapping("/set-default/{id}")
    public R<?> setDefaultAddress(@PathVariable Long id) {
        // 获取当前用户ID
        Long userId = JwtUtil.getUserIdFromToken();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }

        // 查询地址
        UserAddress address = userAddressService.getById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(400, "地址不存在");
        }

        // 设置默认地址
        userAddressService.setDefaultAddress(userId, id);

        return R.success();
    }
}
