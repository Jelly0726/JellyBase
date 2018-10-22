package com.base.crashlog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.base.Utils.FilesUtil;
import com.base.appManager.ExecutorManager;
import com.base.config.BaseConfig;
import com.jelly.jellybase.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CrashHandlerUtil implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandlerUtil";
    /**
     * 默认放在内存卡的root路径
     */
    private String CAHCE_CRASH_LOG = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator;
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandlerUtil INSTANCE = new CrashHandlerUtil();
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<>();

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
    private String crashTip = "应用开小差了，稍后重启下，亲！";

    public String getCrashTip() {
        return crashTip;
    }

    public void setCrashTip(String crashTip) {
        this.crashTip = crashTip;
    }


    private CrashHandlerUtil() {
    }


    public static CrashHandlerUtil getInstance() {
        return INSTANCE;
    }


    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     *
     * @param thread 线程
     * @param ex     异常
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e("error : ", e.getMessage());
                e.printStackTrace();
            }
            //退出程序
            //退出JVM(java虚拟机),释放所占内存资源,0表示正常退出(非0的都为异常退出)
            System.exit(0);
            //从操作系统中结束掉当前程序的进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param throwable 异常
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                throwable.printStackTrace();
                Toast.makeText(mContext,getCrashTip(),Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        saveCrashInfo2File(throwable);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx 上下文
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("error package info", e.getMessage());
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
//                Logger.e(field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e("error crash info", e.getMessage());
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex 异常
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        Log.e("error",sb.toString());
        if(BuildConfig.DEBUG) {
            return null;
        }

        /*
        这个 crashInfo 就是我们收集到的所有信息，可以做一个异常上报的接口用来提交用户的crash信息
         */
        String crashInfo = sb.toString();
        writerToFile(crashInfo);
        return null;
    }
    private void writerToFile(String s) {

        try {
            /**
             * 创建日志文件名称
             */
            String curtTimer = ""+ System.currentTimeMillis();
            if (formatter == null) {

                formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            }
            String timer = formatter.format(new Date());

            String fileName = "crash-"+timer+"-"+curtTimer+".log";
            /**
             * 创建文件夹
             */
            File folder = new File(CAHCE_CRASH_LOG);

            if (!folder.exists())
                folder.mkdirs();

            /**
             * 创建日志文件
             */
            File file = new File(folder.getAbsolutePath()+ File.separator+fileName);

            if (!file.exists())
                file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(s);
            bufferedWriter.flush();
            bufferedWriter.close();

            //sendCrashLogToServer(folder, file);

        }catch (Exception e) {
            Log.e(TAG, "writerToFile - "+e.getMessage());
        }
    }
    /**
     * 过滤.log的文件
     */
    public class CrashLogFliter implements FileFilter {

        @Override
        public boolean accept(File file) {

            if (file.getName().endsWith(".log"))
                return true;
            return false;
        }
    }
    private boolean isSendErr() {
        return BuildConfig.SEND_ERR;
    }
    public void sendCrash(){
        ExecutorManager.getInstance().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "file - "+CAHCE_CRASH_LOG);
                File file = new File(CAHCE_CRASH_LOG);
                if (file.exists()) {
                    if (file != null && file.isDirectory()) {
                        File[] files = file.listFiles(new CrashLogFliter());
                        if (files!=null) {
                            for (int i = 0; i < files.length; i++) {
                                sendCrashLogToServer(file, files[i]);
                            }
                        }
                    }
                }
            }
        });
    }
    public void sendCrashLogToServer(File folder,final File file) {
        Log.e("*********", "文件夹:"+folder.getAbsolutePath()+" - "+file.getAbsolutePath()+"");
        //发送服务端
        if (isSendErr()) {
            String data = FilesUtil.getInstance().read(file).toString();
            if (!TextUtils.isEmpty(data)) {
                JSONObject map=new JSONObject();
                try {
                    map.put("msgtype", "text");
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("content", data);
                    map.put("text", jsonObject1.toString());
                    OkHttpClient httpClient = new OkHttpClient();
                    MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                    RequestBody requestBody = RequestBody.create(JSON, map.toString());
                    Request request = new Request.Builder()
                            .url(BaseConfig.sendError_URL)
                            .post(requestBody)
                            .build();
                    try {
                        Response response = httpClient.newCall(request).execute();
                        if (response.isSuccessful()){
                            String ss=response.body().string();
                            Log.i("SSS","response="+ss);
                            JSONObject object=new JSONObject(ss);
                            if (object.getInt("errcode")==0){
                                boolean is= FilesUtil.getInstance().deleteDirectory(file);
                                Log.i("SSS","is="+is);
                            }
                        }
                    } catch (IOException e) {
                        Log.e("SSS","e="+e.getMessage());
                    }
                } catch (JSONException e) {
                    Log.e("SSS","e="+e.getMessage());
                }
            }

        }
    }
}
