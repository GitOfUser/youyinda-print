package com.youyinda.util;

import com.youyinda.entity.PrintBasePrice;
import com.youyinda.entity.ExpressBasePrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 价格计算工具类
 * 封装完整的计价逻辑，无计算漏洞，边界场景有兜底校验
 */
@Slf4j
@Component
public class PriceCalculateUtil {

    /**
     * 计算打印单页最终售价
     * 公式：单页最终售价 = MAX( third基础价 × (1 + 盈利比例), 最低限价 )
     * @param basePrice 第三方基础价
     * @param profitRatio 盈利比例（%）
     * @param minPrice 最低限价
     * @return 单页最终售价
     */
    public static BigDecimal calculatePrintPrice(BigDecimal basePrice, BigDecimal profitRatio, BigDecimal minPrice) {
        // 计算基础价格 × (1 + 盈利比例)
        BigDecimal priceWithProfit = basePrice.multiply(BigDecimal.ONE.add(profitRatio.divide(new BigDecimal(100))));
        // 取最大值
        return priceWithProfit.max(minPrice);
    }

    /**
     * 计算打印计费页数（奇数进一）
     * @param actualPages 实际页数
     * @return 计费页数
     */
    public static int calculatePrintPageCount(int actualPages) {
        if (actualPages <= 0) {
            return 0;
        }
        return (actualPages + 1) / 2 * 2; // 奇数进一
    }

    /**
     * 计算打印总费用
     * 公式：总费用=单页价×计费页数+装订+增值+快递
     * @param unitPrice 单页价格
     * @param pageCount 计费页数
     * @param bindingFee 装订费用
     * @param valueAddedFee 增值费用
     * @param expressFee 快递费用
     * @return 总费用
     */
    public static BigDecimal calculatePrintTotalPrice(BigDecimal unitPrice, int pageCount, BigDecimal bindingFee, BigDecimal valueAddedFee, BigDecimal expressFee) {
        BigDecimal pageFee = unitPrice.multiply(new BigDecimal(pageCount));
        return pageFee.add(bindingFee).add(valueAddedFee).add(expressFee);
    }

    /**
     * 计算快递第三方运费
     * 公式：第三方运费=首重+ceil((总重−首重)/续重单位)×续重单价
     * @param firstWeight 首重（kg）
     * @param firstPrice 首重价格
     * @param continueWeight 续重单位（kg）
     * @param continuePrice 续重单价
     * @param totalWeight 总重量（kg）
     * @return 第三方运费
     */
    public static BigDecimal calculateExpressBasePrice(BigDecimal firstWeight, BigDecimal firstPrice, BigDecimal continueWeight, BigDecimal continuePrice, BigDecimal totalWeight) {
        if (totalWeight.compareTo(BigDecimal.ZERO) <= 0) {
            return firstPrice;
        }
        if (totalWeight.compareTo(firstWeight) <= 0) {
            return firstPrice;
        }
        // 计算超出首重的重量
        BigDecimal excessWeight = totalWeight.subtract(firstWeight);
        // 计算续重次数（向上取整）
        int continueTimes = excessWeight.divide(continueWeight, 0, RoundingMode.CEILING).intValue();
        // 计算续重费用
        BigDecimal continueFee = continuePrice.multiply(new BigDecimal(continueTimes));
        // 总费用
        return firstPrice.add(continueFee);
    }

    /**
     * 计算快递最终售价
     * 公式：最终售价=MAX( 第三方运费×(1+盈利比例), 第三方运费+最低固定盈利额 )
     * @param basePrice 第三方运费
     * @param profitRatio 盈利比例（%）
     * @param minProfit 最低固定盈利额
     * @return 最终售价
     */
    public static BigDecimal calculateExpressPrice(BigDecimal basePrice, BigDecimal profitRatio, BigDecimal minProfit) {
        // 计算基础价格 × (1 + 盈利比例)
        BigDecimal priceWithProfit = basePrice.multiply(BigDecimal.ONE.add(profitRatio.divide(new BigDecimal(100))));
        // 计算基础价格 + 最低固定盈利额
        BigDecimal priceWithMinProfit = basePrice.add(minProfit);
        // 取最大值
        return priceWithProfit.max(priceWithMinProfit);
    }

