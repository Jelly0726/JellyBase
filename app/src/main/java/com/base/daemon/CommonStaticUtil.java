package com.base.daemon;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.base.daemon.service.AccessibilityServices;
import com.base.daemon.service.NotificationService;

import java.util.List;
import java.util.Set;


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
     * 开启知监听服务
     * @param context
     */
    public static void starNotificationService(Context context){
        if (!isNotificationListenerEnabled(context)) {
            Intent c = new Intent(context, NotificationService.class);
            context.startService(c);
            toggleNotificationListenerService(context);
        } else {
            openNotificationListenSettings(context);
        }
    }
    /**
     * 开启知监听服务
     * @param context
     */
    public static void starAccessibilityService(Context context){
        Intent c = new Intent(context, AccessibilityServices.class);
        context.startService(c);
    }
    /**
     * 检测通知监听服务是否被授权
     * @param context
     * @return
     */
    private static boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }

    /**
     *打开通知监听设置页面
     */
    private static void openNotificationListenSettings(Context context) {
        try {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新触发系统的 rebind 操作
     * @param context
     */
    private static void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context,NotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(context,NotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}