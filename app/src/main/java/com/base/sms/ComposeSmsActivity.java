package com.base.sms;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ComposeSmsActivity extends Activity {
    //注册监听短信数据库的变化
    private SMSContentObserver smsContentObserver;
    protected static final int MSG_INBOX = 1;
    //监听到数据库变化后
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INBOX:

                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        smsContentObserver = new SMSContentObserver(ComposeSmsActivity.this, mHandler);
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (smsContentObserver != null) {
            getContentResolver().registerContentObserver(
                    Uri.parse("content://sms/"), true, smsContentObserver);// 注册监听短信数据库的变化
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (smsContentObserver != null) {
            getContentResolver().unregisterContentObserver(smsContentObserver);// 取消监听短信数据库的变化
        }

    }
}
