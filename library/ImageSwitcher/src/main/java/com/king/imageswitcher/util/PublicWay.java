package com.king.imageswitcher.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 存放所有的list在最后退出时一起关闭
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:50:49
 */
public class PublicWay {
	public static List<Activity> activityList = new ArrayList<Activity>();
	public static int num = 6;
	public static void remove(Activity activity){
		activityList.remove(activity);
	}
	public static void finshAll(){
		for(Activity ac:activityList){
			ac.finish();
		}
		activityList.clear();
	}
	public static void finsh(Activity activity){
		int i=activityList.indexOf(activity);
		if(i!=-1){
			activityList.get(i).finish();
			activityList.remove(activity);
		}
	}
}
