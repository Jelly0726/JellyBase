package com.base.log;

import android.os.Environment;
import android.util.Log;

import com.base.applicationUtil.AppUtils;
import com.jelly.jellybase.BuildConfig;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * 日志记录
 *
 */
public class LogUtil {
    /**
     * 开发阶段
     */
    private static final int DEVELOP = 0;
    /**
     * 内部测试阶段
     */
    private static final int DEBUG = 1;
    /**
     * 公开测试
     */
    private static final int BATE = 2;
    /**
     * 正式版
     */
    private static final int RELEASE = 3;

    /**
     * 当前阶段标示
     */
    private static int currentStage = DEVELOP;

    /**
     *
     */
    private static String path;
    private static File file;
    private static FileOutputStream outputStream;
    private static String pattern = "yyyy-MM-dd HH:mm:ss";

    static {

        if (AppUtils.isSDcardExist()) {
            if (AppUtils.getSDFreeSize() > 1) {
                File externalStorageDirectory = Environment.getExternalStorageDirectory();
                path = externalStorageDirectory.getAbsolutePath() + "/designateddriver/";
                File directory = new File(path);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                file = new File(new File(path), "log.txt");
                try {
                    outputStream = new FileOutputStream(file, true);
                } catch (FileNotFoundException e) {
                    //can't happen
                }
            }else{
                //storage space is insufficient
            }
        } else {
            //SDcard isn't Exist
        }
    }

    public static void info(String msg) {
        info(LogUtil.class, msg);
    }

    public static void info(Class clazz, String msg) {

        System.out.println(clazz.getSimpleName());
        System.out.println(msg);

        switch (currentStage) {
            case DEVELOP:
                // output to the console
                Log.i(clazz.getSimpleName(), msg);
                break;
            case DEBUG:
                // 在应用下面创建目录存放日志
                break;
            case BATE:
                // write to sdcard
                Date date = new Date();
                String time = DateFormatUtils.format(date, pattern);
                if (AppUtils.isSDcardExist()) {
                    if (outputStream != null && AppUtils.getSDFreeSize() > 1) {
                        try {
                            outputStream.write(time.getBytes());
                            String className = "";
                            if (clazz != null) {
                                className = clazz.getSimpleName();
                            }
                            outputStream.write(("    " + className + "\r\n").getBytes());

                            outputStream.write(msg.getBytes());
                            outputStream.write("\r\n".getBytes());
                            outputStream.flush();
                        } catch (IOException e) {

                        }
                    } else {
                        Log.i("SDCAEDTAG", "file is null or storage insufficient");
                    }
                }
                break;
            case RELEASE:
                // 一般不做日志记录
                break;
        }
    }
    public static void i(String mes){
        if(BuildConfig.LOG_DEBUG){
            //Debug，打印日志
            i(LogUtil.class.getName(),mes);
        }
    }
    public static void v(String mes){
        if(BuildConfig.LOG_DEBUG){
            //Debug，打印日志
            v(LogUtil.class.getName(),mes);
        }
    }
    public static void d(String mes){
        if(BuildConfig.LOG_DEBUG){
            //Debug，打印日志
            d(LogUtil.class.getName(),mes);
        }
    }
    public static void e(String mes){
        if(BuildConfig.LOG_DEBUG){
            //Debug，打印日志
            e(LogUtil.class.getName(),mes);
        }
    }
    public static void w(String mes){
        if(BuildConfig.LOG_DEBUG){
            //Debug，打印日志
            w(LogUtil.class.getName(),mes);
        }
    }
    public static void i(String title,String mes){
        if(BuildConfig.LOG_DEBUG){
            //Debug，打印日志
            Log.i(title,mes);
        }
    }
    public static void v(String title,String mes){
        if(BuildConfig.LOG_DEBUG){
            //Debug，打印日志
            Log.v(title,mes);
        }
    }
    public static void d(String title,String mes){
        if(BuildConfig.LOG_DEBUG){
            //Debug，打印日志
            Log.d(title,mes);
        }
    }
    public static void e(String title,String mes){
        if(BuildConfig.LOG_DEBUG){
            //Debug，打印日志
            Log.e(title,mes);
        }
    }
    public static void w(String title,String mes){
        if(BuildConfig.LOG_DEBUG){
            //Debug，打印日志
            Log.w(title,mes);
        }
    }
}
