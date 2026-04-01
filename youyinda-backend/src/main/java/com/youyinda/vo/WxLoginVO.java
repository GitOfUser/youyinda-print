package com.youyinda.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WxLoginVO {

    private String token;

    private String sessionKey;

    private UserInfoVO userInfo;
}
