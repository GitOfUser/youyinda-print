package com.youyinda.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youyinda.common.BusinessException;
import com.youyinda.dto.PrintFileDTO;
import com.youyinda.dto.PrintOrderCreateRequest;
import com.youyinda.dto.PrintOrderItemRequest;
import com.youyinda.dto.PrintOrderRequest;
import com.youyinda.dto.ThirdApiResponse;
import com.youyinda.entity.OrderDetail;
import com.youyinda.entity.OrderMain;
import com.youyinda.entity.PrintBasePrice;
import com.youyinda.entity.PrintOrder;
import com.youyinda.entity.UserAddress;
import com.youyinda.feign.PrintProviderFeignClient;
import com.youyinda.mapper.PrintOrderMapper;
import com.youyinda.service.impl.PrintOrderServiceImpl;
import com.youyinda.vo.PrintOrderVO;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 打印订单 Service 单元测试
 * 覆盖多场景打印下单、价格计算、服务商降级、盈利计算
 */
@ExtendWith(MockitoExtension.class)
class PrintOrderServiceImplTest {

    @Mock
    private PrintOrderMapper printOrderMapper;
    @Mock
    private OrderMainService orderMainService;
    @Mock
    private OrderDetailService orderDetailService;
    @Mock
    private UserAddressService userAddressService;
    @Mock
    private PrintBasePriceService printBasePriceService;
    @Mock
    private PrintProviderFeignClient printProviderFeignClient;

    @InjectMocks
    private PrintOrderServiceImpl printOrderService;

    private UserAddress address;
    private PrintOrderCreateRequest baseRequest;

    @BeforeEach
    void setUp() {
        address = new UserAddress();
        address.setId(1L);
        address.setName("张三");
        address.setPhone("13800000001");
        address.setProvince("广东");
        address.setCity("深圳");
        address.setDistrict("南山区");
        address.setDetailAddress("科技园1号");

        baseRequest = new PrintOrderCreateRequest();
        baseRequest.setAddressId(1L);
        baseRequest.setRemark("测试订单");
    }

    private PrintOrderItemRequest buildItem(String paperType, String colorType, String singleDouble,
                                            Integer copies, String bindingType, Integer pages) {
        PrintOrderItemRequest item = new PrintOrderItemRequest();
        item.setFileId(100L);
        item.setPaperType(paperType);
        item.setColorType(colorType);
        item.setSingleDouble(singleDouble);
        item.setCopies(copies);
        item.setBindingType(bindingType);
        item.setQuantity(pages);
        item.setSpecJson("{}");
        return item;
    }

    private PrintBasePrice buildBasePrice(String paperType, String colorType, String singleDouble,
                                          double base, double min) {
        PrintBasePrice p = new PrintBasePrice();
        p.setPaperType(paperType);
        p.setColorType(colorType);
        p.setSingleDouble(singleDouble);
        p.setBasePrice(base);
        p.setMinPrice(min);
        p.setProfitRatio(30.0);
        p.setProviderCode("liuliuyin");
        return p;
    }

