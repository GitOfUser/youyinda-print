package com.youyinda.vo;

import lombok.Data;

/**
 * 登录响应VO
 */
@Data
public class LoginVO {
    /**
     * 访问令牌
     */
    private String token;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;

    /**
     * 微信OpenID
     */
    private String openid;
}
