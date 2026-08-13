package com.youyinda.util;

import com.youyinda.entity.ExpressBasePrice;
import com.youyinda.entity.PrintBasePrice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 价格计算工具类单元测试
 * 覆盖打印/快递全量计价公式与边界场景
 */
class PriceCalculateUtilTest {

    // ===================== 打印单页售价测试 =====================

    @Test
    @DisplayName("打印单页售价-最低限价高于按比例价，取最低限价")
    void testCalculatePrintPrice_TakeMinPrice() {
        // 基础价0.08，盈利30% → 0.104，最低限价0.15 → 取0.15
        BigDecimal result = PriceCalculateUtil.calculatePrintPrice(
                new BigDecimal("0.08"), new BigDecimal("30"), new BigDecimal("0.15"));
        assertEquals(0, new BigDecimal("0.15").compareTo(result));
    }

    @Test
    @DisplayName("打印单页售价-按比例价高于最低限价，取按比例价")
    void testCalculatePrintPrice_TakeProfitRatio() {
        // 基础价0.50，盈利30% → 0.65，最低限价0.15 → 取0.65
        BigDecimal result = PriceCalculateUtil.calculatePrintPrice(
                new BigDecimal("0.50"), new BigDecimal("30"), new BigDecimal("0.15"));
        assertEquals(0, new BigDecimal("0.65").compareTo(result));
    }

    @Test
    @DisplayName("打印单页售价-两者相等")
    void testCalculatePrintPrice_Equal() {
        // 基础价0.10，盈利50% → 0.15，最低限价0.15 → 取0.15
        BigDecimal result = PriceCalculateUtil.calculatePrintPrice(
                new BigDecimal("0.10"), new BigDecimal("50"), new BigDecimal("0.15"));
        assertEquals(0, new BigDecimal("0.15").compareTo(result));
    }

    @Test
    @DisplayName("打印单页售价-基础价为0")
    void testCalculatePrintPrice_ZeroBasePrice() {
        BigDecimal result = PriceCalculateUtil.calculatePrintPrice(
                BigDecimal.ZERO, new BigDecimal("30"), new BigDecimal("0.10"));
        assertEquals(0, new BigDecimal("0.10").compareTo(result));
    }

    // ===================== 打印计费页数测试 =====================

    @Test
    @DisplayName("打印计费页数-0页返回0")
    void testCalculatePrintPageCount_Zero() {
        assertEquals(0, PriceCalculateUtil.calculatePrintPageCount(0));
    }

    @Test
    @DisplayName("打印计费页数-负数页返回0")
    void testCalculatePrintPageCount_Negative() {
        assertEquals(0, PriceCalculateUtil.calculatePrintPageCount(-5));
    }

    @Test
    @DisplayName("打印计费页数-奇数页奇数进一")
    void testCalculatePrintPageCount_Odd() {
        // 5页双面 = 3张（奇数进一）
        assertEquals(3, PriceCalculateUtil.calculatePrintPageCount(5));
    }

    @Test
    @DisplayName("打印计费页数-偶数页整除")
    void testCalculatePrintPageCount_Even() {
        // 10页双面 = 5张
        assertEquals(5, PriceCalculateUtil.calculatePrintPageCount(10));
    }

    @Test
    @DisplayName("打印计费页数-1页返回1")
    void testCalculatePrintPageCount_One() {
        assertEquals(1, PriceCalculateUtil.calculatePrintPageCount(1));
    }

    // ===================== 打印总费用测试 =====================

    @Test
    @DisplayName("打印总费用-正常计算")
    void testCalculatePrintTotalPrice_Normal() {
        // 单页0.15 * 10页 + 装订0 + 增值0 + 快递5.00 = 6.50
        BigDecimal result = PriceCalculateUtil.calculatePrintTotalPrice(
                new BigDecimal("0.15"), 10, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("5.00"));
        assertEquals(0, new BigDecimal("6.50").compareTo(result));
    }

