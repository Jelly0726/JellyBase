package com.base.applicationUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.base.MapUtil.LocationTask;
import com.base.crashlog.CrashApphandler;
import com.base.okGo.OkGoApp;
import com.base.sqldao.DBConfig;
import com.base.sqldao.MySQLiteOpenHelper;
import com.jelly.jellybase.BuildConfig;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.zhy.autolayout.config.AutoLayoutConifg;

import java.io.File;
import java.util.ArrayList;
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
    private ArrayList<Activity> activities=new ArrayList<Activity>();
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
        HermesEventBus.getDefault().init(this);
        //初始化一下就行了，别忘记了  --奔溃日志
        CrashApphandler.getInstance().init(this);
        //butterknife注解式绑定id
        ButterKnife.setDebug(BuildConfig.DEBUG);
        if (getPackageName().equals(getCurProcessName(this))) {
            // AutoLayout适配
            AutoLayoutConifg.getInstance().useDeviceSize();
            File file = new File(AppUtils.getFileRoot(this)
                    + File.separator + "SaveXML");
            if (!file.exists()) {
                file.mkdirs();
            }
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

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
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
    //添加Activity到容器中
    public void addActivity(Activity activity)
    {
        activities.add(activity);
    }
    public void deleteActivity(Activity activity){
        activities.remove(activity);
    }
    public void finishAllActivity(){
        for(Activity activity:activities){
            activity.finish();
        }
        activities.clear();
    }
    /**
     * 退出
     */
    public void exit() {
        try{
            for(Activity activity:activities){
                activity.finish();
            }
            activities.clear();
            LocationTask.getInstance(this).onDestroy();//销毁定位
            HermesEventBus.getDefault().destroy();
            System.exit(0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        LocationTask.getInstance(this).onDestroy();//销毁定位
        HermesEventBus.getDefault().destroy();
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
    private static systemdb.DaoMaster daoMaster;
    private static systemdb.DaoSession daoSession;
    /**
     * 取得DaoMaster
     *
     * @param context
     * @return
     */
    public static systemdb.DaoMaster getDaoMaster(Context context)
    {
        if (daoMaster == null)
        {
//            //获取数据库路径
//            String path=context.getDatabasePath("NuoMember").getPath();
//            File file=new File(path);
//            //判断数据库文件是否存在
//            if(file.exists()){//存在就清除
//                //清除应用所有数据库
//                AppUtils.cleanDatabases(context);
//            }

            MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context, DBConfig.DBNAME,
                    null);
            daoMaster = new systemdb.DaoMaster(helper.getWritableDatabase());
            //DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context,Config.DBNAME, null);
            daoMaster = new systemdb.DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }
    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public static systemdb.DaoSession getDaoSession(Context context)
    {
        if (daoSession == null)
        {
            if (daoMaster == null)
            {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}

