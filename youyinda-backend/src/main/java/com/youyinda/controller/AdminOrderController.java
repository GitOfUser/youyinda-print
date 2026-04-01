package com.youyinda.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youyinda.common.R;
import com.youyinda.entity.OrderMain;
import com.youyinda.service.OrderMainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/v1/orders")
public class AdminOrderController {

    @Autowired
    private OrderMainService orderMainService;

    @GetMapping
    public R<IPage<OrderMain>> listOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String orderType,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) Long userId) {
        
        IPage<OrderMain> page = orderMainService.lambdaQuery()
                .like(orderNo != null && !orderNo.isEmpty(), OrderMain::getOrderNo, orderNo)
                .eq(orderType != null && !orderType.isEmpty(), OrderMain::getOrderType, orderType)
                .eq(orderStatus != null && !orderStatus.isEmpty(), OrderMain::getOrderStatus, orderStatus)
                .eq(userId != null, OrderMain::getUserId, userId)
                .orderByDesc(OrderMain::getCreateTime)
                .page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize));
        
        return R.ok(page);
    }

    @GetMapping("/{id}")
    public R<OrderMain> getOrderDetail(@PathVariable Long id) {
        OrderMain order = orderMainService.getById(id);
        return R.ok(order);
    }

    @PutMapping("/{id}/status")
    public R<Void> updateOrderStatus(@PathVariable Long id, @RequestParam String orderStatus) {
        OrderMain order = orderMainService.getById(id);
        order.setOrderStatus(orderStatus);
        orderMainService.updateById(order);
        return R.ok();
    }

    @PostMapping("/{id}/refund")
    public R<Void> refundOrder(@PathVariable Long id, @RequestParam String reason) {
        log.info("管理员审核退款：orderId={}, reason={}", id, reason);
        return R.ok();
    }
}
