package com.base;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import com.base.appManager.AppSubject;
import com.base.appManager.BaseApplication;
import com.base.applicationUtil.AppPrefs;
import com.base.config.ConfigKey;
import com.base.daemon.DaemonEnv;
import com.base.dialog.PrivacyDialog;
import com.base.view.BaseActivity;
import com.jelly.jellybase.BuildConfig;
import com.jelly.jellybase.server.TraceServiceImpl;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import hugo.weaving.DebugLog;

@DebugLog
public class LauncherActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断新生成启动活动是否在栈底,否:弹出,恢复上一次活动
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        //开启服务//true  表示停止服务，false  表示启动服务
        TraceServiceImpl.sShouldStopService = true;
        DaemonEnv.startServiceMayBind(TraceServiceImpl.class);

        if (AppPrefs.getBoolean(BaseApplication.getInstance(), ConfigKey.FIRST, true)) {
//			if(!hasShortcut()){
//				createShut();// 创建快捷方式
//			}else{
//				delShortcut();
//			}
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //android 6.0及以上需要动态请求权限
            if (AppPrefs.getBoolean(BaseApplication.getInstance(), ConfigKey.FIRST, true)) {//第一次运行
                PrivacyDialog privacyDialog = PrivacyDialog.getInstance();
                privacyDialog.setOnClickListener(new PrivacyDialog.OnClickListener() {
                    @Override
                    public void onAgree() {
                        // 申请权限。
                        requestPermission();
                        AppPrefs.putBoolean(BaseApplication.getInstance(), ConfigKey.FIRST, false);
                    }

                    @Override
                    public void onRefuse() {
                        AppSubject.getInstance().exit();
                    }
                });
                privacyDialog.show(getSupportFragmentManager(), "PrivacyDialog");
            } else {
                // 申请权限。
                requestPermission();
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (BaseApplication.getInstance().isLogin()) {
                        //BaseApplication.getInstance().goLoginActivity();
                        BaseApplication.getInstance().goMainActivity();//进入主界面
                        //BaseApplication.getInstance().goGuideActivity();//进入引导页界面
                        finish();
                    } else {
                        if (BuildConfig.IS_MUST_LOGIN) {//是否必须登录
                            BaseApplication.getInstance().goLoginActivity();
                        } else {
                            BaseApplication.getInstance().goMainActivity();//进入主界面
                        }
                        //BaseApplication.getInstance().goGuideActivity();//进入引导页界面
                        finish();
                    }
                }
            }, 1000);
        }
    }
    @Override
    public int getLayoutId(){
        //下面为纯色背景启动图标  若使用背景图片(styles)注释下面即可
//        return  R.layout.base_activity_launcher;
        return -1;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void requestPermission() {
        PermissionX.init(LauncherActivity.this)
                .permissions(
                        Manifest.permission.CAMERA
                        ,Manifest.permission.READ_EXTERNAL_STORAGE
                        ,Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ,Manifest.permission.READ_CONTACTS
                        ,Manifest.permission.WRITE_CONTACTS
                        ,Manifest.permission.READ_CALENDAR
                        ,Manifest.permission.WRITE_CALENDAR
                        ,Manifest.permission.ACCESS_COARSE_LOCATION
                        ,Manifest.permission.ACCESS_FINE_LOCATION
                        ,Manifest.permission.SEND_SMS
                        ,Manifest.permission.READ_SMS
                        ,Manifest.permission.CALL_PHONE
                        ,Manifest.permission.READ_PHONE_STATE
                        ,Manifest.permission.ACCESS_NOTIFICATION_POLICY
                        ,Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
                )
//                .explainReasonBeforeRequest()
//                .onExplainRequestReason(new ExplainReasonCallbackWithBeforeParam() {
//                    @Override
//                    public void onExplainReason(ExplainScope scope, List<String> deniedList, boolean beforeRequest) {
////                                CustomDialog customDialog = new CustomDialog(MainJavaActivity.this, "PermissionX needs following permissions to continue", deniedList);
////                                scope.showRequestReasonDialog(customDialog);
//                        scope.showRequestReasonDialog(deniedList, "此功能需要以下权限权限才可运行", "知道了");
//                    }
//                })
//                .onForwardToSettings(new ForwardToSettingsCallback() {
//                    @Override
//                    public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
//                        scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "知道了");
//                    }
//                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                        if (allGranted) {//允许
                        } else {//拒绝
                        }
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                if (BaseApplication.getInstance().isLogin()) {
                                    //BaseApplication.getInstance().goLoginActivity();
                                    BaseApplication.getInstance().goMainActivity();//进入主界面
                                    //BaseApplication.getInstance().goGuideActivity();//进入引导页界面
                                    finish();
                                } else {
                                    if (BuildConfig.IS_MUST_LOGIN) {//是否必须登录
                                        BaseApplication.getInstance().goLoginActivity();
                                    } else {
                                        BaseApplication.getInstance().goMainActivity();//进入主界面
                                    }
                                    //BaseApplication.getInstance().goGuideActivity();//进入引导页界面
                                    finish();
                                }
                            }
                        }, 1000);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0&&resultCode==0){
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (BaseApplication.getInstance().isLogin()) {
                        //BaseApplication.getInstance().goLoginActivity();
                        BaseApplication.getInstance().goMainActivity();//进入主界面
                        //BaseApplication.getInstance().goGuideActivity();//进入引导页界面
                        finish();
                    } else {
                        if (BuildConfig.IS_MUST_LOGIN) {//是否必须登录
                            BaseApplication.getInstance().goLoginActivity();
                        } else {
                            BaseApplication.getInstance().goMainActivity();//进入主界面
                        }
                        //BaseApplication.getInstance().goGuideActivity();//进入引导页界面
                        finish();
                    }
                }
            }, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
