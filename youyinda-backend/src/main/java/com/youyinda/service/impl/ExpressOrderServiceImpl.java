package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.common.BusinessException;
import com.youyinda.common.enums.ErrorCodeEnum;
import com.youyinda.dto.ExpressCancelOrderRequest;
import com.youyinda.dto.ExpressOrderCreateRequest;
import com.youyinda.dto.ExpressOrderRequest;
import com.youyinda.dto.ExpressPriceQueryRequest;
import com.youyinda.dto.ExpressTrackQueryRequest;
import com.youyinda.dto.ThirdApiResponse;
import com.youyinda.entity.ExpressBasePrice;
import com.youyinda.entity.ExpressCompany;
import com.youyinda.entity.ExpressOrder;
import com.youyinda.entity.OrderLogistics;
import com.youyinda.entity.OrderMain;
import com.youyinda.entity.UserAddress;
import com.youyinda.feign.ExpressProviderFeignClient;
import com.youyinda.mapper.ExpressOrderMapper;
import com.youyinda.service.ExpressBasePriceService;
import com.youyinda.service.ExpressCompanyService;
import com.youyinda.service.ExpressOrderService;
import com.youyinda.service.OrderLogisticsService;
import com.youyinda.service.OrderMainService;
import com.youyinda.service.UserAddressService;
import com.youyinda.util.PriceCalculateUtil;
import com.youyinda.vo.ExpressOrderVO;
import com.youyinda.vo.ExpressPriceVO;
import com.youyinda.vo.ExpressTrackVO;
import com.youyinda.vo.UserAddressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 快递订单表Service实现类
 * 打通快递100·百递云下单全链路：比价 → 下单 → 物流落库 → 状态同步
 */
@Slf4j
@Service
public class ExpressOrderServiceImpl extends ServiceImpl<ExpressOrderMapper, ExpressOrder> implements ExpressOrderService {

    /** 物流状态：待揽收 */
    private static final String LOGISTICS_WAIT_PICKUP = "WAIT_PICKUP";
    /** 物流状态：已取消 */
    private static final String LOGISTICS_CANCELED = "CANCELED";
    /** 订单状态：待揽收（对应 OrderMain.status 待支付/待揽收区间，此处用字符串描述） */
    private static final String ORDER_STATUS_WAIT_PICKUP = "WAIT_PICKUP";
    /** 订单状态：已取消 */
    private static final String ORDER_STATUS_CANCELED = "CANCELED";
    /** 订单状态：已完成 */
    private static final String ORDER_STATUS_FINISHED = "FINISHED";
    /** 物流轨迹刷新间隔（毫秒）：30 分钟 */
    private static final long TRACK_REFRESH_INTERVAL = 30 * 60 * 1000L;

    @Resource
    private OrderMainService orderMainService;

    @Resource
    private UserAddressService userAddressService;

    @Resource
    private ExpressCompanyService expressCompanyService;

    @Resource
    private ExpressBasePriceService expressBasePriceService;

    @Resource
    private OrderLogisticsService orderLogisticsService;

    @Resource
    private ExpressProviderFeignClient expressProviderFeignClient;

    /** 第三方快递不可用时是否降级为本地模拟单号（测试/联调环境开启） */
    @Value("${third-party.express.mock-enabled:false}")
    private boolean mockEnabled;

    @Override
    public ExpressOrder getByOrderId(Long orderId) {
        return this.getById(orderId);
    }

    @Override
    public boolean updateThirdInfo(Long orderId, String thirdOrderNo, Integer thirdStatus) {
        ExpressOrder order = new ExpressOrder();
        order.setId(orderId);
        order.setThirdOrderNo(thirdOrderNo);
        order.setThirdStatus(thirdStatus);
        return this.updateById(order);
    }

