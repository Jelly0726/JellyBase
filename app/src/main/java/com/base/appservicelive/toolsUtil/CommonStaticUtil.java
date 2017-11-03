package com.base.appservicelive.toolsUtil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.base.appservicelive.receiver.PushAlarmReceiver;
import com.base.appservicelive.service.LiveService;
import com.jelly.jellybase.R;

import java.util.List;


/**
 * 静态工具类：主要判断服务的开启，应用的前后台的运行情况、开启广播监听，每一分钟检测一次开启推送服务等

 * @author wangl(Mail:WangleiDree@gmail.com)
 */
public class CommonStaticUtil {

    private static final String TAG = "msg";
    private static final long ALARM_INTERVAL = 60*1000;         // 通知定时器
    /**
     * 用来判断服务是否在运行.
     * @param mContext 上下文
     * @param className  判断的服务名字
     * [url=home.php?mod=space&uid=309376]@return[/url] isRunning ：true 在运行  、false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        //默认标记：为false
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的服务
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(Integer.MAX_VALUE);
        //Log.i("msg","serviceList.size()="+serviceList.size());
        //如果没有，那么返回false
        if (!(serviceList.size() > 0)) {
            return false;
        }
        //如果有，那么迭代List，判断是否有当前某个服务
        for (int i = 0; i < serviceList.size(); i++) {
           //Log.i("msg","service="+serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                return true;
            }
        }
        return isRunning;
    }

    /**
     * 判断程序是否在当前前台运行
     *
     * @return  true程序在当前前台运行、false的时候程序不在当前前台运行
     */
    public static boolean isRunningForeground (Context context){
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        //The activity component at the top of the history stack of the task. This is what the user is currently doing.
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName()))
        {
            return true ;
        }
        return false ;
    }

    /**
     * 程序是否在前台队列中运行
     * @param mContext 上下文
     * @return true 标识是在队列里、false标识不在前台桟列
     */
    public static boolean isAppOnForeground(Context mContext) {
        // Returns a list of application processes that are running on the device
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = mContext.getApplicationContext().getPackageName();
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 开启后台推送服务
     * @param context 上下文
     */
    public static void startService(Context context){
        //Log.i(TAG, "----startService-class----------------");
        if(!CommonStaticUtil.isServiceRunning(context
                , LiveService.class.getName())){
            //Log.i(TAG, "Service 没有开启，开启推送服务........>");
            ServiceManager serviceManager = new ServiceManager(context);
            serviceManager.setNotificationIcon(R.drawable.icon_logo);
            serviceManager.startService();
        }else{
           // Log.i(TAG, "Service 推送服务已开启........>");
//            ServiceManager serviceManager = new ServiceManager(context);
//            serviceManager.setNotificationIcon(R.drawable.touxiang);
//            serviceManager.startService();
        }
    }

    /**
     * 关闭 推送服务
     * @param context 上下文
     */
    public static void stopService(Context context){
       // Log.i(TAG, "关闭 推送服务........");
        // 关闭推送服务
        ServiceManager serviceManager = new ServiceManager(context);
        serviceManager.setNotificationIcon(R.drawable.icon_logo);
        serviceManager.stopService();
        //删除配置文件
//        SharedPreferences preferences = context
//                .getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
//        Editor editor = preferences.edit();
//        editor.clear();
//        editor.commit();
    }

    /**
     * 开启广播监听，每一分钟检测一次开启推送服务
     * @param context 上下文传递
     */
    public static void startReceiver(Context context){
        //Log.i(TAG, "开启广播监听........");
        AlarmManager alarmManager = (AlarmManager)context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(context, PushAlarmReceiver.class);
        //发送一个广播
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        //1分钟定时重复发送--：即一分钟检测一次服务有没有开启，没有开启就开启服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            alarmManager.setWindow(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ALARM_INTERVAL , pendingIntent);
        }else {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ALARM_INTERVAL, pendingIntent);
        }
    }



    /**
     * 获取设备的deviceId用来做推送username
     * @param context
     * @return 返回设备的DeviceID号
     */
    public static String getDeiviceID(Context context){

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
              /*
               * 唯一的设备ID：
               * GSM手机的 IMEI 和 CDMA手机的 MEID.
               * Return null if device ID is not available.
               */
        return tm.getDeviceId();
    }

}