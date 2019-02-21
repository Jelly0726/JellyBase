package com.base.appManager;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.base.MapUtil.LocationTask;
import com.base.album.GlideAlbumLoader;
import com.base.applicationUtil.ChangeLanguageHelper;
import com.base.bgabanner.GuideActivity;
import com.base.cockroach.Cockroach;
import com.base.cockroach.CrashUtils;
import com.base.cockroach.ExceptionHandler;
import com.base.config.IntentAction;
import com.base.daemon.DaemonEnv;
import com.base.eventBus.HermesManager;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.sqldao.DBManager;
import com.base.toast.ToastUtils;
import com.jelly.jellybase.BuildConfig;
import com.jelly.jellybase.server.TraceServiceImpl;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.wenming.library.LogReport;
import com.wenming.library.save.imp.CrashWriter;
import com.wenming.library.upload.email.EmailReporter;
import com.wenming.library.upload.http.HttpReporter;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.zhy.autolayout.config.AutoLayoutConifg;

import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import hugo.weaving.DebugLog;


/**
 * Created by Administrator on 2015/10/8.
 */
@DebugLog
public class BaseApplication extends Application {
    private static BaseApplication myApp;
    private static  boolean backStage=true;//后台运行
    private static  boolean mainState=false;//MianAcitivity是否运行
    public static  String areacode="0";//
    public static boolean isMainState() {
        return mainState;
    }

    public static void setMainState(boolean mainState) {
        BaseApplication.mainState = mainState;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApp=this;
        //初始化一下就行了，别忘记了  --奔溃日志
        installCockroach();//崩溃后自动重启并发送崩溃信息
//        initCrashReport();//崩溃后不重启保存崩溃信息，下次启动压缩崩溃信息并发送
        //多语言切换初始化
        ChangeLanguageHelper.init(this);
        if (getPackageName().equals(getCurProcessName())) {
            //初始化数据库
            DBManager.getDBManager().init(this);
//            HermesEventBus.getDefault().init(this);
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
            //极光推送
            JPushInterface.setDebugMode(true);
            JPushInterface.init(this);
        }
        //初始化图片选择器
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new GlideAlbumLoader())
                .build());
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
        MultiDex.install(base);
    }
    /**
     * 获取本app可用的Context
     *
     * @return 返回一个本类的context
     */
    public static BaseApplication getInstance(){
        if(myApp==null){
            myApp=new BaseApplication();
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
    //初始化Cockroach，开启crash防护
    private void installCockroach() {
        final Thread.UncaughtExceptionHandler sysExcepHandler = Thread.getDefaultUncaughtExceptionHandler();
        Cockroach.install(new ExceptionHandler() {
            @Override
            protected void onUncaughtExceptionHappened(Thread thread, Throwable throwable) {
                Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:" + thread + "<---", throwable);
                CrashUtils.getInstance().saveErrorInfo(myApp, throwable);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.show(myApp,"捕获到导致崩溃的异常并发送");
                    }
                });
            }

            @Override
            protected void onBandageExceptionHappened(Throwable throwable) {
                throwable.printStackTrace();//打印警告级别log，该throwable可能是最开始的bug导致的，无需关心
                ToastUtils.show(myApp,"捕获到导致崩溃的异常");
            }

            @Override
            protected void onEnterSafeMode() {
                ToastUtils.show(myApp,"已经进入安全模式");
            }

            @Override
            protected void onMayBeBlackScreen(Throwable e) {
                Thread thread = Looper.getMainLooper().getThread();
                Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:" + thread + "<---", e);
                //黑屏时建议直接杀死app
                sysExcepHandler.uncaughtException(thread, new RuntimeException("black screen"));
            }

        });

    }
    private void initCrashReport() {
        LogReport.getInstance()
                .setCacheSize(30 * 1024 * 1024)//支持设置缓存大小，超出后清空
                .setLogDir(getApplicationContext(), "sdcard/" + this.getString(this.getApplicationInfo().labelRes) + "/")//定义路径为：sdcard/[app name]/
                .setWifiOnly(true)//设置只在Wifi状态下上传，设置为false为Wifi和移动网络都上传
                .setLogSaver(new CrashWriter(getApplicationContext()))//支持自定义保存崩溃信息的样式
                //.setEncryption(new AESEncode()) //支持日志到AES加密或者DES加密，默认不开启
                .init(getApplicationContext());
        initEmailReporter();
    }

    /**
     * 使用EMAIL发送日志
     */
    private void initEmailReporter() {
        EmailReporter email = new EmailReporter(this);
        email.setReceiver("jieye_1@163.com");//收件人
        email.setSender("vicdaner@163.com");//发送人邮箱
        email.setSendPassword("1097382492email");//邮箱的客户端授权码，注意不是邮箱密码
        email.setSMTPHost("smtp.163.com");//SMTP地址
        email.setPort("465");//SMTP 端口
        LogReport.getInstance().setUploadType(email);
    }
    /**
     * 使用HTTP发送日志
     */
    private void initHttpReporter() {
        HttpReporter http = new HttpReporter(this);
        http.setUrl("http://crashreport.jd-app.com/your_receiver");//发送请求的地址
        http.setFileParam("fileName");//文件的参数名
        http.setToParam("to");//收件人参数名
        http.setTo("你的接收邮箱");//收件人
        http.setTitleParam("subject");//标题
        http.setBodyParam("message");//内容
        LogReport.getInstance().setUploadType(http);
    }
    /**
     * 退出
     */
    public void exit() {
        try{
            HermesManager.getHermesManager().clear();
            AppSubject.getInstance().detachAll();
            LocationTask.getInstance(this).onDestroy();//销毁定位
            if (getPackageName().equals(getCurProcessName())) {
//                EventBus.getDefault().destroy();
            }
            // 杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        LocationTask.getInstance(this).onDestroy();//销毁定位
        if (getPackageName().equals(getCurProcessName())) {
//            EventBus.getDefault().destroy();
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

