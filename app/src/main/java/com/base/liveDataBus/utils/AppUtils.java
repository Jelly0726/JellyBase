package com.base.liveDataBus.utils;

import android.annotation.SuppressLint;
import android.app.Application;

public final class AppUtils {

    @SuppressLint("StaticFieldLeak")
    private static volatile Application sApplication;

    public static Application getApplicationContext() {
        if (sApplication == null) {
            synchronized (AppUtils.class) {
                if (sApplication == null) {
                    try {
                        sApplication = (Application) Class.forName("android.app.ActivityThread")
                                .getMethod("currentApplication")
                                .invoke(null, (Object[]) null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sApplication;
    }
}