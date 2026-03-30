package com.xzh.controller;

import com.xzh.model.CmUserQueryRequest;
import com.xzh.result.BaseResponse;
import com.xzh.service.CmUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class CmUserController {

    @Resource
    private CmUserService cmUserService;

    @RequestMapping("/getCaptcha")
    public BaseResponse getCaptcha(@RequestBody Map<String,Object> map) {
        return cmUserService.getCaptcha(map);
    }

    @RequestMapping("/register")
    public BaseResponse register(@RequestBody Map<String,Object> map) {
        return cmUserService.register(map);
    }

    @RequestMapping("/login")
    public BaseResponse login(@RequestBody Map<String,Object> map) {
        return cmUserService.login(map);
    }

    @RequestMapping("/test")
    public BaseResponse test(@RequestBody Map<String,Object> map) {
        return BaseResponse.success();
    }

    @PostMapping("/users/query")
    public BaseResponse queryUsers(@RequestBody(required = false) CmUserQueryRequest request) {
        return cmUserService.queryUsers(request);
    }

    @PostMapping("/users/export")
    public void exportUsers(@RequestBody(required = false) CmUserQueryRequest request,
                            HttpServletResponse response) {
        cmUserService.exportUsers(request, response);
    }

}
