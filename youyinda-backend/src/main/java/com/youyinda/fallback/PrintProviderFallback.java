package com.youyinda.fallback;

import com.youyinda.dto.PrintCancelOrderRequest;
import com.youyinda.dto.PrintOrderRequest;
import com.youyinda.dto.PrintOrderStatusRequest;
import com.youyinda.dto.PrintPriceSyncRequest;
import com.youyinda.dto.ThirdApiResponse;
import com.youyinda.feign.PrintProviderFeignClient;
import com.youyinda.vo.PrintOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class PrintProviderFallback implements PrintProviderFeignClient {

    @Override
    public ThirdApiResponse<List<PrintPriceSyncRequest>> syncPrintPrice() {
        log.warn("打印服务商同步价格接口熔断，返回空列表");
        return ThirdApiResponse.success(Collections.emptyList());
    }

    public ThirdApiResponse<List<PrintPriceSyncRequest>> syncPrintPriceFallback(Exception e) {
        log.error("打印服务商同步价格接口异常", e);
        return ThirdApiResponse.fail("打印服务暂不可用，请稍后重试");
    }

    @Override
    public ThirdApiResponse<PrintOrderVO> createOrder(PrintOrderRequest request) {
        log.warn("打印服务商下单接口熔断，订单号：{}", request.getOrderNo());
        return ThirdApiResponse.fail("打印服务暂不可用，请稍后重试");
    }

    public ThirdApiResponse<PrintOrderVO> createOrderFallback(PrintOrderRequest request, Exception e) {
        log.error("打印服务商下单接口异常，订单号：{}", request.getOrderNo(), e);
        return ThirdApiResponse.fail("打印服务暂不可用，请稍后重试");
    }

    @Override
    public ThirdApiResponse<PrintOrderVO> getOrderStatus(PrintOrderStatusRequest request) {
        log.warn("打印服务商查询订单状态接口熔断，订单号：{}", request.getOrderNo());
        return ThirdApiResponse.fail("打印服务暂不可用，请稍后重试");
    }

    public ThirdApiResponse<PrintOrderVO> getOrderStatusFallback(PrintOrderStatusRequest request, Exception e) {
        log.error("打印服务商查询订单状态接口异常，订单号：{}", request.getOrderNo(), e);
        return ThirdApiResponse.fail("打印服务暂不可用，请稍后重试");
    }

    @Override
    public ThirdApiResponse<Boolean> cancelOrder(PrintCancelOrderRequest request) {
        log.warn("打印服务商取消订单接口熔断，订单号：{}", request.getOrderNo());
        return ThirdApiResponse.fail("打印服务暂不可用，请稍后重试");
    }

    public ThirdApiResponse<Boolean> cancelOrderFallback(PrintCancelOrderRequest request, Exception e) {
        log.error("打印服务商取消订单接口异常，订单号：{}", request.getOrderNo(), e);
        return ThirdApiResponse.fail("打印服务暂不可用，请稍后重试");
    }
}
