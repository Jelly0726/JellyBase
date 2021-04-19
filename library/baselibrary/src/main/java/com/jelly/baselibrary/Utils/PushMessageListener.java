package com.jelly.baselibrary.Utils;

/**
 *  推送消息接口
 * Created by Administrator on 2015/11/27.
 */
public interface PushMessageListener {
    /**
     *  推送消息回调
     * @param date
     */
    public void pushMessage(String date);
}
