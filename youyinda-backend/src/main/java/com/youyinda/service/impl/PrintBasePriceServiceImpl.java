package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.PrintBasePrice;
import com.youyinda.mapper.PrintBasePriceMapper;
import com.youyinda.service.PrintBasePriceService;
import org.springframework.stereotype.Service;

/**
 * 打印基础价格服务实现
 */
@Service
public class PrintBasePriceServiceImpl extends ServiceImpl<PrintBasePriceMapper, PrintBasePrice> implements PrintBasePriceService {

    @Override
    public PrintBasePrice getByParams(String paperType, String colorType, String printSide) {
        QueryWrapper<PrintBasePrice> queryWrapper = new QueryWrapper<>();
        if (paperType != null) {
            queryWrapper.eq("paper_type", paperType);
        }
        if (colorType != null) {
            queryWrapper.eq("color_type", colorType);
        }
        if (printSide != null) {
            queryWrapper.eq("print_side", printSide);
        }
        return baseMapper.selectOne(queryWrapper);
    }
}
