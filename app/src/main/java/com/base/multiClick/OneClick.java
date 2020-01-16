package com.base.multiClick;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/11/9.
 * 存储点击事件，存储的数量超出了可以自动删除之前的。
 */

public class OneClick {
    private String methodName;
    private static final int CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    public OneClick(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }
    /**
     *
     * @return false 正常点击  true 重复点击
     */
    public boolean check() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            return false;
        } else {
            return true;
        }
    }
}