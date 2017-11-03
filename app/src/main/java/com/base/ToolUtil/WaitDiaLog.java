package com.base.ToolUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 等待时长
 * @author Administrator
 *
 */
@SuppressLint("HandlerLeak")
public class WaitDiaLog {
	private Context context;
	private Timer timer=null;
	private TimerTask task=null;
	private long Count=0;
	private volatile long  TimerNuit=60;
	private volatile int SETTING_100MILLISECOND_ID=0;
	private volatile int settingTimerNuit=SETTING_100MILLISECOND_ID;
	private Intent myIntent;
	private HashMap<String, Object> map;
	//创建对象
	static WaitDiaLog wait=new WaitDiaLog();
	/**
	 * 私有化构造函数
	 */
	private WaitDiaLog(){

	}
	/**
	 * 提供获取对象的方法
	 * @return 对象
	 */
	public static WaitDiaLog getWaitDiaLog() {

		return wait;

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
	 * 停止按钮监听事件
	 * @return
	 */
	public OnClickListener getStopOnClick() {
		// TODO Auto-generated method stub
		try {
			task.cancel();
			task=null;
			timer.cancel();
			timer.purge();
			timer=null;
			map.clear();
			map=null;
			myIntent=null;
			//util.setIsJs(false);


		} catch (Exception e) {
			e.printStackTrace();
		}

		return new OnClickListener() {
			@Override
			public void onClick(View v) {


			}
		};
	}
}
