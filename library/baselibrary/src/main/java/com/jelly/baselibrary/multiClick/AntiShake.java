package com.jelly.baselibrary.multiClick;

/**
 * Created by Administrator on 2017/11/9.
 * 防重复点击
 */

public class AntiShake {

    private static LimitQueue<OneClick> queue = new LimitQueue<>(10);
    /**
     *
     * @return false 正常点击  true 重复点击
     */
    public static boolean check(Object o) {
        String flag;
        if(o == null) {
            flag = Thread.currentThread().getStackTrace()[2].getMethodName();
        } else {
            flag = o.toString();
        }
        for (OneClick util : queue.getArrayList()) {
            if (util.getMethodName().equals(flag)) {
                return util.check();
            }
        }
        OneClick clickUtil = new OneClick(flag);
        queue.offer(clickUtil);
        return clickUtil.check();
    }
}