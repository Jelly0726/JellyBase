package com.base.appservicelive.service;

import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.base.appservicelive.toolsUtil.CommonStaticUtil;


/**
 * Created by Administrator on 2017/3/27.
 */

public class NotificationService extends NotificationListenerService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CommonStaticUtil.startService(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        CommonStaticUtil.startService(this);
        //不能再这里更新UI，会报错，你可以试试，在子线程中不能更新UI
        //  Toast.makeText(NotificationService.this,"怎一个曹字了得！",Toast.LENGTH_SHORT).show();
       // super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        CommonStaticUtil.startService(this);
        //super.onNotificationRemoved(sbn);
    }

}