package com.jelly.baselibrary.applicationUtil;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/***
 * 工具类获取其他语言资源
 * Android多语言的适配以及APP内语言切换的实现
 */
public class ChangeLanguageHelper {
    private static Context mContext = null;
    private static String country = null;

    public static final int CHANGE_LANGUAGE_CHINA = 1;
    public static final int CHANGE_LANGUAGE_ENGLISH = 2;
    public static final int CHANGE_LANGUAGE_DEFAULT = 0;
    private static String mLanguage = "";

    private static Resources mResources;
    private static Locale mDefaultLocale;

    public static void init(Context context) {
        mResources = context.getResources();
        country = context.getResources().getConfiguration().locale.getCountry();

        mContext = context;
//        int appLanguage = Configure.getAppLanguage();
        int appLanguage = AppPrefs.getInt(mContext,"AppLanguage",0);
        changeLanguage(appLanguage);
    }

    /**
     * 获取当前字符串资源的内容
     *
     * @param id
     * @return
     */
    public static String getStringById(int id) {
        String string ;
        if (mLanguage != null && !"".equals(mLanguage)){
            string = mResources.getString(id,mLanguage);
        }else {
            string = mResources.getString(id,"");
        }

        if (string != null && string.length() > 0) {
            return string;
        }
        return "";
    }

    public static void changeLanguage(int language) {

        Configuration config = mResources.getConfiguration();     // 获得设置对象
        DisplayMetrics dm = mResources.getDisplayMetrics();
        switch (language) {
            case CHANGE_LANGUAGE_CHINA:
                config.locale = Locale.SIMPLIFIED_CHINESE;     // 中文
                config.setLayoutDirection(Locale.SIMPLIFIED_CHINESE);
                mLanguage = "zh-CN";
                country = "CN";
//                Configure.setAppLanguage(CHANGE_LANGUAGE_CHINA);
                AppPrefs.putInt(mContext,"AppLanguage",CHANGE_LANGUAGE_CHINA);
                break;
            case CHANGE_LANGUAGE_ENGLISH:
                config.locale = Locale.ENGLISH;   // 英文
                config.setLayoutDirection(Locale.ENGLISH);
                mLanguage = "en";
                country = "US";
//                Configure.setAppLanguage(CHANGE_LANGUAGE_ENGLISH);
                AppPrefs.putInt(mContext,"AppLanguage",CHANGE_LANGUAGE_ENGLISH);
                break;
            case CHANGE_LANGUAGE_DEFAULT:

                country = Locale.getDefault().getCountry();

                if ("CN".equals(country)){
                    mDefaultLocale = Locale.SIMPLIFIED_CHINESE;
                }else {
                    mDefaultLocale =  Locale.ENGLISH;
                }

                config.locale = mDefaultLocale;         // 系统默认语言
                config.setLayoutDirection(mDefaultLocale);
//                Configure.setAppLanguage(CHANGE_LANGUAGE_DEFAULT);
                AppPrefs.putInt(mContext,"AppLanguage",CHANGE_LANGUAGE_DEFAULT);
                break;
        }
        mResources.updateConfiguration(config, dm);
        //EventBus.getDefault().post(new AppEvent.ChangeLanguage());

    }

    public static boolean getDefaultLanguage() {
        return ("CN".equals(country));
    }

}