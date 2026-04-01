package com.youyinda.service.impl;

import com.youyinda.dto.WxPayRequest;
import com.youyinda.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WxPayServiceImpl implements WxPayService {

    @Override
    public WxPayRequest createPayment(Long orderId, String orderType) {
        log.info("创建微信支付请求：orderId={}, orderType={}", orderId, orderType);
        return new WxPayRequest();
    }

    @Override
    public void handlePaymentNotify(String notifyData) {
        log.info("处理微信支付回调通知：notifyData={}", notifyData);
    }

    @Override
    public void closeOrder(Long orderId) {
        log.info("关闭微信支付订单：orderId={}", orderId);
    }

    @Override
    public void refundOrder(Long orderId, String refundReason) {
        log.info("申请微信支付退款：orderId={}, refundReason={}", orderId, refundReason);
    }
}
