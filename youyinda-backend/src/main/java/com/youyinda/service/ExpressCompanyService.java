package com.youyinda.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyinda.entity.ExpressCompany;

import java.util.List;

public interface ExpressCompanyService extends IService<ExpressCompany> {

    List<ExpressCompany> listEnabledCompanies();
}
