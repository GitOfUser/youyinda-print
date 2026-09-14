package com.youyinda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.common.BusinessException;
import com.youyinda.common.enums.ErrorCodeEnum;
import com.youyinda.dto.PrintOrderCreateRequest;
import com.youyinda.dto.PrintOrderItemRequest;
import com.youyinda.dto.PrintOrderRequest;
import com.youyinda.dto.ThirdApiResponse;
import com.youyinda.entity.OrderDetail;
import com.youyinda.entity.OrderLogistics;
import com.youyinda.entity.OrderMain;
import com.youyinda.entity.PrintBasePrice;
import com.youyinda.entity.PrintOrder;
import com.youyinda.entity.UserAddress;
import com.youyinda.feign.PrintProviderFeignClient;
import com.youyinda.mapper.PrintOrderMapper;
import com.youyinda.service.OrderLogisticsService;
import com.youyinda.service.OrderMainService;
import com.youyinda.service.PrintBasePriceService;
import com.youyinda.service.PrintOrderService;
import com.youyinda.service.UserAddressService;
import com.youyinda.util.PriceCalculateUtil;
import com.youyinda.vo.PrintOrderDetailVO;
import com.youyinda.vo.PrintOrderVO;
import com.youyinda.vo.UserAddressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
 * 打印订单表Service实现类
 * 打通 66印 / 小猴云印 多场景打印下单全链路：计价 → 第三方下单（66印优先，小猴云印降级）→ 落库
 */
@Slf4j
@Service
public class PrintOrderServiceImpl extends ServiceImpl<PrintOrderMapper, PrintOrder> implements PrintOrderService {

    /** 装订附加费（价格与前端枚举对齐）：staple=1.00、clip=0.50、glue=5.00、ring=8.00、cover=3.00 */
    private static final BigDecimal BINDING_STAPLE_FEE = new BigDecimal("1.00");
    private static final BigDecimal BINDING_CLIP_FEE = new BigDecimal("0.50");
    private static final BigDecimal BINDING_GLUE_FEE = new BigDecimal("5.00");
    private static final BigDecimal BINDING_RING_FEE = new BigDecimal("8.00");
    private static final BigDecimal BINDING_COVER_FEE = new BigDecimal("3.00");
    /** 增值服务：覆膜每页 +0.5 元，打孔每份 +1 元 */
    private static final BigDecimal FILM_PER_PAGE = new BigDecimal("0.5");
    private static final BigDecimal PUNCH_PER_COPY = new BigDecimal("1.00");
    /** 默认快递配送费 */
    private static final BigDecimal DEFAULT_EXPRESS_FEE = new BigDecimal("5.00");

    /** 服务商编码：66印（优先）、小猴云印（降级） */
    private static final String PROVIDER_66YIN = "liuliuyin";
    private static final String PROVIDER_XIAOHOU = "xiaohou";

    @Resource
    private OrderMainService orderMainService;

    @Resource
    private UserAddressService userAddressService;

    @Resource
    private PrintBasePriceService printBasePriceService;

    @Resource
    private OrderLogisticsService orderLogisticsService;

    @Resource
    private PrintProviderFeignClient printProviderFeignClient;

    @Override
    public PrintOrder getByOrderId(Long orderId) {
        return this.getById(orderId);
    }

    @Override
    public boolean updateThirdInfo(Long orderId, String thirdOrderNo, Integer thirdStatus) {
        PrintOrder order = new PrintOrder();
        order.setId(orderId);
        order.setThirdOrderNo(thirdOrderNo);
        order.setThirdStatus(thirdStatus);
        return this.updateById(order);
    }

