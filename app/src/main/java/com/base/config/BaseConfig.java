package com.base.config;

/**
 * Created by Administrator on 2017/9/7.
 */

public class BaseConfig {
    //微信appId
    public static final String WechatPay_APP_ID = "wxf52070028451d0be";
    //微信AppSecret
    public static final String WechatPay_APP_SECRET = "4090f2fa39d5fe169060bafa7405e615";
    //QQAppID
    public static final String QQ_APP_ID="";
    //QQAppSecret
    public static final String QQ_APP_SECRET="";
    //新浪微博AppID
    public static final String WB_APP_ID="";
    //新浪微博AppSecret
    public static final String WB_APP_SECRET="";
    //新浪微博RedirectUrl
    public static final String WB_REDIRECT_URL="";
    /**
     * 应用宝渠道号（SELF_UPDATE_CHANNEL）用于应用宝侧统计接入方集成的省流量更新SDK为应用宝带来的新增量。
     * 如果需要特殊区分你们带来的新增，则需要联系省流量更新SDK产品分配一个，如果不用区分，则使用以上默认值（1003143）即可。
     */
    public static final String SELF_UPDATE_CHANNEL="1003143";
    public static final int SERVICE_ID=01;//前台服务ID
    public static final String CHANNEL_ID="0101";// NotificationChannel 的Id.
    public static String SERVICE_IP="driver.4000000144.com";
    public static final String KEY="appdc9622f8c3f62f68";
    //  向钉钉发送崩溃信息
    public static final String sendError_URL="https://oapi.dingtalk.com/robot/send?access_token=853eda618138e5237cd1b9ea758cdfe4c3db1153d384bb8ca7f6e90e0276ffd3";
}
