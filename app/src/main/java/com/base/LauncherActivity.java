package com.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import com.base.appManager.AppSubject;
import com.base.appManager.BaseApplication;
import com.base.applicationUtil.AppPrefs;
import com.base.circledialog.PrivacyDialog;
import com.base.config.ConfigKey;
import com.base.daemon.DaemonEnv;
import com.base.permission.CallBack;
import com.base.permission.PermissionUtils;
import com.base.view.BaseActivity;
import com.jelly.jellybase.BuildConfig;
import com.jelly.jellybase.server.TraceServiceImpl;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

import hugo.weaving.DebugLog;

@DebugLog
public class LauncherActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//判断新生成启动活动是否在栈底,否:弹出,恢复上一次活动
		if (!isTaskRoot()){
			Intent intent = getIntent();
			String action = intent.getAction();
			if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)){
				finish();
				return;
			}
		}
		//下面为纯色背景启动图标  若使用背景图片(styles)注释下面即可
//		setContentView(R.layout.base_activity_launcher);
		//开启服务//true  表示停止服务，false  表示启动服务
		TraceServiceImpl.sShouldStopService = true;
		DaemonEnv.startServiceMayBind(TraceServiceImpl.class);

		if (AppPrefs.getBoolean(BaseApplication.getInstance(),ConfigKey.FIRST,true)) {
//			if(!hasShortcut()){
//				createShut();// 创建快捷方式
//			}else{
//				delShortcut();
//			}
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			//android 6.0及以上需要动态请求权限
			if (AppPrefs.getBoolean(BaseApplication.getInstance(), ConfigKey.FIRST,true)) {//第一次运行
				PrivacyDialog privacyDialog = PrivacyDialog.getInstance();
				privacyDialog.setOnClickListener(new PrivacyDialog.OnClickListener() {
					@Override
					public void onAgree() {
						// 申请权限。
						PermissionUtils.getInstance().requestPermission(LauncherActivity.this, new CallBack() {
									@Override
									public void onSucess() {
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

									@Override
									public void onFailure(List<String> permissions) {

									}
								},
								Permission.Group.MICROPHONE,//扩音器，麦克风
								Permission.Group.STORAGE,//存储
								Permission.Group.CALENDAR,//日历
								Permission.Group.CAMERA,//照相机
//					Permission.Group.CONTACTS,//联系人
								Permission.Group.LOCATION,//定位
								Permission.Group.SMS,//短信
								new String[]{
										Permission.READ_PHONE_STATE,//读取手机状态
										Permission.CALL_PHONE,//拨打电话
										android.Manifest.permission.SYSTEM_ALERT_WINDOW//<!-- 显示系统窗口权限 -->
								});
					}

					@Override
					public void onRefuse() {
						AppSubject.getInstance().exit();
					}
				});
				privacyDialog.show(getSupportFragmentManager(), "PrivacyDialog");
			}else {
				// 申请权限。
				PermissionUtils.getInstance().requestPermission(LauncherActivity.this, new CallBack() {
							@Override
							public void onSucess() {
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

							@Override
							public void onFailure(List<String> permissions) {

							}
						},
						Permission.Group.MICROPHONE,//扩音器，麦克风
						Permission.Group.STORAGE,//存储
						Permission.Group.CALENDAR,//日历
						Permission.Group.CAMERA,//照相机
//					Permission.Group.CONTACTS,//联系人
						Permission.Group.LOCATION,//定位
						Permission.Group.SMS,//短信
						new String[]{
								Permission.READ_PHONE_STATE,//读取手机状态
								Permission.CALL_PHONE,//拨打电话
								android.Manifest.permission.SYSTEM_ALERT_WINDOW//<!-- 显示系统窗口权限 -->
						});
			}
		}else {
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