    private void mockCommonSuccess(String orderNo) {
        when(userAddressService.getById(1L)).thenReturn(address);
        when(orderMainService.generateOrderNo()).thenReturn(orderNo);
        when(printProviderFeignClient.createOrder(any(PrintOrderRequest.class)))
                .thenReturn(ThirdApiResponse.success(new PrintOrderVO()));
        lenient().when(orderMainService.getById(anyLong())).thenReturn(new OrderMain());
        lenient().when(printOrderMapper.selectById(anyLong())).thenReturn(new PrintOrder());
        lenient().when(orderDetailService.getByOrderId(anyLong())).thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("A4黑白单面打印-单份下单成功")
    void testCreatePrintOrder_SingleFile_A4_BlackWhite_SingleSide() {
        baseRequest.setItems(Collections.singletonList(
                buildItem("A4", "1", "0", 1, "0", 10)));
        when(printBasePriceService.getBySpec("liuliuyin", "A4", "1", "0"))
                .thenReturn(buildBasePrice("A4", "1", "0", 0.08, 0.15));
        mockCommonSuccess("PRT20260722001");

        PrintOrderVO vo = printOrderService.createPrintOrder(100L, baseRequest);

        assertNotNull(vo);
        assertEquals("PRT20260722001", vo.getOrderNo());
        verify(printProviderFeignClient).createOrder(any(PrintOrderRequest.class));
        verify(orderMainService).save(any(OrderMain.class));
    }

    @Test
    @DisplayName("多文件双面打印-分别计价汇总")
    void testCreatePrintOrder_MultiFile_DoubleSide() {
        List<PrintOrderItemRequest> items = new ArrayList<>();
        items.add(buildItem("A4", "1", "1", 2, "0", 10));
        items.add(buildItem("A4", "1", "1", 1, "0", 20));
        baseRequest.setItems(items);
        when(printBasePriceService.getBySpec("liuliuyin", "A4", "1", "1"))
                .thenReturn(buildBasePrice("A4", "1", "1", 0.10, 0.18));
        mockCommonSuccess("PRT20260722002");

        PrintOrderVO vo = printOrderService.createPrintOrder(100L, baseRequest);

        assertNotNull(vo);
        assertEquals("PRT20260722002", vo.getOrderNo());
        verify(orderDetailService, atLeastOnce()).saveBatch(anyList());
    }

    @Test
    @DisplayName("彩色加胶装打印-含装订附加费")
    void testCreatePrintOrder_Color_Binding() {
        baseRequest.setItems(Collections.singletonList(
                buildItem("A4", "2", "0", 1, "2", 30)));
        when(printBasePriceService.getBySpec("liuliuyin", "A4", "2", "0"))
                .thenReturn(buildBasePrice("A4", "2", "0", 0.30, 0.50));
        mockCommonSuccess("PRT20260722003");

        PrintOrderVO vo = printOrderService.createPrintOrder(100L, baseRequest);

        assertNotNull(vo);
        assertEquals("PRT20260722003", vo.getOrderNo());
    }

    @Test
    @DisplayName("照片纸打印-彩色单面无装订")
    void testCreatePrintOrder_Photo() {
        baseRequest.setItems(Collections.singletonList(
                buildItem("照片纸", "2", "0", 1, "0", 10)));
        when(printBasePriceService.getBySpec("liuliuyin", "照片纸", "2", "0"))
                .thenReturn(buildBasePrice("照片纸", "2", "0", 0.80, 1.50));
        mockCommonSuccess("PRT20260722004");

        PrintOrderVO vo = printOrderService.createPrintOrder(100L, baseRequest);

        assertNotNull(vo);
        assertEquals("PRT20260722004", vo.getOrderNo());
    }

    @Test
    @DisplayName("多场景价格计算-四个典型场景总价正确")
    void testMultiScenePricing() {
        // 场景1：考试试卷 A4/黑白/双面/无装订/5页/2份
        when(printBasePriceService.getBySpec("liuliuyin", "A4", "1", "1"))
                .thenReturn(buildBasePrice("A4", "1", "1", 0.10, 0.18));
        // 双面5页按奇数进一=3张；单份价=MAX(0.10*1.3,0.18)=0.18；单份=0.18*3+5=5.54；2份=11.08
        PrintOrderCreateRequest r1 = new PrintOrderCreateRequest();
        r1.setAddressId(1L);
        r1.setItems(Collections.singletonList(buildItem("A4", "1", "1", 2, "0", 5)));
        mockCommonSuccess("PRT-S1");
        PrintOrderVO v1 = printOrderService.createPrintOrder(100L, r1);
        // 单份价=0.18；双面5页按奇数进一=3张；单份=0.18*3+5=5.54；2份=11.08
        assertEquals(0, new BigDecimal("11.08").setScale(2, java.math.RoundingMode.HALF_UP)
                .compareTo(v1.getTotalPrice().setScale(2, java.math.RoundingMode.HALF_UP)));

        // 场景2：毕业论文 A4/黑白/双面/胶装/50页/1份
        PrintOrderCreateRequest r2 = new PrintOrderCreateRequest();
        r2.setAddressId(1L);
        r2.setItems(Collections.singletonList(buildItem("A4", "1", "1", 1, "2", 50)));
        mockCommonSuccess("PRT-S2");
        PrintOrderVO v2 = printOrderService.createPrintOrder(100L, r2);
        // 单份价=0.18；双面50页按奇数进一=25张；打印费=0.18*25=4.50；胶装+5.00；快递+5.00；总价=14.50
        assertEquals(0, new BigDecimal("14.50").setScale(2, java.math.RoundingMode.HALF_UP)
                .compareTo(v2.getTotalPrice().setScale(2, java.math.RoundingMode.HALF_UP)));

        // 场景3：彩色PPT A4/彩色/单面/骑马钉/20页/1份
        when(printBasePriceService.getBySpec("liuliuyin", "A4", "2", "0"))
                .thenReturn(buildBasePrice("A4", "2", "0", 0.30, 0.50));
        PrintOrderCreateRequest r3 = new PrintOrderCreateRequest();
        r3.setAddressId(1L);
        r3.setItems(Collections.singletonList(buildItem("A4", "2", "0", 1, "1", 20)));
        mockCommonSuccess("PRT-S3");
        PrintOrderVO v3 = printOrderService.createPrintOrder(100L, r3);
        // 单份价=MAX(0.30*1.3,0.50)=0.50；打印费=0.50*20=10.00；骑马钉+2.00；快递+5.00；总价=17.00
        assertEquals(0, new BigDecimal("17.00").compareTo(v3.getTotalPrice()));

        // 场景4：照片冲印 照片纸/彩色/单面/无装订/10张/1份
        when(printBasePriceService.getBySpec("liuliuyin", "照片纸", "2", "0"))
                .thenReturn(buildBasePrice("照片纸", "2", "0", 0.80, 1.50));
        PrintOrderCreateRequest r4 = new PrintOrderCreateRequest();
        r4.setAddressId(1L);
        r4.setItems(Collections.singletonList(buildItem("照片纸", "2", "0", 1, "0", 10)));
        mockCommonSuccess("PRT-S4");
        PrintOrderVO v4 = printOrderService.createPrintOrder(100L, r4);
        // 单份价=MAX(0.80*1.3,1.50)=1.50；打印费=1.50*10=15.00；快递+5.00；总价=20.00
        assertEquals(0, new BigDecimal("20.00").compareTo(v4.getTotalPrice()));
    }

    @Test
    @DisplayName("66印不可用时降级到小猴云印")
    void testCreatePrintOrder_ProviderFallback() {
        baseRequest.setItems(Collections.singletonList(
                buildItem("A4", "1", "0", 1, "0", 10)));
        // 66印查询返回 null，触发降级到小猴云印
        when(printBasePriceService.getBySpec("liuliuyin", "A4", "1", "0")).thenReturn(null);
        when(printBasePriceService.getBySpec(eq("xiaohou"), eq("A4"), eq("1"), eq("0")))
                .thenReturn(buildBasePrice("A4", "1", "0", 0.09, 0.15));
        mockCommonSuccess("PRT20260722005");

        PrintOrderVO vo = printOrderService.createPrintOrder(100L, baseRequest);

        assertNotNull(vo);
        verify(printBasePriceService).getBySpec(eq("xiaohou"), eq("A4"), eq("1"), eq("0"));
    }

    @Test
    @DisplayName("盈利计算-低于最低限价取限价，高于则按比例")
    void testProfitCalculation() {
        // 第三方价0.08元，盈利比例30% → 0.08*1.3=0.104 < 最低限价0.15 → 取0.15
        when(printBasePriceService.getBySpec("liuliuyin", "A4", "1", "0"))
                .thenReturn(buildBasePrice("A4", "1", "0", 0.08, 0.15));
        PrintOrderCreateRequest r1 = new PrintOrderCreateRequest();
        r1.setAddressId(1L);
        r1.setItems(Collections.singletonList(buildItem("A4", "1", "0", 1, "0", 1)));
        mockCommonSuccess("PRT-P1");
        PrintOrderVO v1 = printOrderService.createPrintOrder(100L, r1);
        // 单份价0.15 + 快递5.00 = 5.15
        assertEquals(0, new BigDecimal("5.15").setScale(2, java.math.RoundingMode.HALF_UP)
                .compareTo(v1.getTotalPrice().setScale(2, java.math.RoundingMode.HALF_UP)));

        // 第三方价0.50元，盈利比例30% → 0.50*1.3=0.65 > 最低限价0.15 → 取0.65
        when(printBasePriceService.getBySpec("liuliuyin", "A4", "1", "0"))
                .thenReturn(buildBasePrice("A4", "1", "0", 0.50, 0.15));
        PrintOrderCreateRequest r2 = new PrintOrderCreateRequest();
        r2.setAddressId(1L);
        r2.setItems(Collections.singletonList(buildItem("A4", "1", "0", 1, "0", 1)));
        mockCommonSuccess("PRT-P2");
        PrintOrderVO v2;
        try {
            v2 = printOrderService.createPrintOrder(100L, r2);
        } catch (Throwable t) {
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("target/debug_p2.txt"),
                        ("THROW=" + t.getClass().getName() + " : " + t.getMessage()).getBytes());
            } catch (Exception ignore) {}
            throw new RuntimeException(t);
        }
        // 单份价0.65 + 快递5.00 = 5.65
        BigDecimal actual2 = v2 == null ? null : v2.getTotalPrice();
        if (actual2 != null && actual2.compareTo(new BigDecimal("5.65")) != 0) {
            fail("实际总价=" + actual2.toPlainString() + ", items=" + (r2.getItems()==null?0:r2.getItems().size())
                    + ", pages=" + (r2.getItems()==null?"":r2.getItems().get(0).getQuantity())
                    + ", copies=" + (r2.getItems()==null?"":r2.getItems().get(0).getCopies())
                    + ", binding=" + (r2.getItems()==null?"":r2.getItems().get(0).getBindingType()));
        }
        assertEquals(0, new BigDecimal("5.65").compareTo(actual2));
    }
}
