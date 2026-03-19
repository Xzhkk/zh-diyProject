package com.xzh.config;

import com.xzh.model.UserInfoModel;

public class UserContextHolder {
    private static final ThreadLocal<UserInfoModel> USER_INFO_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUserInfo(UserInfoModel userInfo) {
        USER_INFO_THREAD_LOCAL.set(userInfo);
    }

    public static UserInfoModel getUserInfo() {
        return USER_INFO_THREAD_LOCAL.get();
    }

    public static void clear() {
        USER_INFO_THREAD_LOCAL.remove();
    }
}
