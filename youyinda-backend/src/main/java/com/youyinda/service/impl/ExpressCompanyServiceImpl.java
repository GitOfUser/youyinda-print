package com.youyinda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.ExpressCompany;
import com.youyinda.mapper.ExpressCompanyMapper;
import com.youyinda.service.ExpressCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ExpressCompanyServiceImpl extends ServiceImpl<ExpressCompanyMapper, ExpressCompany> implements ExpressCompanyService {

    @Override
    public List<ExpressCompany> listEnabledCompanies() {
        LambdaQueryWrapper<ExpressCompany> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExpressCompany::getStatus, 1)
                .orderByAsc(ExpressCompany::getSortOrder);
        return this.list(wrapper);
    }
}
