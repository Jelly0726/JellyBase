package com.base;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Menu;
import android.view.Window;

import com.base.applicationUtil.AppPrefs;
import com.base.applicationUtil.MyApplication;
import com.base.bgabanner.GuideActivity;
import com.base.config.ConfigKey;
import com.base.config.IntentAction;
import com.base.sqldao.DBHelper;
import com.base.view.MyActivity;
import com.jelly.jellybase.R;
import com.jelly.jellybase.server.WokeService;

import org.greenrobot.eventbus.Subscribe;

import systemdb.Login;
import xiaofei.library.hermeseventbus.HermesEventBus;

public class LauncherActivity extends MyActivity {
	private Login login;
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
		//去掉  标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.base_activity_launcher);
		HermesEventBus.getDefault().register(LauncherActivity.this);
		//开启服务
		Intent stateGuardService =  new Intent(MyApplication.getMyApp(), WokeService.class);
		startService(stateGuardService);

		if (AppPrefs.getBoolean(MyApplication.getMyApp(),ConfigKey.FIRST,true)) {
			if(!hasShortcut()){
				createShut();// 创建快捷方式
			}else{
				delShortcut();
			}
		}
		login= DBHelper.getInstance(getApplicationContext())
				.getLogin();
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if (login!=null) {
					//Log.i("WF","getDriverId="+login.getDriverId());
					//Toast.makeText(WelcomeActivity.this,"getDriverId="+login.getDriverid(),Toast.LENGTH_LONG).show();
					if(login.getId()!=0){
						goMainActivity();//进入主界面
						//goGuideActivity();//进入引导页界面
					}else{
						//goLoginActivity();
						//goGuideActivity();//进入引导页界面
						goMainActivity();//进入主界面
					}
				}else{
					//goLoginActivity();
					//goGuideActivity();//进入引导页界面
					goMainActivity();//进入主界面
				}
			}
		},1000);
	}
	/**
	 * 进入登陆界面
	 */
	public void goLoginActivity() {
		try{
			Intent intent = new Intent();
			//intent.setClass(this, LoginActivity.class);
			intent.setAction(IntentAction.ACTION_LOGIN);
			startActivity(intent);
			finish();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 进入主界面
	 */
	public void goMainActivity() {
		try{
			Intent intent = new Intent();
			//intent.setClass(this, MainActivity.class);
			intent.setAction(IntentAction.ACTION_MAIN);
			startActivity(intent);
			finish();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 进入引导页界面
	 */
	public void goGuideActivity() {
		try{
			Intent intent = new Intent();
			intent.setClass(this, GuideActivity.class);
			startActivity(intent);
			finish();
		}catch(Exception e){
			e.printStackTrace();
		}
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
			AppPrefs.putBoolean(MyApplication.getMyApp(),ConfigKey.FIRST,false);
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
		HermesEventBus.getDefault().unregister(LauncherActivity.this);
		super.onDestroy();
		//SMSSDK.unregisterAllEventHandler();
	}
	/***
	 * 订阅
	 */
	@Subscribe
	public void onEvent(String type){
		switch (type){
			case "FROM":
				break;
		}
	}
}
