package com.youyinda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.dto.AdminLoginRequest;
import com.youyinda.entity.AdminUser;
import com.youyinda.exception.BusinessException;
import com.youyinda.mapper.AdminUserMapper;
import com.youyinda.service.AdminUserService;
import com.youyinda.util.JwtUtil;
import com.youyinda.vo.AdminLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AdminLoginVO login(AdminLoginRequest request) {
        AdminUser adminUser = getByUsername(request.getUsername());
        if (adminUser == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!passwordEncoder.matches(request.getPassword(), adminUser.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (adminUser.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        String token = jwtUtil.generateToken(adminUser.getId(), "admin");

        AdminLoginVO vo = new AdminLoginVO();
        BeanUtils.copyProperties(adminUser, vo);
        vo.setToken(token);
        vo.setExpireTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000L);

        updateLoginInfo(adminUser.getId(), null);

        return vo;
    }

    @Override
    public AdminUser getByUsername(String username) {
        return lambdaQuery()
                .eq(AdminUser::getUsername, username)
                .one();
    }

    @Override
    public void updateLoginInfo(Long adminId, String ip) {
        AdminUser adminUser = getById(adminId);
        if (adminUser != null) {
            adminUser.setLastLoginTime(new Date());
            adminUser.setLastLoginIp(ip);
            updateById(adminUser);
        }
    }
}
