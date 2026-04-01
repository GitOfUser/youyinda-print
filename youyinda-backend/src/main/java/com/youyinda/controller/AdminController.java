package com.youyinda.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youyinda.common.R;
import com.youyinda.dto.AdminLoginRequest;
import com.youyinda.entity.AdminPermission;
import com.youyinda.entity.AdminRole;
import com.youyinda.entity.AdminUser;
import com.youyinda.service.AdminPermissionService;
import com.youyinda.service.AdminRoleService;
import com.youyinda.service.AdminUserService;
import com.youyinda.vo.AdminLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/v1")
public class AdminController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AdminRoleService adminRoleService;

    @Autowired
    private AdminPermissionService adminPermissionService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public R<AdminLoginVO> login(@Validated @RequestBody AdminLoginRequest request) {
        log.info("管理员登录：username={}", request.getUsername());
        AdminLoginVO vo = adminUserService.login(request);
        return R.ok(vo);
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        log.info("管理员登出");
        return R.ok();
    }

    @GetMapping("/info")
    public R<AdminUser> getInfo() {
        Long adminId = 1L;
        AdminUser admin = adminUserService.getById(adminId);
        admin.setPassword(null);
        return R.ok(admin);
    }

    @GetMapping("/user/list")
    public R<IPage<AdminUser>> listUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username) {
        
        IPage<AdminUser> page = adminUserService.lambdaQuery()
                .like(username != null && !username.isEmpty(), AdminUser::getUsername, username)
                .orderByDesc(AdminUser::getCreateTime)
                .page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize));
        
        page.getRecords().forEach(user -> user.setPassword(null));
        return R.ok(page);
    }

    @PostMapping("/user")
    public R<Void> saveUser(@Validated @RequestBody AdminUser user) {
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        adminUserService.save(user);
        return R.ok();
    }

    @PutMapping("/user")
    public R<Void> updateUser(@Validated @RequestBody AdminUser user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        adminUserService.updateById(user);
        return R.ok();
    }

    @DeleteMapping("/user/{id}")
    public R<Void> deleteUser(@PathVariable Long id) {
        adminUserService.removeById(id);
        return R.ok();
    }

    @GetMapping("/role/list")
    public R<List<AdminRole>> listRoles() {
        List<AdminRole> roles = adminRoleService.listAllRoles();
        return R.ok(roles);
    }

    @PostMapping("/role")
    public R<Void> saveRole(@Validated @RequestBody AdminRole role) {
        adminRoleService.saveRoleWithPermissions(role);
        return R.ok();
    }

    @DeleteMapping("/role/{id}")
    public R<Void> deleteRole(@PathVariable Long id) {
        adminRoleService.deleteRole(id);
        return R.ok();
    }

    @GetMapping("/permission/list")
    public R<List<AdminPermission>> listPermissions() {
        List<AdminPermission> all = adminPermissionService.listAllPermissions();
        List<AdminPermission> tree = adminPermissionService.buildPermissionTree(all);
        return R.ok(tree);
    }

    @PostMapping("/permission")
    public R<Void> savePermission(@Validated @RequestBody AdminPermission permission) {
        adminPermissionService.savePermission(permission);
        return R.ok();
    }

    @DeleteMapping("/permission/{id}")
    public R<Void> deletePermission(@PathVariable Long id) {
        adminPermissionService.deletePermission(id);
        return R.ok();
    }
}
