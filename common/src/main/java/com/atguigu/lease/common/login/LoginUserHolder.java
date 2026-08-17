package com.atguigu.lease.common.login;

public class LoginUserHolder {
    //LoginUser即要保存的数据类型
    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    //将当前登录用户的信息保存到线程的本地变量中
    public static void setLoginUser(LoginUser loginUser) {
        threadLocal.set(loginUser);
    }

    //从当前的线程本地变量中获取登录用户的信息
    public static LoginUser getLoginUser() {
        return threadLocal.get();
    }

    //清除线程本地变量的数据
    public static void clear() {
        threadLocal.remove();
    }
}