    /**
     * 创建打印订单（多场景打印全链路）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrintOrderVO createPrintOrder(Long userId, PrintOrderCreateRequest request) {
        if (CollectionUtils.isEmpty(request.getItems())) {
            throw new BusinessException(ErrorCodeEnum.PARAM_ERROR, "打印文件列表不能为空");
        }
        UserAddress address = userAddressService.getById(request.getAddressId());
        if (address == null) {
            throw new BusinessException(ErrorCodeEnum.ADDRESS_NOT_FOUND, "收货地址不存在");
        }

        String orderNo = orderMainService.generateOrderNo();
        BigDecimal orderTotal = BigDecimal.ZERO;
        List<PrintOrderDetailVO> detailVos = new ArrayList<>();
        List<OrderDetail> orderDetails = new ArrayList<>();

        // 遍历每个文件项计算价格
        for (PrintOrderItemRequest item : request.getItems()) {
            // 1. 解析打印参数
            String paperType = item.getPaperType();          // A4 / A3 / 照片纸
            String colorType = item.getColorType();          // 前端传 "1"/"2"，也可能为 黑白/彩色/black/color
            String colorTypeCn = normalizeColorType(colorType); // 统一为中文「黑白/彩色」再查价格表
            String singleDouble = item.getSingleDouble();    // single / double
            Integer copies = item.getCopies() == null ? 1 : item.getCopies();
            Integer pages = item.getQuantity() == null ? 0 : item.getQuantity();
            String binding = item.getBindingType();         // 无 / 骑马钉 / 胶装

            // 2. 查询第三方基础价（优先 66印，降级小猴云印）
            // print_side 列存中文「单面/双面」，需将 single/double 映射后查询（getBySpec 保持按 print_side 列匹配）
            String printSideCn = "double".equalsIgnoreCase(singleDouble) ? "双面" : "单面";
            PrintBasePrice base = printBasePriceService.getBySpec(PROVIDER_66YIN, paperType, colorTypeCn, printSideCn);
            String usedProvider = PROVIDER_66YIN;
            if (base == null) {
                base = printBasePriceService.getBySpec(PROVIDER_XIAOHOU, paperType, colorTypeCn, printSideCn);
                usedProvider = PROVIDER_XIAOHOU;
            }
            // B5 纸型在 print_base_price 表无基础价，兜底降级为 A4 计价，保证下单不中断
            if (base == null && paperType != null && paperType.contains("B5")) {
                log.warn("[打印基础价] 纸型 {} 无基础价，降级为 A4 计价", paperType);
                base = printBasePriceService.getBySpec(PROVIDER_66YIN, "A4", colorTypeCn, printSideCn);
                usedProvider = PROVIDER_66YIN;
                if (base == null) {
                    base = printBasePriceService.getBySpec(PROVIDER_XIAOHOU, "A4", colorTypeCn, printSideCn);
                    usedProvider = PROVIDER_XIAOHOU;
                }
            }
            if (base == null) {
                throw new BusinessException(ErrorCodeEnum.PRICE_CALC_ERROR,
                        "未找到打印基础价格：" + paperType + "/" + colorType + "/" + printSideCn);
            }

            // 3. 计算平台最终价（单份）
            // 双面按奇数进一计算张数（singleDouble 取值："1"/"double" 表示双面，"0"/"single" 表示单面）
            boolean isDouble = "1".equals(singleDouble) || "double".equalsIgnoreCase(singleDouble);
            int billingPages = isDouble
                    ? PriceCalculateUtil.calculatePrintPageCount(pages) : pages;
            BigDecimal bindingFee = calcBindingFee(binding);
            BigDecimal valueAddedFee = calcValueAddedFee(item, pages, copies);
            BigDecimal expressFee = DEFAULT_EXPRESS_FEE; // 默认快递配送

            PriceCalculateUtil.PriceDetail detail = PriceCalculateUtil.calculatePrintPrice(
                    base, billingPages, bindingFee, valueAddedFee, expressFee);
            BigDecimal unitPrice = detail.getUnitPrice(); // 单页价
            // 单份总价 = 单页价 × 计费页数 + 装订 + 增值 + 快递（PriceDetail.totalPrice 已含）
            BigDecimal singleTotal = detail.getTotalPrice();
            // 该文件项总价 = 单份总价 × 份数
            BigDecimal itemTotal = singleTotal.multiply(new BigDecimal(copies));
            orderTotal = orderTotal.add(itemTotal);

            // 4. 构建 OrderDetail 落库
            OrderDetail od = new OrderDetail();
            od.setOrderId(null); // 回填
            od.setProductType(1); // 1-打印
            od.setProductName(item.getFileName());
            od.setQuantity(copies);
            od.setUnitPrice(unitPrice.doubleValue());
            od.setTotalPrice(itemTotal.doubleValue());
            od.setFileUrl(item.getFileUrl());
            od.setFileName(item.getFileName());
            od.setPrintPages(pages);
            od.setPrintCopies(copies);
            od.setPaperType(paperType);
            od.setColorType(colorTypeCn);
            od.setPrintSide(singleDouble);
            od.setBindingType(binding);
            od.setSubtotal(itemTotal.doubleValue());
            orderDetails.add(od);

            // 5. 构建 VO
            PrintOrderDetailVO dv = new PrintOrderDetailVO();
            dv.setFileName(item.getFileName());
            dv.setFileUrl(item.getFileUrl());
            dv.setPages(pages);
            dv.setCopies(copies);
            dv.setPaperType(paperType);
            dv.setColorType(colorTypeCn);
            dv.setPrintSide(singleDouble);
            dv.setUnitPrice(unitPrice);
            dv.setSubtotal(itemTotal);
            detailVos.add(dv);

            // 6. 调用第三方下单（每个文件项独立下单，66印优先，失败降级小猴云印）
            placeThirdOrder(orderNo, item, address, usedProvider);
        }

        // 创建 OrderMain 主表
        OrderMain orderMain = new OrderMain();
        orderMain.setOrderNo(orderNo);
        orderMain.setUserId(userId);
        orderMain.setOrderType(1); // 1-打印订单
        orderMain.setTotalPrice(orderTotal.doubleValue());
        orderMain.setActualPrice(orderTotal.doubleValue());
        orderMain.setStatus(1); // 1-待支付
        orderMain.setPayStatus(1); // 1-未支付
        orderMain.setAddressId(request.getAddressId());
        orderMain.setRemark(request.getRemark());
        orderMainService.save(orderMain);

        // 创建 PrintOrder 实体（含 thirdOrderNo / thirdStatus）
        PrintOrder printOrder = new PrintOrder();
        printOrder.setOrderId(orderMain.getId());
        printOrder.setPageCount(request.getItems().stream()
                .mapToInt(i -> i.getQuantity() == null ? 0 : i.getQuantity()).sum());
        printOrder.setColorType("彩色".equals(colorTypeOf(request)) ? 2 : 1);
        printOrder.setPaperType(paperTypeCode(request));
        printOrder.setBindingType(bindingCode(request));
        printOrder.setDoubleSided(doubleSidedOf(request) ? 1 : 0);
        printOrder.setThirdOrderNo(orderNo);
        printOrder.setThirdStatus(0);
        this.save(printOrder);

        // 回填 OrderDetail 的 orderId
        for (OrderDetail od : orderDetails) {
            od.setOrderId(orderMain.getId());
        }
        // 批量保存 OrderDetail（通过 Service 注入）
        orderDetailService.saveBatch(orderDetails);

        // 组装返回 VO
        PrintOrderVO vo = new PrintOrderVO();
        vo.setId(orderMain.getId());
        vo.setOrderNo(orderNo);
        vo.setStatus("WAIT_PAY");
        vo.setTotalPrice(orderTotal);
        vo.setPayStatus("UNPAID");
        vo.setCreateTime(new Date());
        vo.setDetails(detailVos);
        vo.setAddress(convertAddress(address));
        vo.setRemark(request.getRemark());
        return vo;
    }

    // ===================== 私有辅助方法 =====================

    /**
     * 调用第三方打印下单（66印优先，失败降级小猴云印）
     */
    private void placeThirdOrder(String orderNo, PrintOrderItemRequest item, UserAddress address, String primaryProvider) {
        PrintOrderRequest req = new PrintOrderRequest();
        req.setOrderNo(orderNo);
        req.setFileUrl(item.getSpecJson());
        req.setPaperType(item.getPaperType());
        req.setColorType(item.getColorType());
        req.setSingleDouble(item.getSingleDouble());
        req.setPageCount(item.getQuantity());
        req.setCopies(item.getCopies());
        req.setBindingType(item.getBindingType());
        req.setRemark(item.getSpecJson());
        req.setReceiverName(address.getName());
        req.setReceiverPhone(address.getPhone());
        req.setReceiverAddress(address.getProvince() + address.getCity()
                + address.getDistrict() + address.getDetailAddress());

        // 优先 66印
        try {
            ThirdApiResponse<PrintOrderVO> resp = printProviderFeignClient.createOrder(req);
            if (resp != null && resp.isSuccess()) {
                return;
            }
            log.warn("[66印下单未成功] orderNo={}, msg={}", orderNo, resp == null ? "无响应" : resp.getMsg());
        } catch (Exception e) {
            log.warn("[66印下单异常，降级小猴云印] orderNo={}, err={}", orderNo, e.getMessage());
        }

        // 降级小猴云印（若主服务商不是小猴）
        if (!PROVIDER_XIAOHOU.equals(primaryProvider)) {
            try {
                printProviderFeignClient.createOrder(req);
            } catch (Exception e) {
                log.warn("[小猴云印降级下单失败] orderNo={}, err={}", orderNo, e.getMessage());
            }
        }
    }

