package com.youyinda.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 手机号更新请求DTO
 */
@Data
public class PhoneDTO {
    /**
     * 加密数据
     */
    @NotBlank(message = "encryptedData不能为空")
    private String encryptedData;

    /**
     * 加密算法的初始向量
     */
    @NotBlank(message = "iv不能为空")
    private String iv;

    /**
     * 会话密钥
     */
    @NotBlank(message = "sessionKey不能为空")
    private String sessionKey;
}
