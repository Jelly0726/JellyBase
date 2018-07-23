package com.base.NotifyService;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.base.appManager.MyApplication;

import java.util.List;

/**
 * Created by cxk on 2017/2/4.
 * email:471497226@qq.com
 * <p>
 * 获取即时微信聊天记录服务类
 * 需要手动开启辅助功能权限，重新安装就需要手动开启
 */

public class WeChatLogService extends AccessibilityService {

    /**
     * 聊天对象
     */
    private String ChatName;
    /**
     * 聊天最新一条记录
     */
    private String ChatRecord = "test";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            //每次在聊天界面中有新消息到来时都出触发该事件
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                //获取当前聊天页面的根布局
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                //获取聊天信息
                getWeChatLog(rootNode);
                break;
            //通知消息时触发
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Notification notifications = (Notification) event.getParcelableData();
//              notifications.contentView;                                                // 通知的RemoteViews
//              notifications.contentIntent;                                              // 通知的PendingIntent
//              notifications.actions;                                                    // 通知的行为数组
                // Android4.4后还扩展了可以获取通知详情信息
                if (Build.VERSION.SDK_INT >Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Bundle extras = notifications.extras;
                    String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
                    int notificationIcon = extras.getInt(Notification.EXTRA_SMALL_ICON);
//                    Bitmap notificationLargeIcon = ((Bitmap)extras.getParcelable(Notification.EXTRA_LARGE_ICON));
//                    Drawable notificationLargeIcon = ((Drawable)extras.getParcelable(Notification.EXTRA_LARGE_ICON));
                    CharSequence notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);
                    CharSequence notificationSubText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
                    Log.i("SSSSS", "通知标题ss="+notificationTitle);
                    Log.i("SSSSS", "通知内容ss="+notificationText);
                    Log.i("SSSSS", "通知内容ss="+notificationSubText);
                }
                break;
        }

    }

    /**
     * 遍历
     *
     * @param rootNode
     */

    private void getWeChatLog(AccessibilityNodeInfo rootNode) {
        if (rootNode != null) {
            //获取所有聊天的线性布局
            List<AccessibilityNodeInfo> listChatRecord = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/o");
            if(listChatRecord.size()==0){
                return;
            }
            //获取最后一行聊天的线性布局（即是最新的那条消息）
            AccessibilityNodeInfo finalNode = listChatRecord.get(listChatRecord.size() - 1);
            //获取聊天对象list（其实只有size为1）
            List<AccessibilityNodeInfo> imageName = finalNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/i_");
            //获取聊天信息list（其实只有size为1）
            List<AccessibilityNodeInfo> record = finalNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ib");
            if (imageName.size() != 0) {
                if (record.size() == 0) {
                    //判断当前这条消息是不是和上一条一样，防止重复
                    if (!ChatRecord.equals("对方发的是图片或者表情")) {
                        //获取聊天对象
                        ChatName = imageName.get(0).getContentDescription().toString().replace("头像", "");
                        //获取聊天信息
                        ChatRecord = "对方发的是图片或者表情";

                        Log.e("AAAA", ChatName + "：" + "对方发的是图片或者表情");
                        Toast.makeText(MyApplication.getMyApp(), ChatName + "：" + ChatRecord, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //判断当前这条消息是不是和上一条一样，防止重复
                    if (!ChatRecord.equals(record.get(0).getText().toString())) {
                        //获取聊天对象
                        ChatName = imageName.get(0).getContentDescription().toString().replace("头像", "");
                        //获取聊天信息
                        ChatRecord = record.get(0).getText().toString();

                        Log.e("AAAA", ChatName + "：" + ChatRecord);
                        Toast.makeText(MyApplication.getMyApp(), ChatName + "：" + ChatRecord, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    /**
     * 必须重写的方法：系统要中断此service返回的响应时会调用。在整个生命周期会被调用多次。
     */
    @Override
    public void onInterrupt() {
        Toast.makeText(MyApplication.getMyApp(), "我快被终结了啊-----", Toast.LENGTH_SHORT).show();
    }

    /**
     * 服务开始连接
     */
    @Override
    protected void onServiceConnected() {
        Toast.makeText(MyApplication.getMyApp(), "服务已开启", Toast.LENGTH_SHORT).show();
        super.onServiceConnected();
    }

    /**
     * 服务断开
     *
     * @param intent 点击打开链接
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(MyApplication.getMyApp(), "服务已被关闭", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }
}