    @Test
    @DisplayName("打印总费用-含装订费和增值费")
    void testCalculatePrintTotalPrice_WithBindingAndValueAdded() {
        // 单页0.15 * 20页 + 胶装5.00 + 覆膜10.00 + 快递5.00 = 3.00 + 5.00 + 10.00 + 5.00 = 23.00
        BigDecimal result = PriceCalculateUtil.calculatePrintTotalPrice(
                new BigDecimal("0.15"), 20, new BigDecimal("5.00"), new BigDecimal("10.00"), new BigDecimal("5.00"));
        assertEquals(0, new BigDecimal("23.00").compareTo(result));
    }

    // ===================== 快递基础运费测试 =====================

    @Test
    @DisplayName("快递基础运费-重量为0返回首重价")
    void testCalculateExpressBasePrice_ZeroWeight() {
        BigDecimal result = PriceCalculateUtil.calculateExpressBasePrice(
                new BigDecimal("1.0"), new BigDecimal("10.00"),
                new BigDecimal("1.0"), new BigDecimal("5.00"), BigDecimal.ZERO);
        assertEquals(0, new BigDecimal("10.00").compareTo(result));
    }

    @Test
    @DisplayName("快递基础运费-重量为负返回首重价")
    void testCalculateExpressBasePrice_NegativeWeight() {
        BigDecimal result = PriceCalculateUtil.calculateExpressBasePrice(
                new BigDecimal("1.0"), new BigDecimal("10.00"),
                new BigDecimal("1.0"), new BigDecimal("5.00"), new BigDecimal("-1.0"));
        assertEquals(0, new BigDecimal("10.00").compareTo(result));
    }

    @Test
    @DisplayName("快递基础运费-重量刚好等于首重")
    void testCalculateExpressBasePrice_EqualFirstWeight() {
        BigDecimal result = PriceCalculateUtil.calculateExpressBasePrice(
                new BigDecimal("1.0"), new BigDecimal("10.00"),
                new BigDecimal("1.0"), new BigDecimal("5.00"), new BigDecimal("1.0"));
        assertEquals(0, new BigDecimal("10.00").compareTo(result));
    }

    @Test
    @DisplayName("快递基础运费-重量略超首重，续重向上取整")
    void testCalculateExpressBasePrice_SlightlyOver() {
        // 重量1.1kg，首重1kg/10元，续重1kg/5元 → 1个续重 → 15元
        BigDecimal result = PriceCalculateUtil.calculateExpressBasePrice(
                new BigDecimal("1.0"), new BigDecimal("10.00"),
                new BigDecimal("1.0"), new BigDecimal("5.00"), new BigDecimal("1.1"));
        assertEquals(0, new BigDecimal("15.00").compareTo(result));
    }

    @Test
    @DisplayName("快递基础运费-重量是续重单位整数倍")
    void testCalculateExpressBasePrice_ExactMultiple() {
        // 重量3kg，首重1kg/10元，续重1kg/5元 → 2个续重 → 20元
        BigDecimal result = PriceCalculateUtil.calculateExpressBasePrice(
                new BigDecimal("1.0"), new BigDecimal("10.00"),
                new BigDecimal("1.0"), new BigDecimal("5.00"), new BigDecimal("3.0"));
        assertEquals(0, new BigDecimal("20.00").compareTo(result));
    }

    @Test
    @DisplayName("快递基础运费-续重单位0.5kg，非整数倍向上取整")
    void testCalculateExpressBasePrice_HalfUnit() {
        // 重量1.3kg，首重1kg/10元，续重0.5kg/3元 → 超出0.3kg，向上取整=1个续重 → 13元
        BigDecimal result = PriceCalculateUtil.calculateExpressBasePrice(
                new BigDecimal("1.0"), new BigDecimal("10.00"),
                new BigDecimal("0.5"), new BigDecimal("3.00"), new BigDecimal("1.3"));
        assertEquals(0, new BigDecimal("13.00").compareTo(result));
    }

