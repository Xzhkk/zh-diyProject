package com.xzh.service.impl;

import com.xzh.annotation.TargetDataSource;
import com.xzh.base.BaseServiceImpl;
import com.xzh.bean.CmServiceLog;
import com.xzh.constants.DataSourceConstants;
import com.xzh.mapper.CmServiceLogMapper;
import com.xzh.service.CmServiceLogService;
import org.springframework.stereotype.Service;

@Service
@TargetDataSource(DataSourceConstants.LOG) // 指定该 Service 操作日志库
public class CmServiceLogServiceImpl extends BaseServiceImpl<CmServiceLogMapper, CmServiceLog> implements CmServiceLogService {
    @Override
    @TargetDataSource(DataSourceConstants.LOG)
    public boolean save(CmServiceLog entity) {
        return super.save(entity);
    }
}
