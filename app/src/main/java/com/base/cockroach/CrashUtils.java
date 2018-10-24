package com.base.cockroach;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.base.sendEmail.SendMailUtil;

import java.io.ObjectStreamException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashUtils {
    /**
     * 但是jdk 1.5 以后java 编译器允许乱序执行 。所以执行顺序可能是1-3-2 或者 1-2-3.如果是前者先执行3 的话
     * 切换到其他线程，instance 此时 已经是非空了，此线程就会直接取走instance ，直接使用，这样就回出错。DCL 失效。
     * 解决方法 SUN 官方已经给我们了。将instance 定义成 private volatile static Singleton instance =null: 即可
     */
    private CrashUtils() {}
    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder{
        private static final CrashUtils instance = new CrashUtils();
    }
    /**
     * 单一实例
     */
    public static CrashUtils getInstance() {
        return SingletonHolder.instance;
    }
    /**
     * 要杜绝单例对象在反序列化时重新生成对象，那么必须加入如下方法：
     * @return
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return SingletonHolder.instance;
    }
    public void saveErrorInfo(Context mContext,Throwable throwable) {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(getAppInfo(mContext));
            stringBuffer.append("崩溃时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
            stringBuffer.append("手机系统：").append(Build.VERSION.RELEASE).append("\n");
            stringBuffer.append("android版本号API：" + Build.VERSION.SDK_INT).append("\n");
            stringBuffer.append("手机制造商:" + Build.MANUFACTURER).append("\n");
            stringBuffer.append("手机型号：").append(Build.MODEL).append("\n");

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            Throwable cause = throwable.getCause();
            while(cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.flush();
            printWriter.close();
            String result = writer.toString();
            stringBuffer.append("崩溃信息：").append(result);
            //发送邮箱
            SendMailUtil.send(stringBuffer.toString());
        }catch (Exception e){

        }
    }
    /**
     * 获取应用程序信息
     */
    private String getAppInfo(Context context) {
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
