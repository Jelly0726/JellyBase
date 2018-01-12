package com.base.social;

import net.arvin.socialhelper.SocialHelper;

/**
 * Created by Administrator on 2018/1/12.
 */
public class SocialUtil {
    private static SocialUtil sInstance;

    private SocialHelper socialHelper;

    private SocialUtil() {
        socialHelper = new SocialHelper.Builder()
                .setQqAppId("qqAppId")
                .setWxAppId("wxAppId")
                .setWxAppSecret("wxAppSecret")
                .setWbAppId("wbAppId")
                .setWbRedirectUrl("wbRedirectUrl")
                .build();
    }

    public static SocialUtil getInstance() {
        if (sInstance == null) {
            synchronized (SocialUtil.class) {
                if (sInstance == null) {
                    sInstance = new SocialUtil();
                }
            }
        }
        return sInstance;
    }

    public SocialHelper socialHelper() {
        return socialHelper;
    }
}