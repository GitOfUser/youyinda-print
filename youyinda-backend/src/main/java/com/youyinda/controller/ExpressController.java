package com.youyinda.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youyinda.common.R;
import com.youyinda.config.UserContext;
import com.youyinda.dto.ExpressOrderCreateRequest;
import com.youyinda.dto.ExpressPriceQueryRequest;
import com.youyinda.entity.ExpressCompany;
import com.youyinda.service.ExpressCompanyService;
import com.youyinda.service.ExpressOrderService;
import com.youyinda.vo.ExpressCompanyVO;
import com.youyinda.vo.ExpressOrderVO;
import com.youyinda.vo.ExpressPriceCalcVO;
import com.youyinda.vo.ExpressTrackVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1/express")
public class ExpressController {

    @Autowired
    private ExpressCompanyService expressCompanyService;

    @Autowired
    private ExpressOrderService expressOrderService;

    @GetMapping("/company/list")
    public R<List<ExpressCompanyVO>> listCompanies() {
        List<ExpressCompany> companies = expressCompanyService.listEnabledCompanies();
        List<ExpressCompanyVO> voList = companies.stream()
                .map(company -> {
                    ExpressCompanyVO vo = new ExpressCompanyVO();
                    BeanUtils.copyProperties(company, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        return R.ok(voList);
    }

    @PostMapping("/price/calculate")
    public R<ExpressPriceCalcVO> calculatePrice(@Validated @RequestBody ExpressPriceQueryRequest request) {
        log.info("快递价格试算请求：senderProvince={}, receiverProvince={}, weight={}",
                request.getSenderProvince(), request.getReceiverProvince(), request.getWeight());

        ExpressPriceCalcVO result = new ExpressPriceCalcVO();
        result.setThirdPartyBasePrice(new java.math.BigDecimal("10.00"));
        result.setFinalPrice(new java.math.BigDecimal("15.00"));
        result.setProfitAmount(new java.math.BigDecimal("5.00"));

        return R.ok(result);
    }

    @PostMapping("/order")
    public R<ExpressOrderVO> createOrder(@Validated @RequestBody ExpressOrderCreateRequest request) {
        Long userId = UserContext.getUserId();
        log.info("创建快递寄件订单请求：userId={}", userId);

        ExpressOrderVO orderVO = expressOrderService.createExpressOrder(userId, request);
        return R.ok(orderVO);
    }

    @GetMapping("/order/list")
    public R<IPage<ExpressOrderVO>> listOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderStatus) {
        Long userId = UserContext.getUserId();
        log.info("查询快递订单列表请求：userId={}, pageNum={}, pageSize={}, orderStatus={}",
                userId, pageNum, pageSize, orderStatus);

        IPage<ExpressOrderVO> page = expressOrderService.listExpressOrders(userId, pageNum, pageSize, orderStatus);
        return R.ok(page);
    }

    @GetMapping("/order/{id}")
    public R<ExpressOrderVO> getOrderDetail(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        log.info("查询快递订单详情请求：userId={}, orderId={}", userId, id);

        ExpressOrderVO orderVO = expressOrderService.getExpressOrderDetail(userId, id);
        return R.ok(orderVO);
    }

    @PostMapping("/order/{id}/cancel")
    public R<Void> cancelOrder(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        log.info("取消快递订单请求：userId={}, orderId={}", userId, id);

        expressOrderService.cancelExpressOrder(userId, id);
        return R.ok();
    }

    @GetMapping("/order/{id}/track")
    public R<ExpressTrackVO> getOrderTrack(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        log.info("查询快递订单物流轨迹请求：userId={}, orderId={}", userId, id);

        ExpressTrackVO trackVO = expressOrderService.getExpressTrack(userId, id);
        return R.ok(trackVO);
    }

    @PostMapping("/order/{id}/after-sales")
    public R<Void> applyAfterSales(@PathVariable Long id, @RequestParam String reason) {
        Long userId = UserContext.getUserId();
        log.info("申请快递订单售后请求：userId={}, orderId={}, reason={}", userId, id, reason);

        expressOrderService.applyAfterSales(userId, id, reason);
        return R.ok();
    }
}