    /**
     * 创建快递订单全链路
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpressOrderVO createExpressOrder(Long userId, ExpressOrderCreateRequest request) {
        // 1. 校验寄件人/收件人地址完整性
        UserAddress sender = userAddressService.getById(request.getSenderAddressId());
        UserAddress receiver = userAddressService.getById(request.getReceiverAddressId());
        if (sender == null || receiver == null) {
            throw new BusinessException(ErrorCodeEnum.ADDRESS_NOT_FOUND, "寄件或收件地址不存在");
        }
        validateAddress(sender, "寄件人");
        validateAddress(receiver, "收件人");

        // 2. 查询启用的快递公司列表，获取快递编码与时效
        ExpressCompany company = expressCompanyService.listEnabledCompanies().stream()
                .filter(c -> c.getExpressCode().equals(request.getExpressCode()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.EXPRESS_COMPANY_NOT_FOUND, "快递公司不存在或未启用"));

        // 3. 调用第三方查询实时运费
        ExpressPriceQueryRequest priceQuery = new ExpressPriceQueryRequest();
        priceQuery.setExpressCode(request.getExpressCode());
        priceQuery.setSenderProvince(sender.getProvince());
        priceQuery.setSenderCity(sender.getCity());
        priceQuery.setReceiverProvince(receiver.getProvince());
        priceQuery.setReceiverCity(receiver.getCity());
        priceQuery.setWeight(request.getWeight());

        BigDecimal thirdFreight = queryThirdFreight(priceQuery, company);

        // 4. 通过 PriceCalculateUtil 计算平台最终价格
        // 优先使用基础价格表（含盈利比例/最低固定盈利），否则回退到第三方实时运费 + 默认盈利
        BigDecimal finalPrice = calculateFinalPrice(request.getExpressCode(), sender.getProvince(),
                receiver.getProvince(), request.getWeight(), thirdFreight);

        // 5. 构建第三方下单请求并调用快递100下单（第三方不可用时按配置降级为本地模拟单号）
        String orderNo = orderMainService.generateOrderNo();
        ExpressOrderRequest thirdRequest = buildThirdOrderRequest(orderNo, request, sender, receiver, company);
        String thirdOrderNo = null;
        String trackingNo = null;
        try {
            ThirdApiResponse<ExpressOrderVO> createResp = expressProviderFeignClient.createOrder(thirdRequest);
            if (createResp != null && createResp.isSuccess() && createResp.getData() != null) {
                ExpressOrderVO thirdVo = createResp.getData();
                thirdOrderNo = thirdVo.getOrderNo();
                trackingNo = thirdVo.getTrackingNo();
            } else {
                log.warn("快递第三方下单未成功: {}", createResp == null ? "无响应" : createResp.getMsg());
            }
        } catch (Exception e) {
            log.warn("快递第三方下单异常: {}", e.getMessage());
        }
        if (thirdOrderNo == null) {
            if (!mockEnabled) {
                throw new BusinessException(ErrorCodeEnum.EXPRESS_ORDER_FAIL, "快递下单失败：第三方服务不可用");
            }
            thirdOrderNo = "MOCK" + orderNo;
            trackingNo = "MOCKTN" + System.currentTimeMillis();
            log.warn("快递第三方不可用，已按 mock-enabled 降级生成本地模拟单号：{}", thirdOrderNo);
        }

        // 6. 创建 OrderMain 主表记录（先落主表，供子表关联）
        OrderMain orderMain = new OrderMain();
        orderMain.setOrderNo(orderNo);
        orderMain.setUserId(userId);
        orderMain.setOrderType(2); // 2-快递订单
        orderMain.setTotalPrice(finalPrice.doubleValue());
        orderMain.setActualPrice(finalPrice.doubleValue());
        orderMain.setStatus(1); // 1-待支付（待揽收前置）
        orderMain.setPayStatus(1); // 1-未支付
        orderMain.setCourier(company.getExpressCode());
        orderMain.setTrackingNo(trackingNo);
        orderMain.setFromAddressId(request.getSenderAddressId());
        orderMain.setWeight(request.getWeight() == null ? null : request.getWeight().doubleValue());
        orderMain.setGoodsType(request.getGoodsType());
        orderMain.setRemark(request.getRemark());
        orderMainService.save(orderMain);
        Long orderMainId = orderMain.getId();

        // 7. 创建 OrderLogistics 记录（含第三方单号、物流状态=待揽收）
        OrderLogistics logistics = new OrderLogistics();
        logistics.setOrderId(orderMainId);
        logistics.setOrderNo(orderNo);
        logistics.setExpressCode(company.getExpressCode());
        logistics.setExpressName(company.getExpressName());
        logistics.setTrackingNo(trackingNo);
        logistics.setSenderName(sender.getName());
        logistics.setSenderPhone(sender.getPhone());
        logistics.setSenderProvince(sender.getProvince());
        logistics.setSenderCity(sender.getCity());
        logistics.setSenderDistrict(sender.getDistrict());
        logistics.setSenderAddress(sender.getDetailAddress());
        logistics.setReceiverName(receiver.getName());
        logistics.setReceiverPhone(receiver.getPhone());
        logistics.setReceiverProvince(receiver.getProvince());
        logistics.setReceiverCity(receiver.getCity());
        logistics.setReceiverDistrict(receiver.getDistrict());
        logistics.setReceiverAddress(receiver.getDetailAddress());
        logistics.setWeight(request.getWeight());
        logistics.setGoodsType(request.getGoodsType());
        logistics.setGoodsDesc(request.getGoodsDesc());
        logistics.setInsuranceAmount(request.getInsuranceAmount());
        logistics.setLogisticsStatus(LOGISTICS_WAIT_PICKUP);
        logistics.setThirdOrderNo(thirdOrderNo);
        orderLogisticsService.save(logistics);

        // 8. 创建 ExpressOrder 实体落库
        ExpressOrder expressOrder = new ExpressOrder();
        expressOrder.setOrderId(orderMainId);
        expressOrder.setSenderName(sender.getName());
        expressOrder.setSenderPhone(sender.getPhone());
        expressOrder.setSenderProvince(sender.getProvince());
        expressOrder.setSenderCity(sender.getCity());
        expressOrder.setSenderDistrict(sender.getDistrict());
        expressOrder.setSenderDetail(sender.getDetailAddress());
        expressOrder.setReceiverName(receiver.getName());
        expressOrder.setReceiverPhone(receiver.getPhone());
        expressOrder.setReceiverProvince(receiver.getProvince());
        expressOrder.setReceiverCity(receiver.getCity());
        expressOrder.setReceiverDistrict(receiver.getDistrict());
        expressOrder.setReceiverDetail(receiver.getDetailAddress());
        expressOrder.setWeight(request.getWeight() == null ? null : request.getWeight().doubleValue());
        expressOrder.setGoodsType(request.getGoodsType());
        expressOrder.setThirdOrderNo(thirdOrderNo);
        expressOrder.setThirdStatus(0);
        this.save(expressOrder);

        // 9. 组装返回 VO
        ExpressOrderVO vo = new ExpressOrderVO();
        vo.setId(orderMainId);
        vo.setOrderNo(orderNo);
        vo.setStatus(ORDER_STATUS_WAIT_PICKUP);
        vo.setTotalPrice(finalPrice);
        vo.setCourier(company.getExpressName());
        vo.setTrackingNo(trackingNo);
        vo.setFromAddress(convertAddress(sender));
        vo.setToAddress(convertAddress(receiver));
        vo.setWeight(request.getWeight() == null ? null : request.getWeight().doubleValue());
        vo.setGoodsType(request.getGoodsType());
        vo.setRemark(request.getRemark());
        vo.setCreateTime(new Date());
        return vo;
    }

    /**
     * 获取快递订单详情（含物流轨迹）
     */
    @Override
    public ExpressOrderVO getExpressOrderDetail(Long userId, Long orderId) {
        OrderMain orderMain = orderMainService.getById(orderId);
        if (orderMain == null || !userId.equals(orderMain.getUserId())) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND, "快递订单不存在");
        }
        ExpressOrder expressOrder = this.lambdaQuery().eq(ExpressOrder::getOrderId, orderId).one();
        if (expressOrder == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND, "快递订单不存在");
        }

        ExpressOrderVO vo = new ExpressOrderVO();
        BeanUtils.copyProperties(expressOrder, vo);
        vo.setId(orderMain.getId());
        vo.setOrderNo(orderMain.getOrderNo());
        vo.setStatus(convertOrderStatus(orderMain.getStatus()));
        vo.setTotalPrice(BigDecimal.valueOf(orderMain.getTotalPrice() == null ? 0 : orderMain.getTotalPrice()));
        vo.setPayStatus(convertPayStatus(orderMain.getPayStatus()));
        vo.setPayTime(orderMain.getPayTime());
        vo.setCreateTime(orderMain.getCreateTime());
        vo.setCourier(orderMain.getCourier());
        vo.setTrackingNo(orderMain.getTrackingNo());
        vo.setWeight(expressOrder.getWeight());
        vo.setGoodsType(expressOrder.getGoodsType());
        vo.setRemark(orderMain.getRemark());

        // 填充地址
        UserAddress sender = userAddressService.getById(orderMain.getFromAddressId());
        if (sender != null) {
            vo.setFromAddress(convertAddress(sender));
        }
        UserAddress receiver = userAddressService.getById(orderMain.getAddressId());
        if (receiver != null) {
            vo.setToAddress(convertAddress(receiver));
        }

        // 若订单未完成，刷新物流状态
        if (orderMain.getStatus() != null && orderMain.getStatus() < 7) {
            try {
                ExpressTrackVO track = refreshTrack(expressOrder, orderMain);
                vo.setTrackingNo(track.getTrackingNo());
            } catch (Exception e) {
                log.warn("[物流刷新失败] orderId={}, err={}", orderId, e.getMessage());
            }
        }
        return vo;
    }

    /**
     * 取消快递订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelExpressOrder(Long userId, Long orderId) {
        OrderMain orderMain = orderMainService.getById(orderId);
        if (orderMain == null || !userId.equals(orderMain.getUserId())) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND, "快递订单不存在");
        }
        ExpressOrder expressOrder = this.lambdaQuery().eq(ExpressOrder::getOrderId, orderId).one();
        if (expressOrder == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND, "快递订单不存在");
        }
        // 仅待揽收可取消
        if (!ORDER_STATUS_WAIT_PICKUP.equals(convertOrderStatus(orderMain.getStatus()))
                && orderMain.getStatus() != 1) {
            throw new BusinessException(ErrorCodeEnum.ORDER_STATUS_ERROR, "仅待揽收状态可取消");
        }

        // 调用第三方取消
        ExpressCancelOrderRequest cancelReq = new ExpressCancelOrderRequest();
        cancelReq.setOrderNo(orderMain.getOrderNo());
        cancelReq.setProviderOrderNo(expressOrder.getThirdOrderNo());
        cancelReq.setCancelReason("用户主动取消");
        try {
            expressProviderFeignClient.cancelOrder(cancelReq);
        } catch (Exception e) {
            log.warn("[第三方取消失败] orderId={}, err={}", orderId, e.getMessage());
        }

        // 更新本地订单状态为已取消
        orderMainService.updateStatus(orderMain.getId(), 6); // 6-已取消
        expressOrder.setThirdStatus(2);
        this.updateById(expressOrder);

        OrderLogistics logistics = orderLogisticsService.getByOrderId(orderMain.getId());
        if (logistics != null) {
            logistics.setLogisticsStatus(LOGISTICS_CANCELED);
            orderLogisticsService.updateById(logistics);
        }
        return true;
    }

    /**
     * 获取物流轨迹
     */
    @Override
    public ExpressTrackVO getExpressTrack(Long userId, Long orderId) {
        OrderMain orderMain = orderMainService.getById(orderId);
        if (orderMain == null || !userId.equals(orderMain.getUserId())) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND, "快递订单不存在");
        }
        ExpressOrder expressOrder = this.lambdaQuery().eq(ExpressOrder::getOrderId, orderId).one();
        if (expressOrder == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND, "快递订单不存在");
        }
        OrderLogistics logistics = orderLogisticsService.getByOrderId(orderMain.getId());
        if (logistics == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND, "物流记录不存在");
        }

        // 若轨迹为空或超过 30 分钟未更新，则刷新
        boolean needRefresh = StringUtils.isEmpty(logistics.getTrackJson())
                || logistics.getUpdateTime() == null
                || (System.currentTimeMillis() - logistics.getUpdateTime().getTime()) > TRACK_REFRESH_INTERVAL;
        if (needRefresh) {
            return refreshTrack(expressOrder, orderMain);
        }

        // 解析已存储的轨迹 JSON
        ExpressTrackVO vo = new ExpressTrackVO();
        vo.setTrackingNo(logistics.getTrackingNo());
        vo.setExpressName(logistics.getExpressName());
        vo.setLogisticsStatus(logistics.getLogisticsStatus());
        try {
            vo.setTrackList(com.alibaba.fastjson.JSON.parseArray(logistics.getTrackJson(),
                    ExpressTrackVO.TrackInfo.class));
        } catch (Exception e) {
            log.warn("[轨迹解析失败] orderId={}", orderId);
        }
        return vo;
    }

    /**
     * 申请售后
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyAfterSales(Long userId, Long orderId, String reason) {
        ExpressOrder expressOrder = this.getById(orderId);
        if (expressOrder == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND, "快递订单不存在");
        }
        OrderMain orderMain = orderMainService.getById(expressOrder.getOrderId());
        if (orderMain == null || !userId.equals(orderMain.getUserId())) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND, "订单无权限访问");
        }
        // 校验订单已完成
        if (orderMain.getStatus() == null || orderMain.getStatus() != 7) {
            throw new BusinessException(ErrorCodeEnum.ORDER_STATUS_ERROR, "仅已完成订单可申请售后");
        }
        // 创建售后记录（此处以备注 + 状态标记体现，售后单号由售后系统生成）
        String afterSalesNo = "AS" + System.currentTimeMillis();
        orderMain.setRemark("售后单号[" + afterSalesNo + "] 原因：" + (reason == null ? "无" : reason));
        orderMainService.updateById(orderMain);
        log.info("[售后申请] orderId={}, afterSalesNo={}, reason={}", orderId, afterSalesNo, reason);
        return true;
    }

    /**
     * 分页查询用户快递订单
     */
    @Override
    public IPage<ExpressOrderVO> listExpressOrders(Long userId, Integer page, Integer size, String status) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        // 先查 OrderMain（快递类型=2）
        List<OrderMain> mains = orderMainService.getByUserIdAndType(userId, 2);
        if (!StringUtils.isEmpty(status)) {
            mains.removeIf(m -> !status.equals(convertOrderStatus(m.getStatus())));
        }
        IPage<ExpressOrderVO> result = new Page<>(page, size);
        List<ExpressOrderVO> vos = new ArrayList<>();
        for (OrderMain m : mains) {
            ExpressOrder expressOrder = this.lambdaQuery()
                    .eq(ExpressOrder::getOrderId, m.getId()).last("LIMIT 1").one();
            ExpressOrderVO vo = new ExpressOrderVO();
            vo.setId(m.getId());
            vo.setOrderNo(m.getOrderNo());
            vo.setStatus(convertOrderStatus(m.getStatus()));
            vo.setTotalPrice(BigDecimal.valueOf(m.getTotalPrice() == null ? 0 : m.getTotalPrice()));
            vo.setPayStatus(convertPayStatus(m.getPayStatus()));
            vo.setPayTime(m.getPayTime());
            vo.setCreateTime(m.getCreateTime());
            vo.setCourier(m.getCourier());
            vo.setTrackingNo(m.getTrackingNo());
            if (expressOrder != null) {
                vo.setWeight(expressOrder.getWeight());
                vo.setGoodsType(expressOrder.getGoodsType());
            }
            UserAddress sender = userAddressService.getById(m.getFromAddressId());
            if (sender != null) {
                vo.setFromAddress(convertAddress(sender));
            }
            UserAddress receiver = userAddressService.getById(m.getAddressId());
            if (receiver != null) {
                vo.setToAddress(convertAddress(receiver));
            }
            vos.add(vo);
        }
        result.setRecords(vos);
        result.setTotal(mains.size());
        return result;
    }

    // ===================== 私有辅助方法 =====================

    /**
     * 校验地址完整性
     */
    private void validateAddress(UserAddress addr, String role) {
        if (StringUtils.isEmpty(addr.getName()) || StringUtils.isEmpty(addr.getPhone())
                || StringUtils.isEmpty(addr.getProvince()) || StringUtils.isEmpty(addr.getCity())
                || StringUtils.isEmpty(addr.getDetailAddress())) {
            throw new BusinessException(ErrorCodeEnum.PARAM_ERROR, role + "地址信息不完整");
        }
    }

    /**
     * 查询第三方实时运费（优先取第一个报价）
     */
    private BigDecimal queryThirdFreight(ExpressPriceQueryRequest query, ExpressCompany company) {
        try {
            ThirdApiResponse<List<ExpressPriceVO>> resp = expressProviderFeignClient.queryPrice(query);
            if (resp != null && resp.isSuccess() && !CollectionUtils.isEmpty(resp.getData())) {
                return resp.getData().get(0).getTotalPrice();
            }
        } catch (Exception e) {
            log.warn("[运费查询失败] code={}, err={}", company.getExpressCode(), e.getMessage());
        }
        return null;
    }

    /**
     * 计算平台最终价格
     */
    private BigDecimal calculateFinalPrice(String expressCode, String fromProvince, String toProvince,
                                           BigDecimal weight, BigDecimal thirdFreight) {
        ExpressBasePrice base = expressBasePriceService.getByParams(expressCode, fromProvince, toProvince);
        double w = weight == null ? 1.0 : weight.doubleValue();
        if (base != null) {
            PriceCalculateUtil.PriceDetail detail = PriceCalculateUtil.calculateExpressPrice(base, w);
            return detail.getTotalPrice();
        }
        // 回退：第三方运费 + 默认 20% 盈利
        if (thirdFreight != null) {
            return thirdFreight.multiply(new BigDecimal("1.2"))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        throw new BusinessException(ErrorCodeEnum.PRICE_CALC_ERROR, "无法计算运费，缺少基础价格配置");
    }

    /**
     * 构建第三方下单请求
     */
    private ExpressOrderRequest buildThirdOrderRequest(String orderNo, ExpressOrderCreateRequest request,
                                                      UserAddress sender, UserAddress receiver, ExpressCompany company) {
        ExpressOrderRequest req = new ExpressOrderRequest();
        req.setOrderNo(orderNo);
        req.setExpressCode(company.getExpressCode());
        req.setSenderName(sender.getName());
        req.setSenderPhone(sender.getPhone());
        req.setSenderProvince(sender.getProvince());
        req.setSenderCity(sender.getCity());
        req.setSenderDistrict(sender.getDistrict());
        req.setSenderAddress(sender.getDetailAddress());
        req.setReceiverName(receiver.getName());
        req.setReceiverPhone(receiver.getPhone());
        req.setReceiverProvince(receiver.getProvince());
        req.setReceiverCity(receiver.getCity());
        req.setReceiverDistrict(receiver.getDistrict());
        req.setReceiverAddress(receiver.getDetailAddress());
        req.setWeight(request.getWeight());
        req.setGoodsName(request.getGoodsType());
        req.setGoodsCount(1);
        req.setRemark(request.getRemark());
        return req;
    }

    /**
     * 刷新物流轨迹（调用第三方并落库）
     */
    private ExpressTrackVO refreshTrack(ExpressOrder expressOrder, OrderMain orderMain) {
        ExpressTrackQueryRequest trackReq = new ExpressTrackQueryRequest();
        trackReq.setOrderNo(orderMain.getOrderNo());
        trackReq.setProviderOrderNo(expressOrder.getThirdOrderNo());
        trackReq.setTrackingNo(orderMain.getTrackingNo());
        trackReq.setExpressCode(orderMain.getCourier());
        try {
            ThirdApiResponse<ExpressTrackVO> resp = expressProviderFeignClient.queryTrack(trackReq);
            if (resp != null && resp.isSuccess() && resp.getData() != null) {
                ExpressTrackVO track = resp.getData();
                OrderLogistics logistics = orderLogisticsService.getByOrderId(orderMain.getId());
                if (logistics != null) {
                    logistics.setLogisticsStatus(track.getLogisticsStatus());
                    logistics.setTrackingNo(track.getTrackingNo());
                    if (!CollectionUtils.isEmpty(track.getTrackList())) {
                        logistics.setTrackJson(com.alibaba.fastjson.JSON.toJSONString(track.getTrackList()));
                    }
                    orderLogisticsService.updateById(logistics);
                }
                return track;
            }
            log.warn("[轨迹查询响应无效，降级返回本地状态] orderId={}", orderMain.getId());
        } catch (Exception e) {
            log.warn("[轨迹刷新失败，降级返回本地状态] orderId={}, err={}", orderMain.getId(), e.getMessage());
        }
        // 第三方不可用，返回本地兜底轨迹
        OrderLogistics logistics = orderLogisticsService.getByOrderId(orderMain.getId());
        ExpressTrackVO fallback = new ExpressTrackVO();
        fallback.setTrackingNo(orderMain.getTrackingNo());
        fallback.setExpressName(logistics != null ? logistics.getExpressName() : null);
        fallback.setLogisticsStatus(logistics != null ? logistics.getLogisticsStatus() : LOGISTICS_WAIT_PICKUP);
        fallback.setTrackList(new ArrayList<>());
        return fallback;
    }

    /**
     * 地址实体转 VO
     */
    private UserAddressVO convertAddress(UserAddress addr) {
        if (addr == null) {
            return null;
        }
        UserAddressVO vo = new UserAddressVO();
        BeanUtils.copyProperties(addr, vo);
        return vo;
    }

    /**
     * OrderMain.status(1-7) → 字符串状态
     */
    private String convertOrderStatus(Integer status) {
        if (status == null) {
            return ORDER_STATUS_WAIT_PICKUP;
        }
        switch (status) {
            case 1: return ORDER_STATUS_WAIT_PICKUP;
            case 6: return ORDER_STATUS_CANCELED;
            case 7: return ORDER_STATUS_FINISHED;
            default: return "PROCESSING";
        }
    }

    /**
     * OrderMain.payStatus(1-4) → 字符串
     */
    private String convertPayStatus(Integer payStatus) {
        if (payStatus == null) {
            return "UNPAID";
        }
        switch (payStatus) {
            case 1: return "UNPAID";
            case 2: return "PAID";
            case 3: return "REFUNDING";
            case 4: return "REFUNDED";
            default: return "UNPAID";
        }
    }
}
