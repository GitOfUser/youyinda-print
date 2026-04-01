package com.youyinda.service;

import com.youyinda.dto.WxPayRequest;

public interface WxPayService {

    WxPayRequest createPayment(Long orderId, String orderType);

    void handlePaymentNotify(String notifyData);

    void closeOrder(Long orderId);

    void refundOrder(Long orderId, String refundReason);
}
