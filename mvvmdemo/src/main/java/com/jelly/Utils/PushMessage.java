package com.jelly.Utils;

/**
 * 推送消息
 * Created by Administrator on 2015/11/27.
 */

public class PushMessage implements PushMessageListener {
    private static PushMessage pushMessage;
    private PushMessageListener pushListener;
    private PushMessage(){}//私有化构造函数
    /**
     * 对外提供对象获取方法
     * @return
     */
    public static PushMessage getInstance(){
        if(pushMessage==null){
            pushMessage=new PushMessage();
        }
        return  pushMessage;
    }

    /**
     * 设置回调监听对象
     * @param pushListener
     */
    public void setPushListener(PushMessageListener pushListener){
        this.pushListener=pushListener;
    }
    /**
     * 推送消息回调
     * @param date
     */
    @Override
    public void pushMessage(String date) {
        pushListener.pushMessage(date);
    }
}
