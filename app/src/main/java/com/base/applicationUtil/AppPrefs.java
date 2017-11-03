package com.base.applicationUtil;

import android.content.Context;

import net.grandcentrix.tray.TrayPreferences;


/**
 * Created by Administrator on 2017/1/24.
 */

public class AppPrefs {
    private static final int VERSION = 1;
    private static volatile TrayEMMPrefs  mPrefs;
    //private static final Object PrefsSyncObject = new Object();

    /**
     * 继承TrayPreferences以修改模块名
     */
    private static class TrayEMMPrefs extends TrayPreferences {

        public TrayEMMPrefs(Context context) {
            super(context, context.getPackageName(), VERSION);
        }
    }

    private AppPrefs(Context context) {
    }

    private static TrayEMMPrefs getPrefs(Context context) {
        if (mPrefs == null) {
            synchronized(AppPrefs.class) {
                if (mPrefs == null) {
                    mPrefs = new TrayEMMPrefs(context);
                }
            }
        }

        return mPrefs;
    }

    /**
     * 设置可被多个进程共享的Boolean值
     */
    public static void putBoolean(Context context, String key, boolean value) {
        TrayEMMPrefs prefs = getPrefs(context);
        prefs.put(key,value);
    }

    /**
     * 设置可被多个进程共享的Int值
     */
    public static void putInt(Context context, String key, int value) {
        TrayEMMPrefs prefs = getPrefs(context);
        prefs.put(key,value);
    }

    /**
     * 设置可被多个进程共享的Long值
     */
    public static void putLong(Context context, String key, long value) {
        TrayEMMPrefs prefs = getPrefs(context);
        prefs.put(key,value);
    }

    /**
     * 设置可被多个进程共享的String值
     */
    public static void putString(Context context, String key, String value) {
        TrayEMMPrefs prefs = getPrefs(context);
        prefs.put(key,value);
    }

    /**
     * 获取可被多个进程共享的Boolean值,缺省值为false
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /**
     * 获取可被多个进程共享的Boolean值,若key不存在,则返回defaultValue
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        TrayEMMPrefs prefs = getPrefs(context);
        return prefs.getBoolean(key, defaultValue);
    }

    /**
     * 获取可被多个进程共享的Int值,若key不存在,则返回0
     */
    public static int getInt(Context context, String key) {
        return getInt(context, key, 0);
    }

    /**
     * 获取可被多个进程共享的Int值,若key不存在,则返回defaultValue
     */
    public static int getInt(Context context, String key, int defaultValue) {
        TrayEMMPrefs prefs = getPrefs(context);
        return prefs.getInt(key, defaultValue);
    }

    /**
     * 获取可被多个进程共享的Long值,若key不存在,则返回0
     */
    public static long getLong(Context context, String key) {
        return getLong(context, key, 0);
    }

    /**
     * 获取可被多个进程共享的Long值,若key不存在,则返回defaultValue
     */
    public static long getLong(Context context, String key, long defaultValue) {
        TrayEMMPrefs prefs = getPrefs(context);
        return prefs.getLong(key, defaultValue);
    }

    /**
     * 获取可被多个进程共享的Int值,若key不存在,则返回null
     */
    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    /**
     * 获取可被多个进程共享的Int值,若key不存在,则返回defaultValue
     */
    public static String getString(Context context, String key, String defaultValue) {
        TrayEMMPrefs prefs = getPrefs(context);
        return prefs.getString(key, defaultValue);
    }

    public static void remove(Context context, String key) {
        TrayEMMPrefs prefs = getPrefs(context);
        if (key != null) {
            prefs.remove(key);
        }
    }

    /**
     * 清除配置文件
     */
    public static void clear(Context context) {
        TrayEMMPrefs prefs = getPrefs(context);
        prefs.clear();
    }
}
