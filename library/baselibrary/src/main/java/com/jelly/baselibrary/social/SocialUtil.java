package com.jelly.baselibrary.social;

import com.jelly.baselibrary.config.BaseConfig;

import net.arvin.socialhelper.SocialHelper;

/**
 * Created by Administrator on 2018/1/12.
 */
public class SocialUtil {
    private static SocialUtil sInstance;

    private SocialHelper socialHelper;

    private SocialUtil() {
        socialHelper = new SocialHelper.Builder()
                .setQqAppId(BaseConfig.QQ_APP_ID)
                .setWxAppId(BaseConfig.WechatPay_APP_ID)
                .setWxAppSecret(BaseConfig.WechatPay_APP_SECRET)
                .setWbAppId(BaseConfig.WB_APP_ID)
                .setWbRedirectUrl(BaseConfig.WB_REDIRECT_URL)
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
        if (socialHelper==null){
            synchronized (SocialUtil.class) {
                if (socialHelper == null) {
                    socialHelper = new SocialHelper.Builder()
                            .setWxAppId(BaseConfig.WechatPay_APP_ID)
                            .setWxAppSecret(BaseConfig.WechatPay_APP_SECRET)
                            .build();
                }
            }
        }
        return socialHelper;
    }
}