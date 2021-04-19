package com.base

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.text.TextUtils
import android.util.Log
import androidx.multidex.MultiDex
import butterknife.ButterKnife
import cn.jpush.android.api.JPushInterface
import com.base.MapUtil.LocationTask
import com.base.httpmvp.retrofitapi.token.GlobalToken
import com.bumptech.glide.Glide
import com.jelly.baselibrary.album.GlideAlbumLoader
import com.jelly.baselibrary.applicationUtil.AppPrefs
import com.jelly.baselibrary.applicationUtil.ChangeLanguageHelper
import com.base.cockroach.Cockroach
import com.base.cockroach.CrashUtils
import com.base.cockroach.ExceptionHandler
import com.base.config.IntentAction
import com.jelly.baselibrary.config.ConfigKey
import com.base.daemon.DaemonEnv
import com.base.sqldao.DBManager
import com.jelly.baselibrary.log.LogUtils
import com.jelly.baselibrary.toast.ToastUtils
import com.jelly.jellybase.BuildConfig
import com.jelly.jellybase.R
import com.jelly.jellybase.server.TraceServiceImpl
import com.jeremyliao.liveeventbus.LiveEventBus
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsListener
import com.wenming.library.LogReport
import com.wenming.library.save.imp.CrashWriter
import com.wenming.library.upload.email.EmailReporter
import com.wenming.library.upload.http.HttpReporter
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import hugo.weaving.DebugLog
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.unit.Subunits
import systemdb.Login
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

/**
 * Created by Administrator on 2015/10/8.
 */
