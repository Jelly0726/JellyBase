package com.base.redpacket.job;

import android.app.Notification;
import android.view.accessibility.AccessibilityEvent;

import com.base.redpacket.service.RedPacketService;


public interface AccessbilityJob {
    String getTargetPackageName();
    void onCreateJob(RedPacketService service);
    void onReceiveJob(AccessibilityEvent event);
    void onStopJob();
    void onNotificationPosted(Notification notification);
    boolean isEnable();
}
