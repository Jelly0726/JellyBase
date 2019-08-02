package com.base.crashlog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 奔溃日志
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    //系统默认的异常处理器
    private Thread.UncaughtExceptionHandler defaultCrashHandler;
    private static CrashHandler crashHandler = new CrashHandler();
    private Context mContext;
    //私有化构造函数
    private CrashHandler() {
    }
    //获取实例
    public static CrashHandler getInstance() {
        return crashHandler;
    }
    public void init(Context context) {
        mContext = context;
        defaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置系统的默认异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        //把错误信息发送邮箱
        if (!saveErrorInfo(throwable)&&defaultCrashHandler != null) {
            //如果此异常不处理则由系统自己处理，调用它来处理异常信息
            defaultCrashHandler.uncaughtException(thread, throwable);
        } else {
            //延时1秒杀死进程
            SystemClock.sleep(2000);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
    private boolean saveErrorInfo(Throwable throwable) {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(getAppInfo(mContext));
            stringBuffer.append("崩溃时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
            stringBuffer.append("手机系统：").append(Build.VERSION.RELEASE).append("\n");
            stringBuffer.append("android版本号API：" + Build.VERSION.SDK_INT).append("\n");
            stringBuffer.append("手机制造商:" + Build.MANUFACTURER).append("\n");
            stringBuffer.append("手机型号：").append(Build.MODEL).append("\n");
            stringBuffer.append("崩溃信息：").append(throwable.getMessage());
            //发送邮箱
            return true;
        }catch (Exception e){

        }
        return false;
    }
    /**
     * 获取应用程序信息
     */
    public String getAppInfo(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return "应用包名：" + packageInfo.packageName + "\n应用版本：" + packageInfo.versionName
                    +"\n应用版本号：" + packageInfo.versionCode+"\n";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}