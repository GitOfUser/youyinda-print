package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.AdminRole;

import java.util.List;

public interface AdminRoleService extends IService<AdminRole> {

    List<AdminRole> listAllRoles();

    AdminRole getRoleWithPermissions(Long roleId);

    void saveRoleWithPermissions(AdminRole role);

    void deleteRole(Long roleId);
}
