package com.jelly.config;


import com.jelly.AppInit;

/**
 * Created by Administrator on 2017/9/7.
 */

public class IntentAction {
    public static final String NET_STATE= AppInit.INSTANCE.getPackageName()+".network.anomaly";//网络异常
    public static final String JPUSH_CLICK= AppInit.INSTANCE.getPackageName()+".JPUSH_CLICK";//极光推送点击通知打开
    public static final String NOTICE= AppInit.INSTANCE.getPackageName()+".NOTICE";//
    public static final String ACTION_MAIN= AppInit.INSTANCE.getPackageName()+".action.ACTION_MAIN";//跳转主页
    public static final String ACTION_LOGIN = AppInit.INSTANCE.getPackageName()+".action.ACTION_LOGIN";//跳转登录
    public static final String TOKEN_NOT_EXIST = AppInit.INSTANCE.getPackageName()+".action.TOKEN_NOT_EXIST";//token不存在

}
