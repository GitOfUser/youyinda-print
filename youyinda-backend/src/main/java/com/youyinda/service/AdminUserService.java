package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.dto.AdminLoginRequest;
import com.youyinda.entity.AdminUser;
import com.youyinda.vo.AdminLoginVO;

public interface AdminUserService extends IService<AdminUser> {

    AdminLoginVO login(AdminLoginRequest request);

    AdminUser getByUsername(String username);

    void updateLoginInfo(Long adminId, String ip);
}
