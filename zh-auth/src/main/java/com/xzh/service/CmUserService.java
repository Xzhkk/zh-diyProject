package com.xzh.service;

import com.xzh.bean.CmUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xzh.result.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
* @author kk
* @description 针对表【cm_user】的数据库操作Service
* @createDate 2026-03-05 23:08:06
*/
public interface CmUserService extends IService<CmUser> {
    BaseResponse login(Map<String,Object> map);

    BaseResponse getCaptcha(Map<String,Object> map);

    BaseResponse register(Map<String,Object> map);

    void exportUser(HttpServletResponse response,Map<String,Object> map);

    BaseResponse singleExport100W(Map<String,Object> map);
}
