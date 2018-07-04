package com.base.appManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.base.MapUtil.LocationTask;
import com.base.applicationUtil.ChangeLanguageHelper;
import com.base.bgabanner.GuideActivity;
import com.base.config.IntentAction;
import com.base.crashlog.CrashApphandler;
import com.base.daemon.DaemonEnv;
import com.base.eventBus.HermesManager;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.okGo.OkGoApp;
import com.jelly.jellybase.BuildConfig;
import com.jelly.jellybase.server.TraceServiceImpl;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.zhy.autolayout.config.AutoLayoutConifg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * Created by Administrator on 2015/10/8.
 */
public class MyApplication extends OkGoApp {
    private static MyApplication myApp;
    private ExecutorService cachedThreadPool;//可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程
    private ExecutorService fixedThreadPool ;//创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
    private ScheduledExecutorService scheduledThreadPool;//定长线程池，支持定时及周期性任务执行
    private ExecutorService singleThreadExecutor;//单线线程池 线程会在队列中等待
    private static ExecutorService getThread;//线程池
    private static  boolean backStage=true;//后台运行
    private static  boolean mainState=false;//MianAcitivity是否运行
    public static  String areacode="0";//
    public static boolean isMainState() {
        return mainState;
    }

    public static void setMainState(boolean mainState) {
        MyApplication.mainState = mainState;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApp=this;
        //初始化一下就行了，别忘记了  --奔溃日志
        CrashApphandler.getInstance().init(this);
        //多语言切换初始化
        ChangeLanguageHelper.init(this);
        if (getPackageName().equals(getCurProcessName())) {

            HermesEventBus.getDefault().init(this);
            //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
            DaemonEnv.initialize(this, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);

            //butterknife注解式绑定id
            ButterKnife.setDebug(BuildConfig.DEBUG);
            // AutoLayout适配
            AutoLayoutConifg.getInstance().useDeviceSize();
            //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
            //TbsDownloader.needDownload(getApplicationContext(), false);
            QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
                @Override
                public void onViewInitFinished(boolean arg0) {
                    // TODO Auto-generated method stub
                    Log.e("app", " onViewInitFinished is " + arg0);
                }

                @Override
                public void onCoreInitFinished() {
                    // TODO Auto-generated method stub

                }
            };
            QbSdk.setTbsListener(new TbsListener() {
                @Override
                public void onDownloadFinish(int i) {
                    Log.d("app", "onDownloadFinish");
                }

                @Override
                public void onInstallFinish(int i) {
                    Log.d("app", "onInstallFinish");
                }

                @Override
                public void onDownloadProgress(int i) {
                    Log.d("app", "onDownloadProgress:" + i);
                }
            });
            QbSdk.initX5Environment(getApplicationContext(), cb);


            getThread = Executors.newCachedThreadPool();
            cachedThreadPool = Executors.newCachedThreadPool();
            fixedThreadPool = Executors.newFixedThreadPool(2);
            scheduledThreadPool = Executors.newScheduledThreadPool(2);
            singleThreadExecutor = Executors.newSingleThreadExecutor();
            //极光推送
            JPushInterface.setDebugMode(true);
            JPushInterface.init(this);
        }
    }

    private String getCurProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    /**
     * 获取本app可用的Context
     *
     * @return 返回一个本类的context
     */
    public static MyApplication getMyApp(){
        if(myApp==null){
            myApp=new MyApplication();
        }
        return myApp;
    }
    public boolean isLogin(){
        if (!TextUtils.isEmpty(GlobalToken.getToken().getToken())) {
            return true;
        }else{
            return false;
        }
    }
    /**
     * 进入登陆界面
     */
    public void goLoginActivity() {
        try{
            Intent intent = new Intent();
            //intent.setClass(this, LoginActivity.class);
            intent.setAction(IntentAction.ACTION_LOGIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public ExecutorService getThread() {
        if(getThread==null){
            getThread = Executors.newCachedThreadPool();
        }
        return getThread;
    }
    /**
     * 可缓存线程池
     * @return
     */
    public ExecutorService getCachedThread() {
        /*
        例代码如下：
        cachedThreadPool.execute(runnable)
         */
        return cachedThreadPool;
    }

    /**
     * 定长线程池，可控制线程最大并发数
     * @return
     */
    public ExecutorService getFixedThread() {
         /*
        例代码如下：
        fixedThreadPool.execute(runnable)
        */
        return fixedThreadPool;
    }
    /**
     *定长线程池，支持定时及周期性任务执行
     */
    public ExecutorService getScheduledThread() {

        /*
         * 延迟执行示例代码如下：
         * scheduledThreadPool.schedule(runnable, 3, TimeUnit.SECONDS);
         表示延迟3秒执行
         * 定期执行示例代码如下：
         scheduledThreadPool.scheduleAtFixedRate(runnable, 1, 3, TimeUnit.SECONDS);
         表示延迟1秒后每3秒执行一次。
         */
        return scheduledThreadPool;
    }

    /**
     * 单线线程池 线程会在队列中等待
     * @return
     */
    public ExecutorService getSingleThread() {
        /*
        例代码如下：
        singleThreadExecutor.execute(runnable)
         */
        return singleThreadExecutor;
    }
    public void setThread(ExecutorService getordrth) {
        this.getThread = getordrth;
    }
    /**
     * 退出
     */
    public void exit() {
        try{
            HermesManager.getHermesManager().clear();
            AppSubject.getAppSubject().detachAll();
            LocationTask.getInstance(this).onDestroy();//销毁定位
            if (getPackageName().equals(getCurProcessName())) {
                HermesEventBus.getDefault().destroy();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        LocationTask.getInstance(this).onDestroy();//销毁定位
        if (getPackageName().equals(getCurProcessName())) {
            HermesEventBus.getDefault().destroy();
        }
        super.onTerminate();
    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }
    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    /**
     * 是否后台运行
     * @return
     */
    public boolean isBackStage() {
        return backStage;
    }

    public void setBackStage(boolean backStage) {
        this.backStage = backStage;
    }
}

