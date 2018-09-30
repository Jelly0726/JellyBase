package com.base.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.base.appManager.AppSubject;
import com.base.appManager.BaseApplication;
import com.base.appManager.Observable;
import com.base.appManager.Observer;
import com.base.applicationUtil.AppPrefs;
import com.base.circledialog.CircleDialog;
import com.base.circledialog.callback.ConfigDialog;
import com.base.circledialog.callback.ConfigText;
import com.base.circledialog.params.DialogParams;
import com.base.circledialog.params.TextParams;
import com.base.config.ConfigKey;
import com.base.config.IntentAction;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.jelly.jellybase.R;
import com.yanzhenjie.sofia.Sofia;
import com.zhy.autolayout.AutoLayoutActivity;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/12/5.
 */

public class BaseActivity extends AutoLayoutActivity implements Observer {
    private InnerRecevier mRecevier;
    private IntentFilter mFilter;
    private boolean isResume=false;
    private Toolbar mToolbar;
    static {
        //使你的app使用矢量图support library；
        //AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtra();
        mRecevier = new InnerRecevier();
        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mFilter.addAction(IntentAction.TOKEN_NOT_EXIST);
        AppSubject.getInstance().attach(this);
        /**
         * 开始监听，注册广播
         */
        if (mRecevier != null) {
            registerReceiver(mRecevier, mFilter);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);//must store the new intent unless getIntent() will return the old one
        getExtra();
    }

    /**
     * 获取Intent传值
     */
    public void getExtra(){

    }
    @Override
    public void setContentView(View view) {
        FrameLayout frameLayout=new FrameLayout(this);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(view);
        View topBar = getLayoutInflater().inflate(R.layout.toolbar_dark, null);
        frameLayout.addView(topBar);
        super.setContentView(frameLayout);
        iniBar();
    }
    @Override
    public void setContentView(int view) {
        View views = getLayoutInflater().inflate(view, null);
        this.setContentView(views);
    }
    private void iniBar(){
        //// ↓↓↓↓↓内容入侵状态栏。↓↓↓↓↓
        mToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setVisibility(View.GONE);
        Sofia.with(this)
                // 状态栏深色字体。
                .statusBarDarkFont()
                // 状态栏浅色字体。
                //.statusBarLightFont()
                // 导航栏背景透明度。
                //.navigationBarBackgroundAlpha(int alpha)
                // 状态栏背景。可接受Color、Drawable
                .statusBarBackground(ContextCompat.getColor(this, R.color.navi_color))
                // 导航栏背景。可接受Color、Drawable
                //.navigationBarBackground(ContextCompat.getDrawable(getActivity(), R.color.colorNavigation))
                // 内容入侵状态栏。
                .invasionStatusBar()
                // 内容入侵导航栏。
                //.invasionNavigationBar()
                // 让某一个View考虑状态栏的高度，显示在适当的位置，可接受viewID、view
                .fitsSystemWindowView(mToolbar);
        setAnyBarAlpha(0);
        //// ↑↑↑↑↑内容入侵状态栏。↑↑↑↑↑
    }
    /**
     * 设置状态栏透明度
     * @param alpha
     */
    private void setAnyBarAlpha(int alpha) {
        mToolbar.getBackground().mutate().setAlpha(alpha);
        Sofia.with(this)
                .statusBarBackgroundAlpha(alpha);
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
        /**
         * 停止监听，注销广播
         */
        if (mRecevier != null) {
            unregisterReceiver(mRecevier);
        }
        AppSubject.getInstance().detach(this);
        super.onDestroy();
        if (circleDialog!=null){
            circleDialog.onDismiss();
            circleDialog=null;
        }
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
                        AppPrefs.putBoolean(BaseApplication.getInstance(), ConfigKey.ISHOME,true);
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
                        synchronized (BaseApplication.getInstance()) {
                            if (circleDialog == null) {
                                circleDialog=new CircleDialog.Builder(BaseActivity.this)
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
                                                AppSubject.getInstance().detachAll();
                                                Intent intent1 = new Intent();
                                                //intent.setClass(this, LoginActivity.class);
                                                intent1.setAction(IntentAction.ACTION_LOGIN);
                                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                BaseApplication.getInstance().startActivity(intent1);
                                            }
                                        });
                                circleDialog.show();
                            }
                        }
                    }
                    break;
            }

        }
    };
    @Override
    public void finish() {
        // TODO Auto-generated method stub
        AppSubject.getInstance().detach(this);
        super.finish();
    }
    @Override
    public void onUpdate(Observable observable, Object data) {
        // TODO Auto-generated method stub
        this.finish();
    }
}
