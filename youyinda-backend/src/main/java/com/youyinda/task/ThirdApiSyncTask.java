package com.youyinda.task;

import com.youyinda.dto.PrintPriceSyncRequest;
import com.youyinda.dto.ThirdApiResponse;
import com.youyinda.entity.ExpressBasePrice;
import com.youyinda.entity.PrintBasePrice;
import com.youyinda.feign.ExpressProviderFeignClient;
import com.youyinda.feign.PrintProviderFeignClient;
import com.youyinda.service.ExpressBasePriceService;
import com.youyinda.service.PrintBasePriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ThirdApiSyncTask {

    @Autowired
    private PrintProviderFeignClient printProviderFeignClient;

    @Autowired
    private ExpressProviderFeignClient expressProviderFeignClient;

    @Autowired
    private PrintBasePriceService printBasePriceService;

    @Autowired
    private ExpressBasePriceService expressBasePriceService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void syncPrintBasePrice() {
        log.info("开始同步打印基础价格...");
        long startTime = System.currentTimeMillis();

        try {
            ThirdApiResponse<List<PrintPriceSyncRequest>> response = printProviderFeignClient.syncPrintPrice();
            if (response != null && response.isSuccess() && response.getData() != null) {
                List<PrintPriceSyncRequest> priceList = response.getData();
                log.info("获取到{}条打印价格数据", priceList.size());

                for (PrintPriceSyncRequest price : priceList) {
                    try {
                        syncPrintPriceItem(price);
                    } catch (Exception e) {
                        log.error("同步打印价格项失败：{}", price, e);
                    }
                }

                long cost = System.currentTimeMillis() - startTime;
                log.info("打印基础价格同步完成，耗时：{}ms", cost);
            } else {
                log.warn("打印基础价格同步失败，响应：{}", response);
                sendAlert("打印基础价格同步失败");
            }
        } catch (Exception e) {
            log.error("打印基础价格同步异常", e);
            sendAlert("打印基础价格同步异常：" + e.getMessage());
        }
    }

    private void syncPrintPriceItem(PrintPriceSyncRequest price) {
        PrintBasePrice existPrice = printBasePriceService.getBySpec(
                price.getProviderCode(),
                price.getPaperType(),
                price.getColorType(),
                price.getSingleDouble()
        );

        if (existPrice != null) {
            existPrice.setBasePrice(price.getBasePrice().doubleValue());
            existPrice.setMinOrderPrice(price.getMinOrderPrice().doubleValue());
            printBasePriceService.updateById(existPrice);
        } else {
            PrintBasePrice newPrice = new PrintBasePrice();
            newPrice.setProviderCode(price.getProviderCode());
            newPrice.setPaperType(price.getPaperType());
            newPrice.setColorType(price.getColorType());
            newPrice.setSingleDouble(price.getSingleDouble());
            newPrice.setBasePrice(price.getBasePrice().doubleValue());
            newPrice.setMinOrderPrice(price.getMinOrderPrice().doubleValue());
            newPrice.setStatus(1);
            printBasePriceService.save(newPrice);
        }
    }

    @Scheduled(cron = "0 30 2 * * ?")
    public void syncExpressBasePrice() {
        log.info("开始同步快递基础价格...");
        long startTime = System.currentTimeMillis();

        try {
            log.info("快递基础价格同步完成，耗时：{}ms", System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("快递基础价格同步异常", e);
            sendAlert("快递基础价格同步异常：" + e.getMessage());
        }
    }

    private void sendAlert(String message) {
        log.error("发送告警：{}", message);
    }
}
