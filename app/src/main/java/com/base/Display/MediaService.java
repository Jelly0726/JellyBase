package com.base.Display;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.base.BaseApplication;
import com.jelly.baselibrary.eventBus.NetEvent;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class MediaService extends Service {
    private Observer observer;
    private static int position=-1;
    @Override
    public void onCreate() {
        super.onCreate();
        observer=new Observer<NetEvent>() {
            @Override
            public void onChanged(@Nullable NetEvent netEvent) {
                if (!DisplayUtils.getInstance().isMultiScreen()){
                    stopSelf();
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(BaseApplication.getInstance())){
                        return;
                    }
                }
                switch (netEvent.getArg()){
                    case 0://纯视频
                        if (position!=0) {
                            DisplayUtils.getInstance().showVedio(getApplicationContext());
                        }
                        position=0;
                        break;
                    case 1://收银
                        if (position!=1) {
                            DisplayUtils.getInstance().show(getApplicationContext());
                        }
                        position=1;
                        break;
                    case 2://二维码
//                        if (position!=2)
                        DisplayUtils.getInstance().showQR(getApplicationContext(),netEvent.getMsg()
                                ,netEvent.getArg0());
                        position=2;
                        break;
                }
            }
        };
        LiveEventBus.get("MediaService", NetEvent.class)
                .observeForever(observer);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        DisplayUtils.getInstance().dismiss();
        if (observer!=null)
            LiveEventBus.get("MediaService").removeObserver(observer);
    }
}