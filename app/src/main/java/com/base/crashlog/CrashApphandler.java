package com.base.crashlog;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**奔溃日志
 * Created by Administrator on 2016/12/21.
 */
public class CrashApphandler extends CrashAppLog{
    public static CrashApphandler mCrashApphandler = null;


    private CrashApphandler(){};
    public static CrashApphandler getInstance() {

        if (mCrashApphandler == null)
            mCrashApphandler = new CrashApphandler();

        return mCrashApphandler;

    }

    @Override
    public void initParams(CrashAppLog crashAppLog) {
        //动态的改变缓存目录和缓存文件数量
        if (crashAppLog != null){

            crashAppLog.setCAHCE_CRASH_LOG(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"crashLog");
            crashAppLog.setLIMIT_LOG_COUNT(5);
        }
    }

    @Override
    public void sendCrashLogToServer(File folder, File file) {
        //发送服务端
        Log.e("*********", "文件夹:"+folder.getAbsolutePath()+" - "+file.getAbsolutePath()+"");
    }
}