    // ===================== 快递最终售价测试 =====================

    @Test
    @DisplayName("快递最终售价-按比例盈利高于固定盈利")
    void testCalculateExpressPrice_ProfitRatioHigher() {
        // 基础运费10元，盈利比例20% → 12元，固定盈利2元 → 10+2=12元 → 两者相等
        BigDecimal result = PriceCalculateUtil.calculateExpressPrice(
                new BigDecimal("10.00"), new BigDecimal("20"), new BigDecimal("2.00"));
        assertEquals(0, new BigDecimal("12.00").compareTo(result));
    }

    @Test
    @DisplayName("快递最终售价-固定盈利高于按比例盈利")
    void testCalculateExpressPrice_MinProfitHigher() {
        // 基础运费10元，盈利比例10% → 11元，固定盈利5元 → 10+5=15元 → 取15
        BigDecimal result = PriceCalculateUtil.calculateExpressPrice(
                new BigDecimal("10.00"), new BigDecimal("10"), new BigDecimal("5.00"));
        assertEquals(0, new BigDecimal("15.00").compareTo(result));
    }

    // ===================== 使用实体计算打印价格测试 =====================

    @Test
    @DisplayName("使用实体计算打印价格-正常场景")
    void testCalculatePrintPrice_WithEntity_Normal() {
        PrintBasePrice base = new PrintBasePrice();
        base.setBasePrice(0.10);
        base.setMinPrice(0.18);
        base.setProfitRatio(30.0);

        // 单份价MAX(0.10*1.3,0.18)=0.18；10页*0.18=1.80 + 装订2.00 + 快递5.00 = 8.80
        PriceCalculateUtil.PriceDetail detail = PriceCalculateUtil.calculatePrintPrice(
                base, 10, new BigDecimal("2.00"), BigDecimal.ZERO, new BigDecimal("5.00"));

        assertNotNull(detail);
        assertEquals(0, new BigDecimal("0.18").compareTo(detail.getUnitPrice()));
        assertEquals(10, detail.getBillingPages());
        assertEquals(0, new BigDecimal("8.80").compareTo(detail.getTotalPrice()));
    }

    @Test
    @DisplayName("使用实体计算打印价格-实体为空抛异常")
    void testCalculatePrintPrice_WithEntity_Null() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> PriceCalculateUtil.calculatePrintPrice(null, 10, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("5.00")));
        assertTrue(ex.getMessage().contains("打印基础价格信息不能为空"));
    }

    // ===================== 使用实体计算快递价格测试 =====================

    @Test
    @DisplayName("使用实体计算快递价格-正常场景")
    void testCalculateExpressPrice_WithEntity_Normal() {
        ExpressBasePrice base = new ExpressBasePrice();
        base.setFirstWeight(1.0);
        base.setFirstPrice(10.0);
        base.setContinueWeight(1.0);
        base.setContinuePrice(5.0);
        base.setProfitRatio(20.0);
        base.setMinProfit(2.0);

        // 重量2kg → 首重10 + 1个续重5 = 15元基础运费
        // 最终售价MAX(15*1.2, 15+2) = MAX(18, 17) = 18
        PriceCalculateUtil.PriceDetail detail = PriceCalculateUtil.calculateExpressPrice(base, 2.0);

        assertNotNull(detail);
        assertEquals(0, new BigDecimal("15.00").compareTo(detail.getBasePrice()));
        assertEquals(0, new BigDecimal("18.00").compareTo(detail.getTotalPrice()));
        assertEquals(0, new BigDecimal("3.00").compareTo(detail.getProfit()));
    }

    @Test
    @DisplayName("使用实体计算快递价格-实体为空抛异常")
    void testCalculateExpressPrice_WithEntity_Null() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> PriceCalculateUtil.calculateExpressPrice(null, 1.0));
        assertTrue(ex.getMessage().contains("快递基础价格信息不能为空"));
    }
}