@DebugLog
class BaseApplication : Application() {
    companion object {
        private lateinit var myApp: BaseApplication
        private var backStage = true //后台运行
        var isMainState = false //MianAcitivity是否运行
        var areacode = "0" //
        var isVampix = false //是否App黑白化
        var login: Login? = null //
        /**
         * 获取本app可用的Context
         *
         * @return 返回一个本类的context
         */
        @JvmStatic
        val instance: BaseApplication
            get() {
                if (myApp == null) {
                    myApp = BaseApplication()
                }
                return myApp
            }
    }
    /**
     * 是否后台运行
     * @return
     */
    var isBackStage: Boolean
        get() = backStage
        set(backStags) {
           backStage = backStags
        }
    override fun onCreate() {
        super.onCreate()
        myApp = this
        LogUtils.init(BuildConfig.LOG_DEBUG)
        //RxJava2 取消订阅后，抛出的异常无法捕获，导致程序崩溃
        RxJavaPlugins.setErrorHandler(Consumer<Throwable?> { })
        //初始化一下就行了，别忘记了  --奔溃日志
        installCockroach() //崩溃后自动重启并发送崩溃信息
        //        initCrashReport();//崩溃后不重启保存崩溃信息，下次启动压缩崩溃信息并发送
        //多语言切换初始化
        ChangeLanguageHelper.init(this)
        //屏幕自动适配 对单位的自定义配置, 请在 App 启动时完成
        configUnits()
        LiveEventBus.config()
                .lifecycleObserverAlwaysActive(true) //  配置LifecycleObserver（如Activity）接收消息的模式（默认值true）：
        //true：整个生命周期（从onCreate到onDestroy）都可以实时收到消息
        // false：激活状态（Started）可以实时收到消息，非激活状态（Stoped）无法实时收到消息，
        // 需等到Activity重新变成激活状态，方可收到消息
        if (packageName == curProcessName) {
            //初始化数据库
            DBManager.getDBManager().init(this)
            //            HermesEventBus.getDefault().init(this);
            //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
            DaemonEnv.initialize(this, TraceServiceImpl::class.java, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL)

            //butterknife注解式绑定id
            ButterKnife.setDebug(BuildConfig.DEBUG)
            //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
            //TbsDownloader.needDownload(getApplicationContext(), false);
            val cb: QbSdk.PreInitCallback = object : QbSdk.PreInitCallback {
                override fun onViewInitFinished(arg0: Boolean) {
                    // TODO Auto-generated method stub
                    Log.e("app", " onViewInitFinished is $arg0")
                }

                override fun onCoreInitFinished() {
                    // TODO Auto-generated method stub
                }
            }
            QbSdk.setTbsListener(object : TbsListener {
                override fun onDownloadFinish(i: Int) {
                    Log.d("app", "onDownloadFinish")
                }

                override fun onInstallFinish(i: Int) {
                    Log.d("app", "onInstallFinish")
                }

                override fun onDownloadProgress(i: Int) {
                    Log.d("app", "onDownloadProgress:$i")
                }
            })
            QbSdk.initX5Environment(applicationContext, cb)
            //极光推送
            JPushInterface.setDebugMode(true)
            JPushInterface.init(this)
        }
        //初始化图片选择器
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(GlideAlbumLoader())
                .build())
    }

    private val curProcessName: String?
        private get() {
            val pid = Process.myPid()
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
                var processName = reader.readLine()
                if (!TextUtils.isEmpty(processName)) {
                    processName = processName.trim { it <= ' ' }
                }
                return processName
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            } finally {
                try {
                    reader?.close()
                } catch (exception: IOException) {
                    exception.printStackTrace()
                }
            }
            return null
        }
    /**
     * 注意!!! 布局时的实时预览在开发阶段是一个很重要的环节, 很多情况下 Android Studio 提供的默认预览设备并不能完全展示我们的设计图
     * 所以我们就需要自己创建模拟设备, 以下链接是给大家的福利, 按照链接中的操作可以让预览效果和设计图完全一致!
     * @see [dp、pt、in、mm 这四种单位的模拟设备创建方法](https://github.com/JessYanCoding/AndroidAutoSize/blob/master/README-zh.md.preview)
     *
     *
     * v0.9.0 以后, AndroidAutoSize 强势升级, 将这个方案做到极致, 现在支持5种单位
     */
    private fun configUnits() {
        //AndroidAutoSize 默认开启对 dp 的支持, 调用 UnitsManager.setSupportDP(false); 可以关闭对 dp 的支持
        //主单位 dp 和 副单位可以同时开启的原因是, 对于旧项目中已经使用了 dp 进行布局的页面的兼容
        //让开发者的旧项目可以渐进式的从 dp 切换到副单位, 即新页面用副单位进行布局, 然后抽时间逐渐的将旧页面的布局单位从 dp 改为副单位
        //最后将 dp 全部改为副单位后, 再使用 UnitsManager.setSupportDP(false); 将 dp 的支持关闭, 彻底隔离修改 density 所造成的不良影响
        //如果项目完全使用副单位, 则可以直接以像素为单位填写 AndroidManifest 中需要填写的设计图尺寸, 不需再把像素转化为 dp
        AutoSizeConfig.getInstance().unitsManager
                .setSupportDP(false) //当使用者想将旧项目从主单位过渡到副单位, 或从副单位过渡到主单位时
                //因为在使用主单位时, 建议在 AndroidManifest 中填写设计图的 dp 尺寸, 比如 360 * 640
                //而副单位有一个特性是可以直接在 AndroidManifest 中填写设计图的 px 尺寸, 比如 1080 * 1920
                //但在 AndroidManifest 中却只能填写一套设计图尺寸, 并且已经填写了主单位的设计图尺寸
                //所以当项目中同时存在副单位和主单位, 并且副单位的设计图尺寸与主单位的设计图尺寸不同时, 可以通过 UnitsManager#setDesignSize() 方法配置
                //如果副单位的设计图尺寸与主单位的设计图尺寸相同, 则不需要调用 UnitsManager#setDesignSize(), 框架会自动使用 AndroidManifest 中填写的设计图尺寸
                //                .setDesignSize(2160, 3840)
                //AndroidAutoSize 默认开启对 sp 的支持, 调用 UnitsManager.setSupportSP(false); 可以关闭对 sp 的支持
                //如果关闭对 sp 的支持, 在布局时就应该使用副单位填写字体的尺寸
                //如果开启 sp, 对其他三方库控件影响不大, 也可以不关闭对 sp 的支持, 这里我就继续开启 sp, 请自行斟酌自己的项目是否需要关闭对 sp 的支持
                .setSupportSP(false).supportSubunits = Subunits.MM
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    val isLogin: Boolean
        get() = if (!TextUtils.isEmpty(GlobalToken.getToken().token)) {
            if (login == null) login = AppPrefs.getObject(instance,
                    ConfigKey.LOGIN_INFO, Login::class.java) as Login
            login != null
        } else {
            false
        }

    /**
     * 进入登陆界面
     */
    fun goLoginActivity() {
        try {
            val intent = Intent()
            //intent.setClass(this, LoginActivity.class);
            intent.action = IntentAction.ACTION_LOGIN
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 进入主界面
     */
    fun goMainActivity() {
        try {
            val intent = Intent()
            //intent.setClass(this, MainActivity.class);
            intent.action = IntentAction.ACTION_MAIN
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 进入引导页界面
     */
    fun goGuideActivity() {
        try {
            val intent = Intent()
            intent.setClass(this, GuideActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //初始化Cockroach，开启crash防护
    private fun installCockroach() {
        val sysExcepHandler = Thread.getDefaultUncaughtExceptionHandler()
        Cockroach.install(this, object : ExceptionHandler() {
            override fun onUncaughtExceptionHappened(thread: Thread, throwable: Throwable) {
                Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:$thread<---", throwable)
                CrashUtils.getInstance().saveErrorInfo(myApp, throwable)
                Handler(Looper.getMainLooper()).post { ToastUtils.show(myApp, R.string.safe_mode_excep_tips) }
            }

            override fun onBandageExceptionHappened(throwable: Throwable) {
                throwable.printStackTrace() //打印警告级别log，该throwable可能是最开始的bug导致的，无需关心
                ToastUtils.show(myApp, "Cockroach Worked")
            }

            override fun onEnterSafeMode() {
                val tips = R.string.safe_mode_tips
                ToastUtils.show(myApp, R.string.safe_mode_tips)
                //
//                if (BuildConfig.DEBUG) {
//                    Intent intent = new Intent(myApp, DebugSafeModeTipActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
            }

            override fun onMayBeBlackScreen(e: Throwable) {
                val thread = Looper.getMainLooper().thread
                Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:$thread<---", e)
                //黑屏时建议直接杀死app
                sysExcepHandler.uncaughtException(thread, RuntimeException("black screen"))
            }
        })
    }

    private fun initCrashReport() {
        LogReport.getInstance()
                .setCacheSize((30 * 1024 * 1024).toLong()) //支持设置缓存大小，超出后清空
                .setLogDir(applicationContext, "sdcard/" + this.getString(this.applicationInfo.labelRes) + "/") //定义路径为：sdcard/[app name]/
                .setWifiOnly(true) //设置只在Wifi状态下上传，设置为false为Wifi和移动网络都上传
                .setLogSaver(CrashWriter(applicationContext)) //支持自定义保存崩溃信息的样式
                //.setEncryption(new AESEncode()) //支持日志到AES加密或者DES加密，默认不开启
                .init(applicationContext)
        initEmailReporter()
    }

    /**
     * 使用EMAIL发送日志
     */
    private fun initEmailReporter() {
        val email = EmailReporter(this)
        email.setReceiver("jieye_1@163.com") //收件人
        email.setSender("vicdaner@163.com") //发送人邮箱
        email.setSendPassword("1097382492email") //邮箱的客户端授权码，注意不是邮箱密码
        email.setSMTPHost("smtp.163.com") //SMTP地址
        email.setPort("465") //SMTP 端口
        LogReport.getInstance().setUploadType(email)
    }

    /**
     * 使用HTTP发送日志
     */
    private fun initHttpReporter() {
        val http = HttpReporter(this)
        http.setUrl("http://crashreport.jd-app.com/your_receiver") //发送请求的地址
        http.setFileParam("fileName") //文件的参数名
        http.setToParam("to") //收件人参数名
        http.setTo("你的接收邮箱") //收件人
        http.setTitleParam("subject") //标题
        http.setBodyParam("message") //内容
        LogReport.getInstance().setUploadType(http)
    }

    /**
     * 退出
     */
    fun exit() {
        try {
            HermesManager.getHermesManager().clear()
            AppSubject.getInstance().detachAll()
            LocationTask.getInstance(this).onDestroy() //销毁定位
            if (packageName == curProcessName) {
//                EventBus.getDefault().destroy();
            }
            // 杀死该应用进程
            Process.killProcess(Process.myPid())
            System.exit(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onTerminate() {
        // 程序终止的时候执行
        LocationTask.getInstance(this).onDestroy() //销毁定位
        if (packageName == curProcessName) {
//            EventBus.getDefault().destroy();
        }
        super.onTerminate()
    }

    override fun onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory()
        //内存不足时清理Glide的缓存
        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory()
        }
        Glide.get(this).trimMemory(level)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}