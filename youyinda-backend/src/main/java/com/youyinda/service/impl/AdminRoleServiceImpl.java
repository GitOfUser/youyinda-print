package com.youyinda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.AdminRole;
import com.youyinda.mapper.AdminRoleMapper;
import com.youyinda.service.AdminRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {

    @Override
    public List<AdminRole> listAllRoles() {
        return lambdaQuery()
                .eq(AdminRole::getStatus, 1)
                .orderByAsc(AdminRole::getCreateTime)
                .list();
    }

    @Override
    public AdminRole getRoleWithPermissions(Long roleId) {
        return getById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleWithPermissions(AdminRole role) {
        if (role.getId() == null) {
            save(role);
        } else {
            updateById(role);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        removeById(roleId);
    }
}
