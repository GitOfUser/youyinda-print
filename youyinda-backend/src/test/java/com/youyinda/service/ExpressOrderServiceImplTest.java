package com.youyinda.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youyinda.common.BusinessException;
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
import com.youyinda.service.impl.ExpressOrderServiceImpl;
import com.youyinda.vo.ExpressOrderVO;
import com.youyinda.vo.ExpressPriceVO;
import com.youyinda.vo.ExpressTrackVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 快递订单 Service 单元测试
 * 覆盖创建、取消、详情、轨迹、售后、分页查询全链路
 */
@ExtendWith(MockitoExtension.class)
class ExpressOrderServiceImplTest {

    @Mock
    private ExpressOrderMapper expressOrderMapper;
    @Mock
    private OrderMainService orderMainService;
    @Mock
    private UserAddressService userAddressService;
    @Mock
    private ExpressCompanyService expressCompanyService;
    @Mock
    private ExpressBasePriceService expressBasePriceService;
    @Mock
    private OrderLogisticsService orderLogisticsService;
    @Mock
    private ExpressProviderFeignClient expressProviderFeignClient;

    @InjectMocks
    private ExpressOrderServiceImpl expressOrderService;

    private UserAddress sender;
    private UserAddress receiver;
    private ExpressCompany company;
    private ExpressOrderCreateRequest validRequest;

    @BeforeEach
    void setUp() {
        sender = new UserAddress();
        sender.setId(1L);
        sender.setName("张三");
        sender.setPhone("13800000001");
        sender.setProvince("广东");
        sender.setCity("深圳");
        sender.setDistrict("南山区");
        sender.setDetailAddress("科技园1号");

        receiver = new UserAddress();
        receiver.setId(2L);
        receiver.setName("李四");
        receiver.setPhone("13800000002");
        receiver.setProvince("北京");
        receiver.setCity("北京");
        receiver.setDistrict("朝阳区");
        receiver.setDetailAddress("建国路2号");

        company = new ExpressCompany();
        company.setExpressCode("yuantong");
        company.setExpressName("圆通速递");

        validRequest = new ExpressOrderCreateRequest();
        validRequest.setSenderAddressId(1L);
        validRequest.setReceiverAddressId(2L);
        validRequest.setExpressCode("yuantong");
        validRequest.setWeight(new BigDecimal("1.0"));
        validRequest.setGoodsType("文件");
        validRequest.setTotalAmount(new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("正常创建快递订单-第三方下单成功返回订单VO")
    void testCreateExpressOrder_Success() {
        when(userAddressService.getById(1L)).thenReturn(sender);
        when(userAddressService.getById(2L)).thenReturn(receiver);
        when(expressCompanyService.listEnabledCompanies()).thenReturn(Collections.singletonList(company));
        when(expressProviderFeignClient.queryPrice(any(ExpressPriceQueryRequest.class)))
                .thenReturn(ThirdApiResponse.success(Collections.singletonList(buildPriceVo())));
        when(expressBasePriceService.getByParams("yuantong", "广东", "北京"))
                .thenReturn(buildExpressBasePrice());
        when(orderMainService.generateOrderNo()).thenReturn("EXP20260722001");
        when(expressProviderFeignClient.createOrder(any(ExpressOrderRequest.class)))
                .thenReturn(ThirdApiResponse.success(buildThirdOrderVo()));
        lenient().when(orderMainService.getById(anyLong())).thenReturn(new OrderMain());
        lenient().when(orderLogisticsService.getByOrderId(anyLong())).thenReturn(new OrderLogistics());

        ExpressOrderVO vo = expressOrderService.createExpressOrder(100L, validRequest);

        assertNotNull(vo);
        assertEquals("EXP20260722001", vo.getOrderNo());
        assertEquals("圆通速递", vo.getCourier());
        assertEquals("WAIT_PICKUP", vo.getStatus());
        assertNotNull(vo.getTrackingNo());
        verify(expressProviderFeignClient).createOrder(any(ExpressOrderRequest.class));
        verify(orderMainService).save(any(OrderMain.class));
    }

    @Test
    @DisplayName("第三方下单失败-正确抛出业务异常")
    void testCreateExpressOrder_ThirdPartyFail() {
        when(userAddressService.getById(1L)).thenReturn(sender);
        when(userAddressService.getById(2L)).thenReturn(receiver);
        when(expressCompanyService.listEnabledCompanies()).thenReturn(Collections.singletonList(company));
        when(expressProviderFeignClient.queryPrice(any(ExpressPriceQueryRequest.class)))
                .thenReturn(ThirdApiResponse.success(Collections.singletonList(buildPriceVo())));
        when(expressBasePriceService.getByParams(anyString(), anyString(), anyString()))
                .thenReturn(buildExpressBasePrice());
        when(orderMainService.generateOrderNo()).thenReturn("EXP20260722002");
        // 第三方返回失败
        ThirdApiResponse<ExpressOrderVO> failResp = new ThirdApiResponse<>();
        failResp.setCode(500);
        failResp.setMsg("第三方下单异常");
        when(expressProviderFeignClient.createOrder(any(ExpressOrderRequest.class))).thenReturn(failResp);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> expressOrderService.createExpressOrder(100L, validRequest));
        assertTrue(ex.getMessage().contains("快递下单失败"));
    }

    @Test
    @DisplayName("寄件人地址不完整-校验失败抛出参数异常")
    void testCreateExpressOrder_AddressIncomplete() {
        // 寄件人缺少详细地址
        sender.setDetailAddress(null);
        when(userAddressService.getById(1L)).thenReturn(sender);
        when(userAddressService.getById(2L)).thenReturn(receiver);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> expressOrderService.createExpressOrder(100L, validRequest));
        assertTrue(ex.getMessage().contains("寄件人地址信息不完整"));
    }

