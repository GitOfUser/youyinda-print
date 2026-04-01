package com.youyinda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.AdminPermission;
import com.youyinda.mapper.AdminPermissionMapper;
import com.youyinda.service.AdminPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminPermissionServiceImpl extends ServiceImpl<AdminPermissionMapper, AdminPermission> implements AdminPermissionService {

    @Override
    public List<AdminPermission> listAllPermissions() {
        return lambdaQuery()
                .eq(AdminPermission::getStatus, 1)
                .orderByAsc(AdminPermission::getSortOrder)
                .list();
    }

    @Override
    public List<AdminPermission> listPermissionsByRole(Long roleId) {
        return listAllPermissions();
    }

    @Override
    public List<AdminPermission> buildPermissionTree(List<AdminPermission> permissions) {
        List<AdminPermission> tree = new ArrayList<>();
        List<AdminPermission> roots = permissions.stream()
                .filter(p -> p.getParentId() == null || p.getParentId() == 0)
                .collect(Collectors.toList());

        for (AdminPermission root : roots) {
            List<AdminPermission> children = permissions.stream()
                    .filter(p -> root.getId().equals(p.getParentId()))
                    .collect(Collectors.toList());
            root.setChildren(children);
            tree.add(root);
        }

        return tree;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePermission(AdminPermission permission) {
        if (permission.getId() == null) {
            save(permission);
        } else {
            updateById(permission);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long permissionId) {
        removeById(permissionId);
    }
}
