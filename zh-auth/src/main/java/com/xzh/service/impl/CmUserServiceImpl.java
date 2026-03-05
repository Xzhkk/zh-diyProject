package com.xzh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzh.bean.CmUser;
import com.xzh.service.CmUserService;
import com.xzh.mapper.CmUserMapper;
import org.springframework.stereotype.Service;

/**
* @author kk
* @description 针对表【cm_user】的数据库操作Service实现
* @createDate 2026-03-05 23:08:06
*/
@Service
public class CmUserServiceImpl extends ServiceImpl<CmUserMapper, CmUser>
    implements CmUserService{

}




