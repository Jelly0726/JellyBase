package com.base.redpacket.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.base.redpacket.Config;
import com.base.redpacket.job.AccessbilityJob;
import com.base.redpacket.job.WechatAccessbilityJob;
import com.base.redpacket.util.EventBusMsg;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RedPacketService extends AccessibilityService {

    private static String TAG = "RedPacketService";

    private static RedPacketService service;
    private static ArrayList<AccessbilityJob> mJobs = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "RedPacketService, onCreate");
        WechatAccessbilityJob wechatJob = new WechatAccessbilityJob();
        wechatJob.onCreateJob(this);
        mJobs.add(wechatJob);
    }

    public Config getConfig() {
        return Config.getConfig(this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        service = null;
        EventBusMsg msg = new EventBusMsg();
        msg.setType(EventBusMsg.ACCESSIBILITY_DISCONNECTED);
        EventBus.getDefault().post(msg);
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
        Toast.makeText(this, "中断抢红包服务", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        service = this;
        EventBusMsg msg = new EventBusMsg();
        msg.setType(EventBusMsg.ACCESSIBILITY_CONNECTED);
        EventBus.getDefault().post(msg);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String pkn = String.valueOf(event.getPackageName());
        for (AccessbilityJob job : mJobs) {
            if(pkn.equals(job.getTargetPackageName()) && job.isEnable()) {
                job.onReceiveJob(event);
                break;
            }
        }
    }

    /** 接收通知栏事件*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void handeNotificationPosted(String packageName, Notification notification) {
        if(TextUtils.isEmpty(packageName) || notification == null) {
            return;
        }
        if(service == null || mJobs.size() == 0) {
            return;
        }
        for (AccessbilityJob job : mJobs) {
            if(packageName.equals(job.getTargetPackageName()) && job.isEnable()) {
                job.onNotificationPosted(notification);
                break;
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isRunning() {
        if(service == null) {
            return false;
        }
        AccessibilityManager accessibilityManager = (AccessibilityManager) service.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityServiceInfo info = service.getServiceInfo();
        if(info == null) {
            return false;
        }
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        Iterator<AccessibilityServiceInfo> iterator = list.iterator();

        boolean isConnect = false;
        while (iterator.hasNext()) {
            AccessibilityServiceInfo i = iterator.next();
            if(i.getId().equals(info.getId())) {
                isConnect = true;
                break;
            }
        }
        if(!isConnect) {
            return false;
        }
        return true;
    }
    /**
     * 该辅助功能开关是否打开了
     * @param accessibilityServiceName：指定辅助服务名字
     * @param context：上下文
     * @return
     */
    private static boolean isAccessibilitySettingsOn(String accessibilityServiceName, Context context) {
        int accessibilityEnable = 0;
        String serviceName = context.getPackageName() + "/" +accessibilityServiceName;
        try {
            accessibilityEnable = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        } catch (Exception e) {
            Log.e("SS", "get accessibility enable failed, the err:" + e.getMessage());
        }
        if (accessibilityEnable == 1) {
            TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(serviceName)) {
                        Log.v("SS", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        }else {
            Log.d("SS","Accessibility service disable");
        }
        return false;
    }
    public static boolean isNotificationServiceRunning() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return false;
        }
        try {
            return NotificationService.isRunning();
        } catch (Throwable t) {}
        return false;
    }

}
