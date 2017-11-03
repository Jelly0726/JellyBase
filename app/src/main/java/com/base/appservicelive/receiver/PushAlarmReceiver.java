package com.base.appservicelive.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.base.appservicelive.toolsUtil.CommonStaticUtil;


/**
 * 广播接受者 定时判断开启服务

 * @author wangl(Mail:WangleiDree@gmail.com)
 */
public class PushAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "msg";
    private Context context;
    private static int count=0;//1分钟检测下后台服务是否开启

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //Log.i(TAG, "-----------------1 minutes to in------------>>>>>>>>");
        CommonStaticUtil.startService(context);
        //Log.i(TAG, "-----------------1 minutes to out------------>>>>>>>>");
    }
}