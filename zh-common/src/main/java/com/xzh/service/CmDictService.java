package com.xzh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xzh.bean.CmDict;

public interface CmDictService extends IService<CmDict> {
    String translate(String classCode,Object value);
}
