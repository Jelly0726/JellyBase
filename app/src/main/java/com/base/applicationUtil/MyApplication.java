package com.base.applicationUtil;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.base.MapUtil.LocationTask;
import com.base.album.GlideAlbumLoader;
import com.base.crashlog.CrashApphandler;
import com.base.okGo.OkGoApp;
import com.base.sqldao.DBConfig;
import com.base.sqldao.MySQLiteOpenHelper;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.zhy.autolayout.config.AutoLayoutConifg;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
        // AutoLayout适配
        AutoLayoutConifg.getInstance().useDeviceSize();
        myApp=this;
        //图片选择
        Album.initialize(
                AlbumConfig.newBuilder(myApp)
                        .setAlbumLoader(new GlideAlbumLoader()) // This is not necessary.
                        .setLocale(Locale.getDefault())
                        .build()
        );
        File file = new File(AppUtils.getFileRoot(this)
                + File.separator +"SaveXML");
        if (!file.exists()) {
            file.mkdirs();
        }
        getThread = Executors.newCachedThreadPool();
        cachedThreadPool = Executors.newCachedThreadPool();
        fixedThreadPool = Executors.newFixedThreadPool(2);
        scheduledThreadPool = Executors.newScheduledThreadPool(2);
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        HermesEventBus.getDefault().init(this);
        //初始化一下就行了，别忘记了  --奔溃日志
        CrashApphandler.getInstance().init(this);
    }
//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }
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
    /**
     * 获取手机唯一表示并转为uuid 加密
     */
    public String getuniqueId(){

        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        String imei=tm.getDeviceId();

        String simSerialNumber=tm.getSimSerialNumber();

        String androidId =android.provider.Settings.Secure.getString(getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid =new UUID(androidId.hashCode(), ((long)imei.hashCode() << 32) |simSerialNumber.hashCode());

        String uniqueId= deviceUuid.toString();

        return uniqueId;

    }
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        String version="V1.0.0";
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public int getVersionCode() {
        int versionCode=1;
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            versionCode = info.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }
    //添加Activity到容器中
    public void addActivity(Activity activity)
    {
        activities.add(activity);
    }
    public void deleteActivity(Activity activity){
        activities.remove(activity);
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
            System.exit(0);
        }catch(Exception e){
            e.printStackTrace();
        }
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

