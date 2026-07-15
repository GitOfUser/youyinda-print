package com.youyinda.controller;

import com.youyinda.common.BusinessException;
import com.youyinda.common.enums.ErrorCodeEnum;
import com.youyinda.dto.PriceCalculateDTO;
import com.youyinda.entity.PrintBasePrice;
import com.youyinda.entity.ExpressBasePrice;
import com.youyinda.service.PrintBasePriceService;
import com.youyinda.service.ExpressBasePriceService;
import com.youyinda.util.PriceCalculateUtil;
import com.youyinda.vo.PriceCalculateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * 价格引擎控制器
 * 处理价格计算相关接口
 */
@Slf4j
@RestController
@RequestMapping("/v1/price")
public class PriceController {

    @Autowired
    private PrintBasePriceService printBasePriceService;

    @Autowired
    private ExpressBasePriceService expressBasePriceService;

    /**
     * 价格预览接口
     * @param priceCalculateDTO 价格计算请求参数
     * @return 价格计算结果
     */
    @PostMapping("/calculate")
    public com.youyinda.common.R<PriceCalculateVO> calculatePrice(@Valid @RequestBody PriceCalculateDTO priceCalculateDTO) {
        try {
            PriceCalculateVO result = new PriceCalculateVO();
            
            if ("print".equals(priceCalculateDTO.getBusinessType())) {
                // 计算打印价格
                calculatePrintPrice(priceCalculateDTO, result);
            } else if ("express".equals(priceCalculateDTO.getBusinessType())) {
                // 计算快递价格
                calculateExpressPrice(priceCalculateDTO, result);
            } else {
                throw new BusinessException(ErrorCodeEnum.PARAM_ERROR, "业务类型不支持");
            }
            
            return com.youyinda.common.R.success(result);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("价格计算失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.PRICE_CALC_ERROR, "价格计算失败");
        }
    }

    /**
     * 计算打印价格
     */
    private void calculatePrintPrice(PriceCalculateDTO dto, PriceCalculateVO result) {
        // 构建查询条件
        PrintBasePrice printBasePrice = printBasePriceService.getByParams(
                dto.getPaperType(),
                dto.getColorType(),
                dto.getPrintSide()
        );
        
        if (printBasePrice == null) {
            throw new BusinessException(ErrorCodeEnum.PRICE_CALC_ERROR, "未找到对应的打印价格配置");
        }
        
        // 计算装订费用（这里简化处理，实际应该根据装订类型查询）
        BigDecimal bindingFee = BigDecimal.ZERO;
        if ("staple".equals(dto.getBindingType())) {
            bindingFee = new BigDecimal(2);
        } else if ("glue".equals(dto.getBindingType())) {
            bindingFee = new BigDecimal(5);
        }
        
        // 计算增值服务费用（这里简化处理，实际应该根据增值服务类型查询）
        BigDecimal valueAddedFee = BigDecimal.ZERO;
        if ("lamination".equals(dto.getValueAddedService())) {
            valueAddedFee = new BigDecimal(3);
        }
        
        // 计算快递费用（这里简化处理，实际应该根据地址和重量计算）
        BigDecimal expressFee = new BigDecimal(10);
        
        // 计算价格详情
        PriceCalculateUtil.PriceDetail priceDetail = PriceCalculateUtil.calculatePrintPrice(
                printBasePrice,
                dto.getPages(),
                bindingFee,
                valueAddedFee,
                expressFee
        );
        
        // 构建响应结果
        BeanUtils.copyProperties(priceDetail, result);
        result.setCalculationNotes("打印价格计算：单页价格 = MAX(第三方基础价 × (1 + 盈利比例), 最低限价)，计费页数=奇数进一，总费用=单页价×计费页数+装订+增值+快递");
    }

    /**
     * 计算快递价格
     */
    private void calculateExpressPrice(PriceCalculateDTO dto, PriceCalculateVO result) {
        // 构建查询条件
        ExpressBasePrice expressBasePrice = expressBasePriceService.getByParams(
                dto.getCourier(),
                dto.getFromProvince(),
                dto.getToProvince()
        );
        
        if (expressBasePrice == null) {
            throw new BusinessException(ErrorCodeEnum.PRICE_CALC_ERROR, "未找到对应的快递价格配置");
        }
        
        // 计算价格详情
        PriceCalculateUtil.PriceDetail priceDetail = PriceCalculateUtil.calculateExpressPrice(
                expressBasePrice,
                dto.getWeight()
        );
        
        // 构建响应结果
        BeanUtils.copyProperties(priceDetail, result);
        result.setCalculationNotes("快递价格计算：第三方运费=首重+ceil((总重−首重)/续重单位)×续重单价，最终售价=MAX(第三方运费×(1+盈利比例), 第三方运费+最低固定盈利额)");
    }
}
