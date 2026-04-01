package com.youyinda.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WxPhoneRequest {

    @NotBlank(message = "encryptedData不能为空")
    private String encryptedData;

    @NotBlank(message = "iv不能为空")
    private String iv;

    @NotBlank(message = "sessionKey不能为空")
    private String sessionKey;
}
