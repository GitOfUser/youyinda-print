package com.youyinda.vo;

import com.youyinda.dto.PrintFileDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 打印场景预览VO
 * 用于返回场景模板参数与预估价格
 */
@Data
public class PrintSceneVO {

    /**
     * 场景编码
     */
    private String sceneCode;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 纸张类型
     */
    private String paperType;

    /**
     * 色彩模式
     */
    private String colorType;

    /**
     * 单双面
     */
    private String printSide;

    /**
     * 装订方式
     */
    private String binding;

    /**
     * 场景描述
     */
    private String description;

    /**
     * 文件列表（含文件名、页数、份数）
     */
    private List<PrintFileDTO> files;

    /**
     * 预估总价
     */
    private BigDecimal estimatedTotal;
}
