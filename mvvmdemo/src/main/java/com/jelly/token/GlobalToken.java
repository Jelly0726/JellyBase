package com.jelly.token;

import android.text.TextUtils;

import com.jelly.AppInit;
import com.jelly.applicationUtil.AppPrefs;


/**
 * Created by Administrator on 2017/11/7.
 */
public class GlobalToken {
    private static TokenModel tokenModel;

    public static synchronized void updateToken(TokenModel token) {
        if (AppInit.context==null){new Throwable("更新异常！请先初始化");return;}
        tokenModel = token;
        AppPrefs.putString(AppInit.context, "Token", tokenModel.getToken());
        AppPrefs.putLong(AppInit.context, "CreateTime", tokenModel.getCreateTime());
        AppPrefs.putInt(AppInit.context, "tokenExpirationTime", tokenModel.getTokenExpirationTime());
    }

    public static TokenModel getToken() {
        if (AppInit.context==null){new Throwable("获取异常！请先初始化");return null;}
        if (tokenModel == null) {
            tokenModel = new TokenModel();
        }
        if (TextUtils.isEmpty(tokenModel.getToken())) {
            tokenModel.setToken(AppPrefs.getString(AppInit.context, "Token"));
            tokenModel.setCreateTime(AppPrefs.getLong(AppInit.context, "CreateTime", 0l));
            tokenModel.setTokenExpirationTime(AppPrefs.getInt(AppInit.context, "tokenExpirationTime", 0));
        }
        return tokenModel;
    }

    public static void removeToken() {
        if (AppInit.context==null){new Throwable("更新异常！请先初始化");return;}
        tokenModel = null;
        AppPrefs.remove(AppInit.context, "Token");
        AppPrefs.remove(AppInit.context, "CreateTime");
        AppPrefs.remove(AppInit.context, "tokenExpirationTime");
    }
}
