package com.base.config;

import com.base.applicationUtil.MyApplication;

/**
 * Created by Administrator on 2017/9/7.
 */

public class IntentAction {
    public static final String NET_STATE="IntentAction.network.anomaly";//网络异常
    public static final String JPUSH_CLICK="IntentAction.JPUSH_CLICK";//极光推送点击通知打开
    public static final String NOTICE="IntentAction.NOTICE";//
    public static final String ACTION_MAIN= MyApplication.getMyApp().getPackageName()+".action.ACTION_MAIN";//跳转主页
    public static final String ACTION_LOGIN =MyApplication.getMyApp().getPackageName()+".action.ACTION_LOGIN";//跳转登录

}
