package com.base.appservicelive.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.base.appservicelive.toolsUtil.CommonStaticUtil;


/**
 * 广播接收者：启动 ,那么使用这个广播接收者来循环调用开启服务

 * @author wangl(Mail:WangleiDree@gmail.com)
 */
public class StarReceiver extends BroadcastReceiver {
    private static final String TAG = "msg";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
//        String action=intent.getAction();
//        if(action.equals(WifiManager.RSSI_CHANGED_ACTION)){
//            Log.i("msg StarReceiver","EXTRA_NEW_RSSI="
//                    +intent.getExtras().get(WifiManager.EXTRA_NEW_RSSI));
//        }
//        Log.i(TAG, "--StarReceiver in  启动------------>>>>>>>>\n" +
//                " action="+action);

        CommonStaticUtil.startService(context);
    }
}