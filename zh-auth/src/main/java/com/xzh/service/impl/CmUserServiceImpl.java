package com.xzh.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.captcha.ArithmeticCaptcha;
import com.xzh.bean.CmUser;
import com.xzh.result.BaseResponse;
import com.xzh.service.CmUserService;
import com.xzh.mapper.CmUserMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
* @author kk
* @description 针对表【cm_user】的数据库操作Service实现
* @createDate 2026-03-05 23:08:06
*/
@Service
public class CmUserServiceImpl extends ServiceImpl<CmUserMapper, CmUser> implements CmUserService{

    public static final String CAPTCHA_PREFIX = "captcha:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public BaseResponse login(Map<String, Object> map) {
        String userName = (String) map.get("userName");
        String passWord = (String) map.get("passWord");
        String uuid = (String) map.get("uuid");
        String code = (String) map.get("code");
        if (StringUtils.isBlank(uuid) || StringUtils.isBlank(code)) {
            return BaseResponse.error("验证码或uuid不能为空");
        }
        String redisKey = CAPTCHA_PREFIX + uuid;
        // 从 Redis 拿出真正的答案
        String correctCode = redisTemplate.opsForValue().get(redisKey);
        if (correctCode == null) {
            return BaseResponse.error("验证码已过期，请刷新");
        }
        if (!correctCode.equals(code)) {
            return BaseResponse.error("验证码错误");
        }
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(passWord)) {
            return BaseResponse.error("用户名或密码为空");
        }
        CmUser user = this.getOne(Wrappers.lambdaQuery(CmUser.class)
                .eq(CmUser::getUserName, userName));
        if (user == null) {
            return BaseResponse.error("账号或密码错误");
        }
        boolean isMatch = BCrypt.checkpw(passWord, user.getPassWord()); // 这里的 getPassWord() 对应你实体类里的方法
        if (!isMatch) {
            return BaseResponse.error("账号或密码错误");
        }
        //返回登录信息给前端
        return null;
    }

    @Override
    public BaseResponse getCaptcha(Map<String, Object> map) {
        // 1. 创建算术类型的验证码 (宽130, 高48, 2位数运算)
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48);
        captcha.setLen(3);
        // 2. 获取运算结果 (注意：text() 拿到的是结果，比如图片是 1+2=?, text 就是 "3")
        String result = captcha.text();
        String uuid = UUID.randomUUID().toString();
        String redisKey = CAPTCHA_PREFIX + uuid;
        redisTemplate.opsForValue().set(redisKey, result, 1, TimeUnit.MINUTES);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("msg", "获取验证码成功");
        Map<String, String> data = new HashMap<>();
        data.put("uuid", uuid);
        data.put("imgBase64", captcha.toBase64()); // 直接转成 Base64 返回，避开底层 Servlet 依赖
        response.put("data", data);
        return BaseResponse.success(response);
    }
}




