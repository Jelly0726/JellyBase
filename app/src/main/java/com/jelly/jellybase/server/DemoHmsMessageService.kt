package com.jelly.jellybase.server

import com.jelly.baselibrary.log.LogUtils
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage

/**
 * 接手华为推送透传消息
 */
public class DemoHmsMessageService: HmsMessageService() {
    /**
     * 接收透传消息
     */
    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        // 判断消息是否为空
        if (message == null) {
            LogUtils.i("Received message entity is null!")
            return;
        }
        LogUtils.i("RemoteMessage=${message}")
    }

    /**
     * 服务端更新token
     */
    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        LogUtils.i("onNewToken=${p0}")
    }
}