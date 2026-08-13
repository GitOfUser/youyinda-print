package com.youyinda.service.impl;

import com.youyinda.common.BusinessException;
import com.youyinda.common.enums.ErrorCodeEnum;
import com.youyinda.dto.PrintFileDTO;
import com.youyinda.entity.PrintBasePrice;
import com.youyinda.enums.PrintScene;
import com.youyinda.service.PrintBasePriceService;
import com.youyinda.service.PrintSceneService;
import com.youyinda.util.PriceCalculateUtil;
import com.youyinda.vo.PrintSceneVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 多场景打印服务实现
 * 提供预设场景模板与自定义场景，自动填充打印参数并预估价格
 */
@Slf4j
@Service
public class PrintSceneServiceImpl implements PrintSceneService {

    /** 默认服务商（66印，价格更有竞争力） */
    private static final String DEFAULT_PROVIDER = "liuliuyin";
    /** 装订附加费：骑马钉 2 元，胶装 5 元 */
    private static final BigDecimal BINDING_STAPLE_FEE = new BigDecimal("2.00");
    private static final BigDecimal BINDING_GLUE_FEE = new BigDecimal("5.00");
    /** 默认快递配送费 */
    private static final BigDecimal DEFAULT_EXPRESS_FEE = new BigDecimal("5.00");

    @Resource
    private PrintBasePriceService printBasePriceService;

    @Override
    public List<PrintSceneVO> listScenes() {
        List<PrintSceneVO> result = new ArrayList<>();
        for (PrintScene scene : PrintScene.values()) {
            PrintSceneVO vo = new PrintSceneVO();
            vo.setSceneCode(scene.getCode());
            vo.setSceneName(scene.getName());
            vo.setPaperType(scene.getPaperType());
            vo.setColorType(scene.getColorType());
            vo.setPrintSide(scene.getPrintSide());
            vo.setBinding(scene.getBinding());
            vo.setDescription(scene.getDesc());
            result.add(vo);
        }
        return result;
    }

    @Override
    public PrintSceneVO applyScene(String sceneCode, List<PrintFileDTO> files) {
        PrintScene scene = PrintScene.getByCode(sceneCode);
        if (scene == null) {
            throw new BusinessException(ErrorCodeEnum.PARAM_ERROR, "未知打印场景：" + sceneCode);
        }
        return buildSceneVO(scene.getCode(), scene.getName(), scene.getPaperType(),
                scene.getColorType(), scene.getPrintSide(), scene.getBinding(),
                scene.getDesc(), files);
    }

    @Override
    public PrintSceneVO customScene(String paperType, String colorType, String printSide,
                                    String binding, Integer copies, List<PrintFileDTO> files) {
        return buildSceneVO("custom", "自定义场景", paperType, colorType, printSide,
                binding, "用户自定义打印场景", files, copies);
    }

    // ===================== 私有辅助方法 =====================

    /**
     * 构建场景VO（使用场景默认份数）
     */
    private PrintSceneVO buildSceneVO(String code, String name, String paperType, String colorType,
                                      String printSide, String binding, String desc, List<PrintFileDTO> files) {
        return buildSceneVO(code, name, paperType, colorType, printSide, binding, desc, files, null);
    }

    /**
     * 构建场景VO并计算预估总价
     */
    private PrintSceneVO buildSceneVO(String code, String name, String paperType, String colorType,
                                      String printSide, String binding, String desc,
                                      List<PrintFileDTO> files, Integer defaultCopies) {
        PrintSceneVO vo = new PrintSceneVO();
        vo.setSceneCode(code);
        vo.setSceneName(name);
        vo.setPaperType(paperType);
        vo.setColorType(colorType);
        vo.setPrintSide(printSide);
        vo.setBinding(binding);
        vo.setDescription(desc);
        vo.setFiles(files);

        if (CollectionUtils.isEmpty(files)) {
            vo.setEstimatedTotal(BigDecimal.ZERO);
            return vo;
        }

        // 单双面映射为 single/double
        String singleDouble = "双面".equals(printSide) ? "double" : "single";
        // 查询基础价（优先 66印，降级小猴云印）
        PrintBasePrice base = printBasePriceService.getBySpec(DEFAULT_PROVIDER, paperType, colorType, singleDouble);
        if (base == null) {
            base = printBasePriceService.getBySpec("xiaohou", paperType, colorType, singleDouble);
        }
        if (base == null) {
            throw new BusinessException(ErrorCodeEnum.PRICE_CALC_ERROR,
                    "未找到打印基础价格：" + paperType + "/" + colorType + "/" + singleDouble);
        }

        BigDecimal bindingFee = calcBindingFee(binding);
        BigDecimal orderTotal = BigDecimal.ZERO;
        for (PrintFileDTO file : files) {
            int copies = defaultCopies != null ? defaultCopies : (file.getCopies() == null ? 1 : file.getCopies());
            int pages = file.getPages() == null ? 0 : file.getPages();
            int billingPages = "double".equalsIgnoreCase(singleDouble)
                    ? PriceCalculateUtil.calculatePrintPageCount(pages) : pages;
            BigDecimal expressFee = DEFAULT_EXPRESS_FEE;
            PriceCalculateUtil.PriceDetail detail = PriceCalculateUtil.calculatePrintPrice(
                    base, billingPages, bindingFee, BigDecimal.ZERO, expressFee);
            BigDecimal singleTotal = detail.getTotalPrice();
            orderTotal = orderTotal.add(singleTotal.multiply(new BigDecimal(copies)));
        }
        vo.setEstimatedTotal(orderTotal);
        return vo;
    }

    /**
     * 计算装订费
     */
    private BigDecimal calcBindingFee(String binding) {
        if (binding == null) {
            return BigDecimal.ZERO;
        }
        if ("骑马钉".equals(binding) || "1".equals(binding)) {
            return BINDING_STAPLE_FEE;
        }
        if ("胶装".equals(binding) || "2".equals(binding)) {
            return BINDING_GLUE_FEE;
        }
        return BigDecimal.ZERO;
    }
}
