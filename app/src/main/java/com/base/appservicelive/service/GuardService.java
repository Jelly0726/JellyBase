package com.base.appservicelive.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import xiaofei.library.hermeseventbus.HermesEventBus;

/**后台不死守护服务
 * Created by Administrator on 2016/9/6.
 */
public class GuardService extends Service {
    private BroadcastReceiver mBR;
    private IntentFilter mIF;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                Intent a = new Intent(GuardService.this, LiveService.class);
                startService(a);
            }
        };
        mIF = new IntentFilter();
        mIF.addAction("LiveService");
        registerReceiver(mBR, mIF);
        //Log.i("msg","GuardService创建");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.i("msg","GuardService启动");
        return Service.START_STICKY;
    }
    @Override
    public void onDestroy() {

        Intent intent = new Intent();
        intent.setAction("GuardService");
        sendBroadcast(intent);

        unregisterReceiver(mBR);
        //Log.i("msg","GuardService关闭");
        super.onDestroy();
        HermesEventBus.getDefault().destroy();
    }
}
