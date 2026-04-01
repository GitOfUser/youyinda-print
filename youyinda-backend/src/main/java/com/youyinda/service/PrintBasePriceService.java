package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.PrintBasePrice;

/**
 * 打印基础价格服务
 */
public interface PrintBasePriceService extends IService<PrintBasePrice> {
    
    /**
     * 根据参数获取打印基础价格
     * @param paperType 纸张类型
     * @param colorType 颜色类型
     * @param printSide 打印面数
     * @return 打印基础价格
     */
    PrintBasePrice getByParams(String paperType, String colorType, String printSide);
}