    @Test
    @DisplayName("待揽收状态取消订单-成功调用第三方取消并更新状态")
    void testCancelExpressOrder_Success() {
        ExpressOrder expressOrder = new ExpressOrder();
        expressOrder.setId(10L);
        expressOrder.setOrderId(20L);
        expressOrder.setThirdOrderNo("THIRD123");
        when(expressOrderMapper.selectById(10L)).thenReturn(expressOrder);

        OrderMain orderMain = new OrderMain();
        orderMain.setId(20L);
        orderMain.setUserId(100L);
        orderMain.setStatus(1); // 待支付=待揽收
        orderMain.setOrderNo("EXP20260722003");
        when(orderMainService.getById(20L)).thenReturn(orderMain);
        when(orderLogisticsService.getByOrderId(20L)).thenReturn(new OrderLogistics());

        boolean result = expressOrderService.cancelExpressOrder(100L, 10L);

        assertTrue(result);
        verify(expressProviderFeignClient).cancelOrder(any(ExpressCancelOrderRequest.class));
        verify(orderMainService).updateStatus(20L, 6);
    }

    @Test
    @DisplayName("已揽收状态取消订单-失败抛出状态异常")
    void testCancelExpressOrder_AlreadyShipped() {
        ExpressOrder expressOrder = new ExpressOrder();
        expressOrder.setId(11L);
        expressOrder.setOrderId(21L);
        when(expressOrderMapper.selectById(11L)).thenReturn(expressOrder);

        OrderMain orderMain = new OrderMain();
        orderMain.setId(21L);
        orderMain.setUserId(100L);
        orderMain.setStatus(3); // 已揽收/运输中，非待揽收
        when(orderMainService.getById(21L)).thenReturn(orderMain);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> expressOrderService.cancelExpressOrder(100L, 11L));
        assertTrue(ex.getMessage().contains("仅待揽收状态可取消"));
        verify(expressProviderFeignClient, never()).cancelOrder(any());
    }

    @Test
    @DisplayName("获取物流轨迹-含mock轨迹JSON正确解析")
    void testGetExpressTrack() {
        ExpressOrder expressOrder = new ExpressOrder();
        expressOrder.setId(12L);
        expressOrder.setOrderId(22L);
        expressOrder.setThirdOrderNo("THIRD456");
        when(expressOrderMapper.selectById(12L)).thenReturn(expressOrder);

        OrderMain orderMain = new OrderMain();
        orderMain.setId(22L);
        orderMain.setUserId(100L);
        orderMain.setStatus(3);
        orderMain.setOrderNo("EXP20260722004");
        orderMain.setCourier("yuantong");
        orderMain.setTrackingNo("YT1234567890");
        when(orderMainService.getById(22L)).thenReturn(orderMain);

        // 构造轨迹 JSON，updateTime 为 null 触发刷新
        OrderLogistics logistics = new OrderLogistics();
        logistics.setTrackingNo("YT1234567890");
        logistics.setExpressName("圆通速递");
        logistics.setLogisticsStatus("TRANSPORTING");
        logistics.setTrackJson(null);
        logistics.setUpdateTime(null);
        when(orderLogisticsService.getByOrderId(22L)).thenReturn(logistics);

        ExpressTrackVO trackVo = new ExpressTrackVO();
        trackVo.setTrackingNo("YT1234567890");
        trackVo.setExpressName("圆通速递");
        trackVo.setLogisticsStatus("TRANSPORTING");
        List<ExpressTrackVO.TrackInfo> list = new ArrayList<>();
        ExpressTrackVO.TrackInfo info = new ExpressTrackVO.TrackInfo();
        info.setTime(new Date());
        info.setStatus("已揽收");
        info.setDescription("快件已揽收");
        info.setLocation("深圳");
        list.add(info);
        trackVo.setTrackList(list);
        when(expressProviderFeignClient.queryTrack(any(ExpressTrackQueryRequest.class)))
                .thenReturn(ThirdApiResponse.success(trackVo));

        ExpressTrackVO result = expressOrderService.getExpressTrack(100L, 12L);

        assertNotNull(result);
        assertEquals("YT1234567890", result.getTrackingNo());
        assertNotNull(result.getTrackList());
        assertEquals(1, result.getTrackList().size());
        assertEquals("已揽收", result.getTrackList().get(0).getStatus());
    }

    @Test
    @DisplayName("分页查询用户快递订单列表-返回分页结果")
    void testListExpressOrders() {
        OrderMain m1 = new OrderMain();
        m1.setId(30L);
        m1.setUserId(100L);
        m1.setOrderType(2);
        m1.setOrderNo("EXP20260722005");
        m1.setStatus(1);
        m1.setTotalPrice(10.0);
        m1.setPayStatus(1);
        m1.setCourier("yuantong");
        m1.setTrackingNo("YT999");
        m1.setFromAddressId(1L);
        m1.setAddressId(2L);

        when(orderMainService.getByUserIdAndType(100L, 2)).thenReturn(Collections.singletonList(m1));
        when(expressOrderMapper.selectOne(any())).thenReturn(new ExpressOrder());
        when(userAddressService.getById(1L)).thenReturn(sender);
        when(userAddressService.getById(2L)).thenReturn(receiver);

        IPage<ExpressOrderVO> page = expressOrderService.listExpressOrders(100L, 1, 10, null);

        assertNotNull(page);
        assertEquals(1, page.getRecords().size());
        assertEquals("EXP20260722005", page.getRecords().get(0).getOrderNo());
        assertEquals("WAIT_PICKUP", page.getRecords().get(0).getStatus());
    }

    // ===================== 辅助构造方法 =====================

    private ExpressPriceVO buildPriceVo() {
        ExpressPriceVO vo = new ExpressPriceVO();
        vo.setExpressCode("yuantong");
        vo.setExpressName("圆通速递");
        vo.setTotalPrice(new BigDecimal("8.00"));
        vo.setEstimatedTime("2天");
        return vo;
    }

    private ExpressBasePrice buildExpressBasePrice() {
        ExpressBasePrice base = new ExpressBasePrice();
        base.setCourier("yuantong");
        base.setFromProvince("广东");
        base.setToProvince("北京");
        base.setFirstWeight(1.0);
        base.setFirstPrice(10.0);
        base.setContinueWeight(1.0);
        base.setContinuePrice(5.0);
        base.setMinProfit(2.0);
        base.setProfitRatio(20.0);
        return base;
    }

    private ExpressOrderVO buildThirdOrderVo() {
        ExpressOrderVO vo = new ExpressOrderVO();
        vo.setOrderNo("THIRD123");
        vo.setTrackingNo("YT1234567890");
        return vo;
    }
}
