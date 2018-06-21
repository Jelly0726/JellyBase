package com.base.jiguang;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.base.applicationUtil.AppPrefs;
import com.base.applicationUtil.MyApplication;
import com.base.config.ConfigKey;
import com.base.config.IntentAction;
import com.base.daemon.DaemonEnv;
import com.base.daemon.service.WatchDogService;

import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
/**
 * 极光接收推送消息Receiver
 */
public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private NotificationManager nm;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Log.d(TAG, "JPush用户注册成功");

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的自定义消息");
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的通知");

            receivingNotification(context,bundle);
            AppPrefs.putBoolean(MyApplication.getMyApp(), ConfigKey.NEWMESSAGE, true);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");

            //openNotification(context,bundle);
            //打开自定义的Activity
            Intent i = new Intent();
            i.setAction(IntentAction.JPUSH_CLICK);
            i.putExtras(bundle);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
            context.startActivity(i);

        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {//连接状态
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            if(!connected){
                Intent mIntent = new Intent();
                mIntent.setAction(IntentAction.NET_STATE);
                context.sendBroadcast(mIntent);//发送广播
            }
            //Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        }else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
        //启动守护服务，运行在:watch子进程中
        DaemonEnv.startServiceMayBind(WatchDogService.class);
    }
    private void receivingNotification(Context context, Bundle bundle){
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Log.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Log.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG, "extras : " + extras);
    }
    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            }else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    //Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it =  json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " +json.optString(myKey) + "]");
                    }
                } catch (Exception e) {
                    //Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //推送下来的自定义消息
    private void processCustomMessage(Context context, Bundle bundle) {
        try {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.i(TAG, "message=" + message + " extras=" + extras);
            //PushMessage.getInstance().pushMessage(extras);

        } catch (Exception e) {
            Log.i("JPush", "e="+ e);
        }
       /* if (MainActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (null != extraJson && extraJson.length() > 0) {
                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
                    }
                } catch (Exception e) {

                }

            }
            context.sendBroadcast(msgIntent);
        }*/
    }

    /***
     * 点击极光推送的通知,跳转到通知网址
     * @param context
     * @param bundle
     */
    private void openNotification(Context context, Bundle bundle){
        try {
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String url = bundle.getString(JPushInterface.EXTRA_EXTRA);
            // WebViewActivity.actionStart(context, new WebTools(title, url));
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }
}
