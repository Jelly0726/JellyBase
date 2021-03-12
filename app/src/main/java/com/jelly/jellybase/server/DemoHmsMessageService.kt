package com.jelly.jellybase.server

import android.os.Bundle
import com.base.log.DebugLog
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
            DebugLog.i("Received message entity is null!")
            return;
        }
        DebugLog.i("RemoteMessage=${message}")
    }

    /**
     * 服务端更新token
     */
    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        DebugLog.i("onNewToken=${p0}")
    }
}