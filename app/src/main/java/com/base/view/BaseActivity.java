package com.base.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;

import com.base.applicationUtil.AppPrefs;
import com.base.applicationUtil.MyApplication;
import com.base.appservicelive.service.LiveService;
import com.base.circledialog.CircleDialog;
import com.base.circledialog.callback.ConfigDialog;
import com.base.circledialog.callback.ConfigText;
import com.base.circledialog.params.DialogParams;
import com.base.circledialog.params.TextParams;
import com.base.config.ConfigKey;
import com.base.config.IntentAction;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.zhy.autolayout.AutoLayoutActivity;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/12/5.
 */

public class BaseActivity extends AutoLayoutActivity {
    private InnerRecevier mRecevier;
    private IntentFilter mFilter;
    private boolean isResume=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecevier = new InnerRecevier();
        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mFilter.addAction(IntentAction.TOKEN_NOT_EXIST);
        MyApplication.getMyApp().addActivity(this);
        /**
         * 开始监听，注册广播
         */
        if (mRecevier != null) {
            registerReceiver(mRecevier, mFilter);
        }
    }
    /**
     * 延迟关闭
     * @param time 延迟时间
     */
    public void finish(int time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },time);
    }
    @Override
    protected void onDestroy() {
        if (circleDialog!=null){
            circleDialog.onDismiss();
            circleDialog=null;
        }
        /**
         * 停止监听，注销广播
         */
        if (mRecevier != null) {
            unregisterReceiver(mRecevier);
        }
        MyApplication.getMyApp().deleteActivity(this);
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        isResume=true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        isResume=false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 广播接收者
     */
    class InnerRecevier extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    //Log.i("msg", "action:" + action + ",reason:" + reason);
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        // 短按home键
                        AppPrefs.putBoolean(MyApplication.getMyApp(), ConfigKey.ISHOME,true);
                        Intent intent1=new Intent(context, LiveService.class);
                        startService(intent1);
                    } else if (reason
                            .equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        // 长按home键
                    }
                }
            }else if (action.equals(IntentAction.TOKEN_NOT_EXIST)) {//token不存在
                if (isResume) {
                    Message message=Message.obtain();
                    message.arg1=0;
                    handler.sendMessage(message);
                }
            }
        }
    }
    private CircleDialog.Builder circleDialog;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1){
                case 0:
                    if (circleDialog == null) {
                        synchronized (MyApplication.getMyApp()) {
                            if (circleDialog == null) {
                                new CircleDialog.Builder(BaseActivity.this)
                                        .configDialog(new ConfigDialog() {
                                            @Override
                                            public void onConfig(DialogParams params) {
                                                params.width = 0.6f;
                                            }
                                        })
                                        .setCanceledOnTouchOutside(false)
                                        .setCancelable(false)
                                        .setTitle("登录过期！")
                                        .configText(new ConfigText() {
                                            @Override
                                            public void onConfig(TextParams params) {
                                                params.gravity = Gravity.LEFT;
                                                params.textColor = Color.parseColor("#FF1F50F1");
                                                params.padding = new int[]{20, 0, 20, 0};
                                            }
                                        })
                                        .setText("登录过期或异地登录，请重新登录!")
                                        .setPositive("确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                circleDialog = null;
                                                GlobalToken.removeToken();
                                                MyApplication.getMyApp().finishAllActivity();
                                                Intent intent1 = new Intent();
                                                //intent.setClass(this, LoginActivity.class);
                                                intent1.setAction(IntentAction.ACTION_LOGIN);
                                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                MyApplication.getMyApp().startActivity(intent1);
                                            }
                                        })
                                        .show();
                            }
                        }
                    }
                    break;
            }

        }
    };
}
