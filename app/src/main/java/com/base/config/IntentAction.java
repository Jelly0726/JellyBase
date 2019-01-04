package com.base.config;

import com.base.appManager.BaseApplication;

/**
 * Created by Administrator on 2017/9/7.
 */

public class IntentAction {
    public static final String NET_STATE=BaseApplication.getInstance().getPackageName()+".network.anomaly";//网络异常
    public static final String JPUSH_CLICK=BaseApplication.getInstance().getPackageName()+".JPUSH_CLICK";//极光推送点击通知打开
    public static final String NOTICE=BaseApplication.getInstance().getPackageName()+".NOTICE";//
    public static final String ACTION_MAIN= BaseApplication.getInstance().getPackageName()+".action.ACTION_MAIN";//跳转主页
    public static final String ACTION_LOGIN = BaseApplication.getInstance().getPackageName()+".action.ACTION_LOGIN";//跳转登录
    public static final String TOKEN_NOT_EXIST = BaseApplication.getInstance().getPackageName()+".action.TOKEN_NOT_EXIST";//token不存在

}
