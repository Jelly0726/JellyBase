package com.base.sms;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.base.appManager.BaseApplication;

import java.util.List;

/**
 * 监听短信数据库
 */
public class SmsService extends Service{
    //注册监听短信数据库的变化
    private SMSContentObserver smsContentObserver;
    protected static final int MSG_INBOX = 1;
    //监听到数据库变化后
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INBOX:
                    List<SMS> list= SmsUtil.getPhoneSms(BaseApplication.getInstance(),
                            Uri.parse("content://sms/"),5);
                    for (SMS map:list){
//                        try {
//                            mApiBll.sendQR(map);
//                            ActMain.sendmsg("收到新的短消息");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
                    }
                    break;
            }
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        smsContentObserver = new SMSContentObserver(this, mHandler);
        if (smsContentObserver != null) {
            getContentResolver().registerContentObserver(
                    Uri.parse("content://sms/"), true, smsContentObserver);// 注册监听短信数据库的变化
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (smsContentObserver != null) {
            getContentResolver().unregisterContentObserver(smsContentObserver);// 取消监听短信数据库的变化
        }
    }
}
