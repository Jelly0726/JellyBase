package com.base.NotifyService;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;


/**
 * Created by Administrator on 2017/3/27.
 * 需要开启通知权限
 if (!NotifyManger.notificationListenerEnable()){
 ToastUtils.showShort(this, "通知权限未开启！");
 NotifyManger.gotoNotificationAccessSetting(MyApplication.getMyApp());
 }else {
 //重新触发通知绑定
 NotifyManger.toggleNotificationListenerService();
 }
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 当有新通知到来时会回调；
     * @param sbn
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        NotifyManger.onNotificationPosted(sbn);
    }

    /**
     * 当有通知移除时会回调；
     * @param sbn
     */
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //super.onNotificationRemoved(sbn);
    }
    /**
     * 当 NotificationListenerService 是可用的并且和通知管理器连接成功时回调。
     */
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.i("ss","NotificationService Connected");
    }

    /**
     * 取消通知
     * @param sbn
     */
    public void cancelNotification(StatusBarNotification sbn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cancelNotification(sbn.getKey());
        } else {
            cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
        }
    }
}
