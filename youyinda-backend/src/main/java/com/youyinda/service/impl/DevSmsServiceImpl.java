package com.youyinda.service.impl;

import com.youyinda.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 开发模式短信服务实现
 * <p>
 * 仅将验证码打印到日志，便于本地联调，不产生真实短信费用。
 * 【上线前必改】将此实现替换为阿里云/腾讯云等真实短信服务，并移除
 * {@code sms.dev-mode=true} 配置，由真实 SmsService 实现承载生产流量。
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "sms.dev-mode", havingValue = "true", matchIfMissing = true)
public class DevSmsServiceImpl implements SmsService {

    @Override
    public void sendSmsCode(String phone, String code) {
        log.info("[DEV-SMS] 发送短信验证码 -> 手机号: {}, 验证码: {}", phone, code);
    }
}
