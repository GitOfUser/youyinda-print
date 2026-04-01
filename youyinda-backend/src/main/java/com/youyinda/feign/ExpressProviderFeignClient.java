package com.youyinda.feign;

import com.youyinda.dto.ExpressCancelOrderRequest;
import com.youyinda.dto.ExpressOrderRequest;
import com.youyinda.dto.ExpressPriceQueryRequest;
import com.youyinda.dto.ExpressTrackQueryRequest;
import com.youyinda.dto.ThirdApiResponse;
import com.youyinda.fallback.ExpressProviderFallback;
import com.youyinda.vo.ExpressOrderVO;
import com.youyinda.vo.ExpressPriceVO;
import com.youyinda.vo.ExpressTrackVO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "expressProvider", url = "${third-party.express.provider-url:}", fallback = ExpressProviderFallback.class)
public interface ExpressProviderFeignClient {

    @CircuitBreaker(name = "expressProvider", fallbackMethod = "queryPriceFallback")
    @Retry(name = "expressProviderIdempotent")
    @PostMapping("/api/express/price/query")
    ThirdApiResponse<List<ExpressPriceVO>> queryPrice(@RequestBody ExpressPriceQueryRequest request);

    @CircuitBreaker(name = "expressProvider", fallbackMethod = "createOrderFallback")
    @PostMapping("/api/express/order/create")
    ThirdApiResponse<ExpressOrderVO> createOrder(@RequestBody ExpressOrderRequest request);

    @CircuitBreaker(name = "expressProvider", fallbackMethod = "queryTrackFallback")
    @Retry(name = "expressProviderIdempotent")
    @PostMapping("/api/express/track/query")
    ThirdApiResponse<ExpressTrackVO> queryTrack(@RequestBody ExpressTrackQueryRequest request);

    @CircuitBreaker(name = "expressProvider", fallbackMethod = "cancelOrderFallback")
    @PostMapping("/api/express/order/cancel")
    ThirdApiResponse<Boolean> cancelOrder(@RequestBody ExpressCancelOrderRequest request);
}
