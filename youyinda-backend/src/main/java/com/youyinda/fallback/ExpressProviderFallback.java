package com.youyinda.fallback;

import com.youyinda.dto.ExpressCancelOrderRequest;
import com.youyinda.dto.ExpressOrderRequest;
import com.youyinda.dto.ExpressPriceQueryRequest;
import com.youyinda.dto.ExpressTrackQueryRequest;
import com.youyinda.dto.ThirdApiResponse;
import com.youyinda.feign.ExpressProviderFeignClient;
import com.youyinda.vo.ExpressOrderVO;
import com.youyinda.vo.ExpressPriceVO;
import com.youyinda.vo.ExpressTrackVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class ExpressProviderFallback implements ExpressProviderFeignClient {

    @Override
    public ThirdApiResponse<List<ExpressPriceVO>> queryPrice(ExpressPriceQueryRequest request) {
        log.warn("快递服务商查询运费接口熔断，返回空列表");
        return ThirdApiResponse.success(Collections.emptyList());
    }

    public ThirdApiResponse<List<ExpressPriceVO>> queryPriceFallback(ExpressPriceQueryRequest request, Exception e) {
        log.error("快递服务商查询运费接口异常", e);
        return ThirdApiResponse.fail("快递服务暂不可用，请稍后重试");
    }

    @Override
    public ThirdApiResponse<ExpressOrderVO> createOrder(ExpressOrderRequest request) {
        log.warn("快递服务商下单接口熔断，订单号：{}", request.getOrderNo());
        return ThirdApiResponse.fail("快递服务暂不可用，请稍后重试");
    }

    public ThirdApiResponse<ExpressOrderVO> createOrderFallback(ExpressOrderRequest request, Exception e) {
        log.error("快递服务商下单接口异常，订单号：{}", request.getOrderNo(), e);
        return ThirdApiResponse.fail("快递服务暂不可用，请稍后重试");
    }

    @Override
    public ThirdApiResponse<ExpressTrackVO> queryTrack(ExpressTrackQueryRequest request) {
        log.warn("快递服务商查询物流轨迹接口熔断，快递单号：{}", request.getTrackingNo());
        return ThirdApiResponse.fail("快递服务暂不可用，请稍后重试");
    }

    public ThirdApiResponse<ExpressTrackVO> queryTrackFallback(ExpressTrackQueryRequest request, Exception e) {
        log.error("快递服务商查询物流轨迹接口异常，快递单号：{}", request.getTrackingNo(), e);
        return ThirdApiResponse.fail("快递服务暂不可用，请稍后重试");
    }

    @Override
    public ThirdApiResponse<Boolean> cancelOrder(ExpressCancelOrderRequest request) {
        log.warn("快递服务商取消订单接口熔断，订单号：{}", request.getOrderNo());
        return ThirdApiResponse.fail("快递服务暂不可用，请稍后重试");
    }

    public ThirdApiResponse<Boolean> cancelOrderFallback(ExpressCancelOrderRequest request, Exception e) {
        log.error("快递服务商取消订单接口异常，订单号：{}", request.getOrderNo(), e);
        return ThirdApiResponse.fail("快递服务暂不可用，请稍后重试");
    }
}
