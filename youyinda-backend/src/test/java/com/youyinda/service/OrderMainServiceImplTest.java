package com.youyinda.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youyinda.entity.OrderMain;
import com.youyinda.mapper.OrderMainMapper;
import com.youyinda.service.impl.OrderMainServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 订单主表 Service 单元测试
 * 覆盖订单查询、生成订单号、状态更新、支付状态更新
 */
@ExtendWith(MockitoExtension.class)
class OrderMainServiceImplTest {

    @Mock
    private OrderMainMapper orderMainMapper;

    @InjectMocks
    private OrderMainServiceImpl orderMainService;

    private OrderMain printOrder;
    private OrderMain expressOrder;

    @BeforeEach
    void setUp() {
        printOrder = new OrderMain();
        printOrder.setId(1L);
        printOrder.setUserId(100L);
        printOrder.setOrderType(1);
        printOrder.setOrderNo("PRT202607280001");
        printOrder.setStatus(1);
        printOrder.setPayStatus(0);
        printOrder.setTotalPrice(15.50);

        expressOrder = new OrderMain();
        expressOrder.setId(2L);
        expressOrder.setUserId(100L);
        expressOrder.setOrderType(2);
        expressOrder.setOrderNo("EXP202607280002");
        expressOrder.setStatus(3);
        expressOrder.setPayStatus(1);
        expressOrder.setTotalPrice(12.00);
    }

    @Test
    @DisplayName("根据用户ID和类型获取订单列表-打印订单")
    void testGetByUserIdAndType_Print() {
        when(orderMainMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(printOrder));

        List<OrderMain> result = orderMainService.getByUserIdAndType(100L, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PRT202607280001", result.get(0).getOrderNo());
    }

    @Test
    @DisplayName("根据用户ID和类型获取订单列表-快递订单")
    void testGetByUserIdAndType_Express() {
        when(orderMainMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(expressOrder));

        List<OrderMain> result = orderMainService.getByUserIdAndType(100L, 2);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("EXP202607280002", result.get(0).getOrderNo());
    }

    @Test
    @DisplayName("根据用户ID、类型、状态筛选订单")
    void testGetByUserId_WithStatusFilter() {
        when(orderMainMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(printOrder));

        List<OrderMain> result = orderMainService.getByUserId(100L, 1, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderMainMapper).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("根据用户ID获取全部订单-不传类型和状态")
    void testGetByUserId_NoFilter() {
        when(orderMainMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(printOrder, expressOrder));

        List<OrderMain> result = orderMainService.getByUserId(100L, null, null);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("根据订单号获取订单-存在")
    void testGetByOrderNo_Exists() {
        when(orderMainMapper.selectOne(any(QueryWrapper.class))).thenReturn(printOrder);

        OrderMain result = orderMainService.getByOrderNo("PRT202607280001");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("根据订单号获取订单-不存在")
    void testGetByOrderNo_NotExists() {
        when(orderMainMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        OrderMain result = orderMainService.getByOrderNo("NOTEXIST");

        assertNull(result);
    }

    @Test
    @DisplayName("生成订单号-格式正确且唯一性")
    void testGenerateOrderNo() {
        String orderNo1 = orderMainService.generateOrderNo();
        String orderNo2 = orderMainService.generateOrderNo();

        assertNotNull(orderNo1);
        assertNotNull(orderNo2);
        assertEquals(20, orderNo1.length()); // yyyyMMddHHmmss + 6位随机数 = 14 + 6 = 20
        assertEquals(20, orderNo2.length());
        assertNotEquals(orderNo1, orderNo2);
        // 验证前缀为当前日期
        String today = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertTrue(orderNo1.startsWith(today));
    }

    @Test
    @DisplayName("更新订单状态-成功")
    void testUpdateStatus_Success() {
        when(orderMainMapper.updateById(any(OrderMain.class))).thenReturn(1);

        boolean result = orderMainService.updateStatus(1L, 2);

        assertTrue(result);
        verify(orderMainMapper).updateById(any(OrderMain.class));
    }

    @Test
    @DisplayName("更新订单状态-失败")
    void testUpdateStatus_Fail() {
        when(orderMainMapper.updateById(any(OrderMain.class))).thenReturn(0);

        boolean result = orderMainService.updateStatus(1L, 2);

        assertFalse(result);
    }

    @Test
    @DisplayName("更新支付状态为已支付-同时更新订单状态为待打印")
    void testUpdatePayStatus_Paid() {
        when(orderMainMapper.updateById(any(OrderMain.class))).thenReturn(1);

        boolean result = orderMainService.updatePayStatus(1L, 1, new Date(), "nonce123");

        assertTrue(result);
        // 捕获参数验证
        org.mockito.ArgumentCaptor<OrderMain> captor = org.mockito.ArgumentCaptor.forClass(OrderMain.class);
        verify(orderMainMapper).updateById(captor.capture());
        assertEquals(1, captor.getValue().getPayStatus());
        assertEquals(2, captor.getValue().getStatus()); // 已支付同时更新状态为待打印
    }

    @Test
    @DisplayName("更新支付状态为未支付-不更新订单状态")
    void testUpdatePayStatus_Unpaid() {
        when(orderMainMapper.updateById(any(OrderMain.class))).thenReturn(1);

        boolean result = orderMainService.updatePayStatus(1L, 0, null, null);

        assertTrue(result);
        org.mockito.ArgumentCaptor<OrderMain> captor = org.mockito.ArgumentCaptor.forClass(OrderMain.class);
        verify(orderMainMapper).updateById(captor.capture());
        assertEquals(0, captor.getValue().getPayStatus());
        assertNull(captor.getValue().getStatus()); // 未支付不更新状态
    }
}
