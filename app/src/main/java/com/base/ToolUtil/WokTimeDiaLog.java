package com.base.ToolUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

import com.base.applicationUtil.AppPrefs;
import com.base.applicationUtil.MyApplication;
import com.base.config.ConfigKey;
import com.base.config.IntentAction;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 在线时长
 * @author Administrator
 *
 */
@SuppressLint("HandlerLeak")
public class WokTimeDiaLog {
	private Context context;
	private Timer timer=null;
	private TimerTask task=null;
	private  long  Count=0;
	private HashMap<String, String> map;
	private Intent myIntent;
	private volatile long  TimerNuit=60;
	private volatile int SETTING_100MILLISECOND_ID=0;
	private volatile int settingTimerNuit=SETTING_100MILLISECOND_ID;
	//创建对象
	static WokTimeDiaLog wokTime=new WokTimeDiaLog();
	/**
	 * 私有化构造函数
	 */
	private WokTimeDiaLog(){

	}
	/**
	 * 提供获取对象的方法
	 * @return 对象
	 */
	public static WokTimeDiaLog getWokTimeDiaLog() {
		return wokTime;
	}
	/**
	 * 设置上下文
	 * @param context
	 */
	public void setContext(Context context){
		this.context=context;
	}
	/**
	 * 自定义dialog对话框
	 */
	public void showWaitDiaLog(){

	}
	/**
	 * 开始计时
	 * @return
	 */
	public void getStarOnClick() {
		Count= AppPrefs.getLong(MyApplication.getMyApp(),ConfigKey.WORKCONNT,0l);
		//map=new HashMap<String, String>();
		//myIntent = new Intent();//创建Intent对象
		//handler1.postDelayed(runnable,1000);         // 开始Timer
		map=new HashMap<String, String>();
		myIntent = new Intent();//创建Intent对象
		//util.setIsJs(true);
		if(null==timer){
			if(null==task){
				task=new TimerTask() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Count = Count + 10;
						int totalSec = 0;
						int totalmai = 0;
						totalSec = (int) (Count / 10);
						totalmai = (int) (Count / 600);
						int shi = totalmai / 60;
						int mai = totalmai % 60;
						int sec = totalSec % 60;
						try {
							String wiatTime = String.format("%1$02d:%2$02d:%3$02d", shi, mai, sec);
							AppPrefs.putString(MyApplication.getMyApp(),ConfigKey.WORKTIME,wiatTime);
							AppPrefs.putLong(MyApplication.getMyApp(),ConfigKey.WORKCONNT,Count);
							if (map != null && myIntent != null) {
								map.put("id", "a");
								map.put("WokeTime", wiatTime);
								myIntent.setAction(IntentAction.NOTICE);
								myIntent.putExtra("notice", map);
								context.sendBroadcast(myIntent);//发送广播
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
			}
			timer=new Timer(true);
			timer.schedule(task,TimerNuit,TimerNuit);

		}
	}
	/**
	 * 停止按钮监听事件
	 * @return
	 */
	public OnClickListener getStopOnClick() {
		// TODO Auto-generated method stub
		try {
			//	handler1.removeCallbacks(runnable);           //停止Timer
			if(task!=null){
				task.cancel();
				task=null;
			}
			if(timer!=null){
				timer.cancel();
				timer.purge();
				timer=null;
			}
			//util.setIsJs(false);
			if(map!=null){
				map.clear();
				map=null;
			}
			myIntent=null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new OnClickListener() {
			@Override
			public void onClick(View v) {


			}
		};
	}
	private Handler handler1 = new Handler( );

	/*private Runnable runnable = new Runnable( ) {

		public void run ( ) {
			if (util.getIsWorking()) {//判断是否上班
				//if(true){
				@SuppressWarnings("static-access")
				String hour = new MyDate().getDatemiao();
				@SuppressWarnings("static-access")
				String da = new MyDate().getDateDaEN();
				if ((hour.equals("00:00:00") || hour.equals("24:00:00")) || !da.equals(util.getWokeda())) {
					Count = 0;
					util.setWokeda(da);
				}
				Count = Count + 10;
				int totalSec = 0;
				int totalmai = 0;
				totalSec = (int) (Count / 10);
				totalmai = (int) (Count / 600);
				int shi = totalmai / 60;
				int mai = totalmai % 60;
				int sec = totalSec % 60;
				try {
					String wiatTime = String.format("%1$02d:%2$02d:%3$02d", shi, mai, sec);
					util.setWokeTime(wiatTime);
					util.setWokeConnt(Count);
					if (map != null && myIntent != null) {
						map.put("id", "a");
						map.put("WokeTime", wiatTime);
						myIntent.setAction(Constants.NOTICE);
						myIntent.putExtra("notice", map);
						context.sendBroadcast(myIntent);//发送广播
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler1.postDelayed(runnable, 1000);     //postDelayed(this,1000)方法安排一个Runnable对象到主线程队列中
			} else {
				getStopOnClick();//停止计时
			}
		}

	};*/
}
