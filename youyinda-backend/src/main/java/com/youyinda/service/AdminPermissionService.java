package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.AdminPermission;

import java.util.List;

public interface AdminPermissionService extends IService<AdminPermission> {

    List<AdminPermission> listAllPermissions();

    List<AdminPermission> listPermissionsByRole(Long roleId);

    List<AdminPermission> buildPermissionTree(List<AdminPermission> permissions);

    void savePermission(AdminPermission permission);

    void deletePermission(Long permissionId);
}
