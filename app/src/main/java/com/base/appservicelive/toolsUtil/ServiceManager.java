package com.base.appservicelive.toolsUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.base.appservicelive.service.GuardService;
import com.base.appservicelive.service.LiveService;


/**
 * Created by Administrator on 2016/9/6.
 */
public class ServiceManager {
    private Context context;
    public ServiceManager(Context context){
        this.context=context;
    }
    public void setNotificationIcon(int id){

    }

    /**
     * 停止服务
     */
    public void stopService(){
        Intent a = new Intent(context, LiveService.class);
        context.stopService(a);
        Intent b = new Intent(context, GuardService.class);
        context.stopService(b);
    }

    /**
     * 开启服务
     */
    public void startService(){
        Intent a = new Intent(context, LiveService.class);
        // 当 API > 18 时，使用 context.startForegroundService()启动前台服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(a);
        }else {
            context.startService(a);
        }
        Intent b = new Intent(context, GuardService.class);
        context.startService(b);
    }
}
