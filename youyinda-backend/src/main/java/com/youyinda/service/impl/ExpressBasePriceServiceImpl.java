package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.ExpressBasePrice;
import com.youyinda.mapper.ExpressBasePriceMapper;
import com.youyinda.service.ExpressBasePriceService;
import org.springframework.stereotype.Service;

/**
 * 快递基础价格服务实现
 */
@Service
public class ExpressBasePriceServiceImpl extends ServiceImpl<ExpressBasePriceMapper, ExpressBasePrice> implements ExpressBasePriceService {

    @Override
    public ExpressBasePrice getByParams(String courier, String fromProvince, String toProvince) {
        QueryWrapper<ExpressBasePrice> queryWrapper = new QueryWrapper<>();
        if (courier != null) {
            queryWrapper.eq("courier", courier);
        }
        if (fromProvince != null) {
            queryWrapper.eq("from_province", fromProvince);
        }
        if (toProvince != null) {
            queryWrapper.eq("to_province", toProvince);
        }
        return baseMapper.selectOne(queryWrapper);
    }
}
