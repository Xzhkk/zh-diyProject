package com.xzh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xzh.bean.CmDynamicExcelConfig;

public interface CmDynamicExcelConfigService extends IService<CmDynamicExcelConfig> {

    CmDynamicExcelConfig getConfigByCode(String code);

}
