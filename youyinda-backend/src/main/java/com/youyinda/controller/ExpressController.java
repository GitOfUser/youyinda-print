package com.youyinda.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youyinda.common.R;
import com.youyinda.config.UserContext;
import com.youyinda.dto.ExpressOrderCreateRequest;
import com.youyinda.dto.ExpressPriceQueryRequest;
import com.youyinda.dto.ThirdApiResponse;
import com.youyinda.entity.ExpressBasePrice;
import com.youyinda.entity.ExpressCompany;
import com.youyinda.feign.ExpressProviderFeignClient;
import com.youyinda.service.ExpressBasePriceService;
import com.youyinda.service.ExpressCompanyService;
import com.youyinda.service.ExpressOrderService;
import com.youyinda.vo.ExpressCompanyVO;
import com.youyinda.vo.ExpressOrderVO;
import com.youyinda.vo.ExpressPriceCalcVO;
import com.youyinda.vo.ExpressPriceCompareVO;
import com.youyinda.vo.ExpressTrackVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/v1/express")
public class ExpressController {

    @Autowired
    private ExpressCompanyService expressCompanyService;

    @Autowired
    private ExpressOrderService expressOrderService;

    @Autowired
    private ExpressBasePriceService expressBasePriceService;

    @Autowired
    private ExpressProviderFeignClient expressProviderFeignClient;

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
    public R<List<ExpressPriceCompareVO>> calculatePrice(@Validated @RequestBody ExpressPriceQueryRequest request) {
        log.info("快递价格试算请求：senderProvince={}, receiverProvince={}, weight={}",
                request.getSenderProvince(), request.getReceiverProvince(), request.getWeight());

        // 1. 查询启用的快递公司列表
        List<ExpressCompany> companies = expressCompanyService.listEnabledCompanies();
        if (companies == null || companies.isEmpty()) {
            throw new com.youyinda.common.BusinessException(
                    com.youyinda.common.enums.ErrorCodeEnum.PARAM_ERROR, "暂无可用快递公司");
        }

        List<ExpressPriceCompareVO> compareList = new ArrayList<>();
        for (ExpressCompany company : companies) {
            // 2. 查 ExpressBasePrice 表获取对应快递公司/收发省份的基础价
            ExpressBasePrice base = expressBasePriceService.getByParams(
                    company.getExpressCode(), request.getSenderProvince(), request.getReceiverProvince());
            if (base == null) {
                log.warn("未找到基础价配置：{} {}->{}", company.getExpressCode(),
                        request.getSenderProvince(), request.getReceiverProvince());
                continue;
            }

            // 3. 调用第三方实时运费查询
            ExpressPriceQueryRequest priceReq = new ExpressPriceQueryRequest();
            priceReq.setExpressCode(company.getExpressCode());
            priceReq.setSenderProvince(request.getSenderProvince());
            priceReq.setSenderCity(request.getSenderCity());
            priceReq.setReceiverProvince(request.getReceiverProvince());
            priceReq.setReceiverCity(request.getReceiverCity());
            priceReq.setWeight(request.getWeight());

            BigDecimal thirdFreight = BigDecimal.ZERO;
            try {
                ThirdApiResponse<List<com.youyinda.vo.ExpressPriceVO>> resp =
                        expressProviderFeignClient.queryPrice(priceReq);
                if (resp != null && resp.isSuccess() && resp.getData() != null && !resp.getData().isEmpty()) {
                    thirdFreight = resp.getData().get(0).getTotalPrice();
                }
            } catch (Exception e) {
                log.warn("查询第三方运费失败：{}", e.getMessage());
            }
            // 第三方无返回时，使用基础价兜底
            if (thirdFreight.compareTo(BigDecimal.ZERO) <= 0) {
                BigDecimal weight = request.getWeight();
                BigDecimal firstWeight = BigDecimal.valueOf(base.getFirstWeight());
                BigDecimal excessWeight = weight.subtract(firstWeight);
                if (excessWeight.compareTo(BigDecimal.ZERO) < 0) {
                    excessWeight = BigDecimal.ZERO;
                }
                thirdFreight = BigDecimal.valueOf(base.getFirstPrice())
                        .add(BigDecimal.valueOf(base.getContinuePrice()).multiply(excessWeight));
            }

            // 4. 通过 PriceCalculateUtil 计算平台最终价（基于第三方实时运费）
            BigDecimal finalPrice =
                    com.youyinda.util.PriceCalculateUtil.calculateExpressPrice(
                            thirdFreight,
                            new BigDecimal(base.getProfitRatio()),
                            new BigDecimal(base.getMinProfit()));

            ExpressPriceCompareVO vo = new ExpressPriceCompareVO();
            vo.setExpressCode(company.getExpressCode());
            vo.setExpressName(company.getExpressName());
            vo.setThirdPartyBasePrice(thirdFreight);
            vo.setFinalPrice(finalPrice);
            vo.setProfitAmount(finalPrice.subtract(thirdFreight));
            vo.setEstimatedTime(company.getTimelinessDesc());
            compareList.add(vo);
        }

        // 按平台最终价升序，方便用户选择最优惠方案
        compareList.sort(Comparator.comparing(ExpressPriceCompareVO::getFinalPrice));
        return R.ok(compareList);
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