    /**
     * 计算装订费
     * 支持前端枚举：none=0、staple=1.00、clip=0.50、glue=5.00、ring=8.00、cover=3.00
     * 兼容旧值：「骑马钉」/「1」按 staple 计，「胶装」/「2」按 glue 计
     */
    private BigDecimal calcBindingFee(String binding) {
        if (binding == null) {
            return BigDecimal.ZERO;
        }
        switch (binding) {
            case "staple":
            case "骑马钉":
            case "1":
                return BINDING_STAPLE_FEE;
            case "clip":
                return BINDING_CLIP_FEE;
            case "glue":
            case "胶装":
            case "2":
                return BINDING_GLUE_FEE;
            case "ring":
                return BINDING_RING_FEE;
            case "cover":
                return BINDING_COVER_FEE;
            case "none":
            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * 计算增值服务费（覆膜每页+0.5，打孔每份+1）
     */
    private BigDecimal calcValueAddedFee(PrintOrderItemRequest item, Integer pages, Integer copies) {
        BigDecimal fee = BigDecimal.ZERO;
        String spec = item.getSpecJson();
        if (spec != null) {
            if (spec.contains("覆膜")) {
                fee = fee.add(FILM_PER_PAGE.multiply(new BigDecimal(pages == null ? 0 : pages)));
            }
            if (spec.contains("打孔")) {
                fee = fee.add(PUNCH_PER_COPY.multiply(new BigDecimal(copies == null ? 0 : copies)));
            }
        }
        return fee;
    }

    /**
     * 取订单中第一个文件的色彩类型
     */
    private String colorTypeOf(PrintOrderCreateRequest request) {
        if (CollectionUtils.isEmpty(request.getItems())) {
            return "黑白";
        }
        return normalizeColorType(request.getItems().get(0).getColorType());
    }

    /**
     * 色彩类型归一化：前端传 "1"/"2"（1-黑白 2-彩色）或 black/color/中文，统一转为中文「黑白/彩色」
     */
    private String normalizeColorType(String colorType) {
        if (colorType == null) {
            return "黑白";
        }
        if ("2".equals(colorType) || "彩色".equals(colorType) || "color".equalsIgnoreCase(colorType)) {
            return "彩色";
        }
        return "黑白"; // "1" / "黑白" / "black" / 其它一律按黑白
    }

    /**
     * 纸张类型编码：A4=1, A3=2, 照片纸=3
     */
    private Integer paperTypeCode(PrintOrderCreateRequest request) {
        if (CollectionUtils.isEmpty(request.getItems())) {
            return 1;
        }
        String pt = request.getItems().get(0).getPaperType();
        if ("A3".equals(pt)) {
            return 2;
        }
        if ("照片纸".equals(pt)) {
            return 3;
        }
        return 1;
    }

    /**
     * 装订类型编码：无=0, 骑马钉=1, 胶装=2
     */
    private Integer bindingCode(PrintOrderCreateRequest request) {
        if (CollectionUtils.isEmpty(request.getItems())) {
            return 0;
        }
        String bt = request.getItems().get(0).getBindingType();
        if ("骑马钉".equals(bt) || "1".equals(bt)) {
            return 1;
        }
        if ("胶装".equals(bt) || "2".equals(bt)) {
            return 2;
        }
        return 0;
    }

    /**
     * 是否双面
     */
    private boolean doubleSidedOf(PrintOrderCreateRequest request) {
        if (CollectionUtils.isEmpty(request.getItems())) {
            return false;
        }
        return "double".equalsIgnoreCase(request.getItems().get(0).getSingleDouble());
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

    @Resource
    private com.youyinda.service.OrderDetailService orderDetailService;
}
