package com.youyinda.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WxLoginRequest {

    @NotBlank(message = "code不能为空")
    private String code;

    private String nickname;

    private String avatar;
}
