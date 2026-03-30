package com.xzh.service.impl;

import com.xzh.base.BaseServiceImpl;
import com.xzh.bean.CmDict;
import com.xzh.mapper.CmDictMapper;
import com.xzh.service.CmDictService;
import org.springframework.stereotype.Service;

@Service
public class CmDictServiceImpl extends BaseServiceImpl<CmDictMapper, CmDict> implements CmDictService {

    @Override
    public String translate(String classCode, Object value) {
        return "";
    }
}
