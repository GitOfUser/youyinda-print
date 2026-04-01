package com.youyinda.vo;

import lombok.Data;

/**
 * 管理员登录响应
 */
@Data
public class AdminLoginVO {

    private Long adminId;

    private String username;

    private String realName;

    private String avatar;

    private String roleName;

    private String token;

    private Long expireTime;
}
