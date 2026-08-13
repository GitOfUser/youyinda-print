package com.youyinda.service;

import com.youyinda.dto.PrintFileDTO;
import com.youyinda.vo.PrintSceneVO;

import java.util.List;

/**
 * 多场景打印服务
 * 封装预设场景模板，方便用户快速选择打印参数并预估价格
 */
public interface PrintSceneService {

    /**
     * 列出所有预设打印场景
     * @return 场景列表
     */
    List<PrintSceneVO> listScenes();

    /**
     * 应用场景模板，自动填充打印参数并计算预估价格
     * @param sceneCode 场景编码（见 PrintScene 枚举）
     * @param files 文件列表
     * @return 填充后的场景预览（含打印参数与预估总价）
     */
    PrintSceneVO applyScene(String sceneCode, List<PrintFileDTO> files);

    /**
     * 自定义场景
     * @param paperType 纸张类型（A4/A3/照片纸）
     * @param colorType 色彩模式（黑白/彩色）
     * @param printSide 单双面（单面/双面）
     * @param binding 装订方式（无/骑马钉/胶装）
     * @param copies 份数
     * @param files 文件列表
     * @return 自定义场景预览（含预估总价）
     */
    PrintSceneVO customScene(String paperType, String colorType, String printSide,
                             String binding, Integer copies, List<PrintFileDTO> files);
}