    /**
     * 计算打印价格（使用实体类）
     * @param printBasePrice 打印基础价格
     * @param actualPages 实际页数
     * @param bindingFee 装订费用
     * @param valueAddedFee 增值费用
     * @param expressFee 快递费用
     * @return 价格详情
     */
    public static PriceDetail calculatePrintPrice(PrintBasePrice printBasePrice, int actualPages, BigDecimal bindingFee, BigDecimal valueAddedFee, BigDecimal expressFee) {
        if (printBasePrice == null) {
            throw new IllegalArgumentException("打印基础价格信息不能为空");
        }

        // 计算计费页数
        int billingPages = calculatePrintPageCount(actualPages);
        // 计算单页价格
        BigDecimal unitPrice = calculatePrintPrice(
                new BigDecimal(printBasePrice.getBasePrice()),
                new BigDecimal(printBasePrice.getProfitRatio()),
                new BigDecimal(printBasePrice.getMinPrice())
        );
        // 计算总费用
        BigDecimal totalPrice = calculatePrintTotalPrice(unitPrice, billingPages, bindingFee, valueAddedFee, expressFee);
        // 计算第三方基础总费用
        BigDecimal baseTotalPrice = new BigDecimal(printBasePrice.getBasePrice())
                .multiply(new BigDecimal(billingPages))
                .add(bindingFee)
                .add(valueAddedFee)
                .add(expressFee);
        // 计算盈利金额
        BigDecimal profit = totalPrice.subtract(baseTotalPrice);

        return PriceDetail.builder()
                .basePrice(new BigDecimal(printBasePrice.getBasePrice()))
                .unitPrice(unitPrice)
                .billingPages(billingPages)
                .bindingFee(bindingFee)
                .valueAddedFee(valueAddedFee)
                .expressFee(expressFee)
                .baseTotalPrice(baseTotalPrice)
                .totalPrice(totalPrice)
                .profit(profit)
                .build();
    }

    /**
     * 计算快递价格（使用实体类）
     * @param expressBasePrice 快递基础价格
     * @param weight 重量（kg）
     * @return 价格详情
     */
    public static PriceDetail calculateExpressPrice(ExpressBasePrice expressBasePrice, double weight) {
        if (expressBasePrice == null) {
            throw new IllegalArgumentException("快递基础价格信息不能为空");
        }

        // 计算第三方基础运费
        BigDecimal basePrice = calculateExpressBasePrice(
                new BigDecimal(expressBasePrice.getFirstWeight()),
                new BigDecimal(expressBasePrice.getFirstPrice()),
                new BigDecimal(expressBasePrice.getContinueWeight()),
                new BigDecimal(expressBasePrice.getContinuePrice()),
                new BigDecimal(weight)
        );
        // 计算最终售价
        BigDecimal totalPrice = calculateExpressPrice(
                basePrice,
                new BigDecimal(expressBasePrice.getProfitRatio()),
                new BigDecimal(expressBasePrice.getMinProfit())
        );
        // 计算盈利金额
        BigDecimal profit = totalPrice.subtract(basePrice);

        return PriceDetail.builder()
                .basePrice(basePrice)
                .totalPrice(totalPrice)
                .profit(profit)
                .build();
    }

    /**
     * 价格详情类
     */
    @lombok.Builder
    @lombok.Data
    public static class PriceDetail {
        /**
         * 第三方基础价
         */
        private BigDecimal basePrice;

        /**
         * 单页价格
         */
        private BigDecimal unitPrice;

        /**
         * 计费页数
         */
        private int billingPages;

        /**
         * 装订费用
         */
        private BigDecimal bindingFee;

        /**
         * 增值费用
         */
        private BigDecimal valueAddedFee;

        /**
         * 快递费用
         */
        private BigDecimal expressFee;

        /**
         * 第三方基础总费用
         */
        private BigDecimal baseTotalPrice;

        /**
         * 最终总售价
         */
        private BigDecimal totalPrice;

        /**
         * 盈利金额
         */
        private BigDecimal profit;
    }
}
