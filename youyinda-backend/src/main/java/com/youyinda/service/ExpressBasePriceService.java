package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.ExpressBasePrice;

/**
 * 快递基础价格服务
 */
public interface ExpressBasePriceService extends IService<ExpressBasePrice> {
    
    /**
     * 根据参数获取快递基础价格
     * @param courier 快递公司
     * @param fromProvince 寄件省份
     * @param toProvince 收件省份
     * @return 快递基础价格
     */
    ExpressBasePrice getByParams(String courier, String fromProvince, String toProvince);
}
