package com.youyinda.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyinda.entity.OrderLogistics;
import com.youyinda.entity.OrderMain;
import com.youyinda.enums.OrderStatusEnum;
import com.youyinda.service.OrderLogisticsService;
import com.youyinda.service.OrderMainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ExpressTrackSyncTask {

    @Autowired
    private OrderLogisticsService orderLogisticsService;

    @Autowired
    private OrderMainService orderMainService;

    private static final List<String> SYNC_STATUS_LIST = Arrays.asList(
            OrderStatusEnum.PAID.getCode(),
            OrderStatusEnum.PRINTING.getCode(),
            OrderStatusEnum.PRINTED.getCode(),
            OrderStatusEnum.SHIPPED.getCode()
    );

    @Scheduled(cron = "0 */30 * * * ?")
    public void syncExpressTrack() {
        log.info("开始同步快递物流状态...");
        long startTime = System.currentTimeMillis();

        try {
            LambdaQueryWrapper<OrderMain> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderMain::getOrderType, "express")
                    .in(OrderMain::getOrderStatus, SYNC_STATUS_LIST);
            List<OrderMain> orderList = orderMainService.list(wrapper);

            log.info("需要同步物流状态的订单数量：{}", orderList.size());

            for (OrderMain order : orderList) {
                try {
                    orderLogisticsService.syncLogisticsStatus(order.getId());
                } catch (Exception e) {
                    log.error("同步订单物流状态失败：orderId={}", order.getId(), e);
                }
            }

            long cost = System.currentTimeMillis() - startTime;
            log.info("快递物流状态同步完成，耗时：{}ms", cost);
        } catch (Exception e) {
            log.error("快递物流状态同步异常", e);
        }
    }
}
