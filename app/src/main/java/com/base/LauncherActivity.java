package com.base;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Menu;

import com.base.appManager.BaseApplication;
import com.base.applicationUtil.AppPrefs;
import com.base.config.ConfigKey;
import com.base.daemon.DaemonEnv;
import com.base.permission.CallBack;
import com.base.permission.PermissionUtils;
import com.base.view.BaseActivity;
import com.jelly.jellybase.BuildConfig;
import com.jelly.jellybase.R;
import com.jelly.jellybase.server.TraceServiceImpl;
import com.yanzhenjie.permission.Permission;

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
		//开启服务
		TraceServiceImpl.sShouldStopService = true;
		DaemonEnv.startServiceMayBind(TraceServiceImpl.class);

		if (AppPrefs.getBoolean(BaseApplication.getInstance(),ConfigKey.FIRST,true)) {
//			if(!hasShortcut()){
//				createShut();// 创建快捷方式
//			}else{
//				delShortcut();
//			}
		}
		// 申请权限。
		PermissionUtils.getInstance().requestPermission(LauncherActivity.this,new CallBack(){
					@Override
					public void onSucess() {
						new Handler().postDelayed(new Runnable() {
							public void run() {
								if (BaseApplication.getInstance().isLogin()){
									//BaseApplication.getInstance().goLoginActivity();
									BaseApplication.getInstance().goMainActivity();//进入主界面
									//BaseApplication.getInstance().goGuideActivity();//进入引导页界面
									finish();
								}else{
									if(BuildConfig.IS_MUST_LOGIN) {//是否必须登录
										BaseApplication.getInstance().goLoginActivity();
									}else {
										BaseApplication.getInstance().goMainActivity();//进入主界面
									}
									//BaseApplication.getInstance().goGuideActivity();//进入引导页界面
									finish();
								}
							}
						},1000);
					}
				},
				Permission.Group.MICROPHONE,//扩音器，麦克风
				Permission.Group.STORAGE,//存储
				Permission.Group.CALENDAR,//日历
				Permission.Group.CAMERA,//照相机
				Permission.Group.CONTACTS,//联系人
				Permission.Group.LOCATION,//定位
				Permission.Group.SMS,//短信
				new String[]{
						android.Manifest.permission.READ_PHONE_STATE,//读取手机状态
						android.Manifest.permission.CALL_PHONE//拨打电话
				});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	//检测是否存在快捷方式
	private boolean hasShortcut()
	{
		boolean isInstallShortcut = false;
		try{
			final ContentResolver cr = getContentResolver();
			final Uri CONTENT_URI = Uri.parse("content://com.android.launcher2.settings/favorites?notify=true");//保持默认
			Cursor c = cr.query(CONTENT_URI,new String[] {"title","iconResource" },"title=?", //保持默认
					//getString(R.string.app_name)是获取string配置文件中的程序名字，这里用一个String的字符串也可以
					new String[] {getString(R.string.app_name).trim()}, null);
			if(c!=null && c.getCount()>0){
				isInstallShortcut = true ;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return isInstallShortcut ;
	}
	/**
	 * 创建桌面快捷方式
	 */
	public void createShut() {
		try{
			// 创建添加快捷方式的Intent
			Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			String title = getResources().getString(R.string.app_name);
			// 加载快捷方式的图标
			Parcelable icon = Intent.ShortcutIconResource.fromContext(
					LauncherActivity.this, R.mipmap.ic_launcher);
			// 创建点击快捷方式后操作Intent,该处当点击创建的快捷方式后，再次启动该程序
			Intent myIntent = new Intent(LauncherActivity.this,
					LauncherActivity.class);
			// 设置快捷方式的标题
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
			// 设置快捷方式的图标
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
			// 设置快捷方式对应的Intent
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);
			addIntent.putExtra("duplicate", false); //不允许重复创建
			// 发送广播添加快捷方式
			sendBroadcast(addIntent);
			AppPrefs.putBoolean(BaseApplication.getInstance(),ConfigKey.FIRST,false);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//删除快捷方式
	private void delShortcut(){
		try{
			Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
			//快捷方式的名称
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));

			//这里的intent要和创建时的intent设置一致
			Intent intent = new Intent(this,LauncherActivity.class);
			intent.setAction("com.hooypay.Activity.logo");
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
			sendBroadcast(shortcut);
		}catch(Exception e){
			e.printStackTrace();
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
