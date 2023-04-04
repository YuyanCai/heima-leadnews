package com.heima.utils.thread;

import com.heima.model.admin.pojos.AdUser;

public class AdminThreadLocalUtil {

    private final static ThreadLocal<AdUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    //存入线程中
    public static void setUser(AdUser adUser){
        WM_USER_THREAD_LOCAL.set(adUser);
    }

    //从线程中获取
    public static AdUser getUser(){
        return WM_USER_THREAD_LOCAL.get();
    }

    //清理
    public static void clear(){
        WM_USER_THREAD_LOCAL.remove();
    }

}
