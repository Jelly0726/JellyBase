package com.base.appservicelive.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.base.appservicelive.toolsUtil.CommonStaticUtil;


/**
 * 广播接收者：解锁 ,那么使用这个广播接收者来循环调用开启服务

 * @author wangl(Mail:WangleiDree@gmail.com)
 */
public class PresentReceiver extends BroadcastReceiver {
    private static final String TAG = "msg";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
       // String action=intent.getAction();
//        if(action.equals(WifiManager.RSSI_CHANGED_ACTION)){
//            LogUtil.i("msg PresentReceiver","EXTRA_NEW_RSSI="
//                    +intent.getExtras().get(WifiManager.EXTRA_NEW_RSSI));
//        }
//        LogUtil.i(TAG, "--PresentReceiver in  解锁------------>>>>>>>>\n" +
//                " action="+action);

        CommonStaticUtil.startService(context);
    }
}