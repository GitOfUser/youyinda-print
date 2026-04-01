package com.youyinda.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求DTO
 */
@Data
public class LoginDTO {
    /**
     * 微信登录code
     */
    @NotBlank(message = "code不能为空")
    private String code;

    /**
     * 用户信息
     */
    private UserInfoDTO userInfo;
}
