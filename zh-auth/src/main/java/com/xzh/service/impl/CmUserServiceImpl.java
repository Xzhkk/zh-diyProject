package com.xzh.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.captcha.ArithmeticCaptcha;
import com.xzh.base.BaseServiceImpl;
import com.xzh.bean.CmUser;
import com.xzh.result.BaseResponse;
import com.xzh.service.CmUserService;
import com.xzh.mapper.CmUserMapper;
import jakarta.annotation.Resource;
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
public class CmUserServiceImpl extends BaseServiceImpl<CmUserMapper, CmUser> implements CmUserService{

    public static final String CAPTCHA_PREFIX = "captcha:";
    public static final String LOGIN_TOKEN_PREFIX = "login:token:";


    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
        String correctCode = stringRedisTemplate.opsForValue().get(redisKey);
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
        String token = IdUtil.fastSimpleUUID();
        String tokenRedisKey = LOGIN_TOKEN_PREFIX + token;
        //返回登录信息给前端
        user.setPassWord(null);
        stringRedisTemplate.opsForValue().set(tokenRedisKey, JSONUtil.toJsonStr(user), 2, TimeUnit.HOURS);
        Map<String,Object> res = new HashMap<>();
        res.put("token",token);
        res.put("nickName",user.getNickName());
        res.put("nickUrl",user.getNickUrl());
        return BaseResponse.success(res);
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
        stringRedisTemplate.opsForValue().set(redisKey, result, 1, TimeUnit.MINUTES);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("msg", "获取验证码成功");
        Map<String, String> data = new HashMap<>();
        data.put("uuid", uuid);
        data.put("imgBase64", captcha.toBase64()); // 直接转成 Base64 返回，避开底层 Servlet 依赖
        response.put("data", data);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse register(Map<String, Object> map) {
        String userName = (String) map.get("userName");
        String passWord = (String) map.get("userName");
        String code = (String) map.get("code");
        CmUser user = this.findOneByField(CmUser::getUserName, userName);
        if (user != null) {
            return BaseResponse.error("当前用户已注册");
        }
        String hashPassword = BCrypt.hashpw(passWord, BCrypt.gensalt());
        CmUser newUser = new CmUser();
        newUser.setUserName(userName);
        newUser.setPassWord(hashPassword); // 存入数据库的是类似于 $2a$10$wK... 的安全密文
        // 给个默认的随机昵称，比如 "用户_a1b2c3"
        newUser.setNickName("用户_" + IdUtil.fastSimpleUUID().substring(0, 6));
        this.save(newUser);
        return BaseResponse.success();
    }
}




