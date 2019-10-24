package com.base.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.base.SystemBar.StatusBarUtil;
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
import com.base.log.DebugLog;
import com.base.toast.ToastUtils;
import com.jelly.jellybase.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.jpush.android.api.JPushInterface;

import static com.base.permission.PermissionUtils.REQUEST_CODE_SETTING;

/**
 * Created by Administrator on 2017/12/5.
 */
@hugo.weaving.DebugLog
public class BaseActivity extends AppCompatActivity implements Observer {
    private InnerRecevier mRecevier;
    private IntentFilter mFilter;
    private boolean isResume=false;
//    public DisplayManager mDisplayManager;//双屏客显
//    public Presentation mPresentation;//双屏客显
    static {
        //使你的app使用矢量图support library；
        //AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            boolean result = fixOrientation();
            DebugLog.i("onCreate fixOrientation when Oreo, result = " + result);
        }
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            boolean result = fixOrientation();
            DebugLog.i("onCreate fixOrientation when Oreo, result = " + result);
        }
        //在BaseActivity里禁用软键盘
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //在需要打开的Activity取消禁用软键盘
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        super.onCreate(savedInstanceState);
        //====解决java.net.SocketException：sendto failed：ECONNRESET（由对等方重置连接）
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //====
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
        //这里注意下 因为在评论区发现有网友调用setRootViewFitsSystemWindows 里面 winContent.getChildCount()=0 导致代码无法继续
        //是因为你需要在setContentView之后才可以调用 setRootViewFitsSystemWindows
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }
        //// ↑↑↑↑↑内容入侵状态栏。↑↑↑↑↑
    }
    private boolean isTranslucentOrFloating() {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }
    private boolean fixOrientation() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            DebugLog.i("avoid calling setRequestedOrientation when Oreo.");
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case REQUEST_CODE_SETTING: {
                ToastUtils.showShort(this, R.string.message_setting_comeback);
                break;
            }
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
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //设置接收到回车事件时隐藏软键盘
        if(event!=null&&event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            View focus = getCurrentFocus();
            manager.hideSoftInputFromWindow(
                    focus == null ? null : focus.getWindowToken(),
//                            editText == null ? null : editText.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return super.dispatchKeyEvent(event);
    }
}
