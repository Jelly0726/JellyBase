package com.base.crashlog;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import com.base.config.BaseConfig;
import com.base.log.DebugLog;
import com.jelly.jellybase.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 奔溃日志 保存为SharedPreferences
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
        if (isSendErr()) {
            mContext = context;
            defaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
            //设置系统的默认异常处理器
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        //把错误信息保存在sp中，然后在下次进入页面的时候再上传错误信息
        saveErrorInfo(throwable);
        if (defaultCrashHandler != null) {
            //如果在自定义异常处理器之前，系统有自己的默认异常处理器的话，调用它来处理异常信息
            defaultCrashHandler.uncaughtException(thread, throwable);
        } else {
            //延时1秒杀死进程
            SystemClock.sleep(2000);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
    private boolean isSendErr() {
        return BuildConfig.LOG_DEBUG;
    }
    public void sendError() {
        if (isSendErr()) {
            final SharedPreferences sp = mContext.getSharedPreferences("errorInfo", Context.MODE_PRIVATE);
            String data = sp.getString("data", "");
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
                            DebugLog.i("response="+ss);
                            JSONObject object=new JSONObject(ss);
                            if (object.getInt("errcode")==0){
                                sp.edit().putString("data", "").commit();
                            }
                        }
                    } catch (IOException e) {
                        DebugLog.e("e="+e.getMessage());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private void saveErrorInfo(Throwable throwable) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getAppInfo(mContext));
        stringBuffer.append("崩溃时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
        stringBuffer.append("手机系统：").append(Build.VERSION.RELEASE).append("\n");
        stringBuffer.append("手机型号：").append(Build.MODEL).append("\n");
        stringBuffer.append("崩溃信息：").append(throwable.getMessage());
        SharedPreferences sp = mContext.getSharedPreferences("errorInfo", Context.MODE_PRIVATE);
        sp.edit().putString("data", stringBuffer.toString()).commit();
    }
    /**
     * 获取应用程序信息
     */
    public String getAppInfo(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return "应用包名：" + packageInfo.packageName + "\n应用版本：" + packageInfo.versionName + "\n";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}