package com.base.crashlog;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.base.applicationUtil.AppPrefs;
import com.base.toast.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * 异常收集类
 */
public class ExceptionCollectHandler implements Thread.UncaughtExceptionHandler {
    //单列
    private static ExceptionCollectHandler mException;
    //上下文
    private Context context;
    //异常收集信息
    private Map<String, String> map;
    //系统异常处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static Boolean ADD_SAVE_EXCEPTION = false;//表示 是否正在新增异常
    /**
     * 获取异常单列
     */
    public static ExceptionCollectHandler getSingle() {
        if (mException == null) {
            synchronized (ExceptionCollectHandler.class) {
                if (mException == null) {
                    mException = new ExceptionCollectHandler();
                }
            }
        }
        return mException;
    }

    /**
     * 初始化
     */
    public void init(Context context) {
        this.context = context.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置当前为默认处理异常类
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable ex) {
        ADD_SAVE_EXCEPTION  = true;
        exceptionHandler(ex);
        ADD_SAVE_EXCEPTION = false;
        //系统自己处理
        mDefaultHandler.uncaughtException(t, ex);

    }

    /**
     * exception处理
     *
     * @param ex 异常
     * @return true返回自己处理了异常，false异常处理失败 需要系统处理
     */
    private Boolean exceptionHandler(Throwable ex) {
        if (ex == null) {
            return false;
        } else {
            if (map == null) {
                map = new HashMap<String, String>();
            } else {
                map.clear();
            }
            collectDeviceAndVersionInfo();
            collectExceptionInfo(ex);
        }
        return false;
    }

    /**
     * 收集设备及应用版本信息
     */
    private void collectDeviceAndVersionInfo() {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo mPackageInfo = manager.getPackageInfo(context.getPackageName(),PackageManager.GET_ACTIVITIES);//版本信息
            int versionCode = mPackageInfo.versionCode;//版本号
            map.put("versionCode", versionCode + "");
            String versionName = mPackageInfo.versionName;//版本名称
            map.put("versionName", versionName);
            Field[] fields = Build.class.getDeclaredFields();//返回 Field 对象的一个数组，这些对象反映此 Class 对象所表示的类或接口所声明的所有字段。
            for (Field field : fields) {
                field.setAccessible(true);//设置private修饰的对象可获取
                map.put(field.getName(), field.get(null).toString());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收集异常
     */
    private void collectExceptionInfo(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        sb.append("[DateTime: " + System.currentTimeMillis() + "]\n");
        sb.append("[DeviceAndVersionInfo: ]\n");
        // 遍历map
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey().toLowerCase(Locale.getDefault());
            String value = entry.getValue();
            sb.append("  " + key + ": " + value + "\n");
        }
        // 将错误信息保留
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();
        String result = writer.toString();
        sb.append("[Excetpion: ]\n");
        sb.append(result);
        // 保存文件
        try {
            String fileName = "exception.log";
            String filePath = context.getFilesDir().getAbsolutePath() + "/exception/";
            File saveFile = new File(filePath);
            if (!saveFile.exists()) {//判断文件是否存在
                saveFile.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath + fileName);
            fileOutputStream.write(sb.toString().getBytes());//写入文件
            fileOutputStream.close();//关闭流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传异常(请在子线程调用该方法)
     * @return
     */
    public void uploadException(String url) throws IOException {
        String fileName = "exception.log";
        String filePath = context.getFilesDir().getAbsolutePath() + "/exception/";
        File file = new File(filePath+fileName);
        if(!file.exists()){//文件不存在
            return;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileInputStream inputStream = new FileInputStream(file);
        int readLength;
        byte[] b = new byte[1024];
        while ((readLength = inputStream.read(b)) != -1){
            bos.write(b,0,readLength);
        }
        inputStream.close();
        String exception = new String((bos.toByteArray()));
        Map<String,String> map = new HashMap<String, String>();
        map.put("content",exception);//异常内容
        try {
            int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES).versionCode;
            map.put("app_version",""+versionCode);//版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            map.put("app_version","没有获取到版本号");
        }
        map.put("phone_type",Build.MANUFACTURER+"");//手机型号
        map.put("_logkey","!&+l=qsrm0!&k1gm#+5i8p8*^w");
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setConnectTimeout(5*1000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);//设置可输出
        connection.setDoInput(true);//设置可读写
        connection.setUseCaches(false);//不使用缓存
        String token = (String) AppPrefs.getString(context,"token","");
        connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        if(!TextUtils.isEmpty(token)){
            connection.setRequestProperty("Authorization","Token "+token);
            connection.setRequestProperty("user-Agent","bd-android");
        }
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(new JSONObject(map).toString().getBytes());
        outputStream.flush();
        InputStream inputStream1 = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream1));
        StringBuilder builder = new StringBuilder();
        String inputLine = null;
        while (((inputLine = bufferedReader.readLine()) != null)){
            builder.append(inputLine).append("\n");
        }
        String content = builder.toString();
        try {
            JSONObject objResult = new JSONObject(content);
            try{
                objResult.getString("id");//未发生异常 表示上传成功
                if(!ADD_SAVE_EXCEPTION){
                    if(file.delete()){//上传成功 判断条件（不相同）
                        ToastUtils.show(context, "上传成功");
                    }
                }
            }catch (Exception e){
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            outputStream.close();
            if(inputStream1 != null){
                inputStream1.close();
            }
            if(inputStream1 != null){
                inputStream1.close();
            }
            if(bufferedReader != null){
                bufferedReader.close();
            }
        }
    }
}