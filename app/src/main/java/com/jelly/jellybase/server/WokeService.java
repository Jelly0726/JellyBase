package com.jelly.jellybase.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**执行工作服务
 * Created by Administrator on 2016/9/9.
 */
public class WokeService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//Handler将一个自定义的线程运行于主线程之上
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            public void run(){
                Toast.makeText(getApplicationContext(), "创建", Toast.LENGTH_LONG).show();
            }
        });
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
