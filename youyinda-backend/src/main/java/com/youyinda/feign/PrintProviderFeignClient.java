package com.youyinda.feign;

import com.youyinda.dto.PrintCancelOrderRequest;
import com.youyinda.dto.PrintOrderRequest;
import com.youyinda.dto.PrintOrderStatusRequest;
import com.youyinda.dto.PrintPriceSyncRequest;
import com.youyinda.dto.ThirdApiResponse;
import com.youyinda.fallback.PrintProviderFallback;
import com.youyinda.vo.PrintOrderVO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "printProvider", url = "${third-party.print.provider-url:}", fallback = PrintProviderFallback.class)
public interface PrintProviderFeignClient {

    @CircuitBreaker(name = "printProvider", fallbackMethod = "syncPrintPriceFallback")
    @Retry(name = "printProviderIdempotent")
    @PostMapping("/api/print/price/sync")
    ThirdApiResponse<List<PrintPriceSyncRequest>> syncPrintPrice();

    @CircuitBreaker(name = "printProvider", fallbackMethod = "createOrderFallback")
    @PostMapping("/api/print/order/create")
    ThirdApiResponse<PrintOrderVO> createOrder(@RequestBody PrintOrderRequest request);

    @CircuitBreaker(name = "printProvider", fallbackMethod = "getOrderStatusFallback")
    @Retry(name = "printProviderIdempotent")
    @PostMapping("/api/print/order/status")
    ThirdApiResponse<PrintOrderVO> getOrderStatus(@RequestBody PrintOrderStatusRequest request);

    @CircuitBreaker(name = "printProvider", fallbackMethod = "cancelOrderFallback")
    @PostMapping("/api/print/order/cancel")
    ThirdApiResponse<Boolean> cancelOrder(@RequestBody PrintCancelOrderRequest request);
}
