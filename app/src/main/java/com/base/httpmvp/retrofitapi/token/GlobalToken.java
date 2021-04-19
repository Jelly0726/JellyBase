package com.base.httpmvp.retrofitapi.token;

import android.text.TextUtils;

import com.base.BaseApplication;
import com.jelly.baselibrary.applicationUtil.AppPrefs;

/**
 * Created by Administrator on 2017/11/7.
 */
public class GlobalToken {
    private static TokenModel tokenModel;

    public static synchronized void updateToken(TokenModel token) {
        tokenModel = token;
        AppPrefs.putString(BaseApplication.getInstance(),"Token",tokenModel.getToken());
        AppPrefs.putLong(BaseApplication.getInstance(),"CreateTime",tokenModel.getCreateTime());
        AppPrefs.putInt(BaseApplication.getInstance(),"tokenExpirationTime",tokenModel.getTokenExpirationTime());
    }
    public static TokenModel getToken() {
        if(tokenModel==null){
            tokenModel=new TokenModel();
        }
        if (TextUtils.isEmpty(tokenModel.getToken())) {
            tokenModel.setToken(AppPrefs.getString(BaseApplication.getInstance(), "Token"));
            tokenModel.setCreateTime(AppPrefs.getLong(BaseApplication.getInstance(), "CreateTime", 0l));
            tokenModel.setTokenExpirationTime(AppPrefs.getInt(BaseApplication.getInstance(), "tokenExpirationTime", 0));
        }
        return tokenModel;
    }
    public static void removeToken(){
        tokenModel=null;
        AppPrefs.remove(BaseApplication.getInstance(),"Token");
        AppPrefs.remove(BaseApplication.getInstance(),"CreateTime");
        AppPrefs.remove(BaseApplication.getInstance(),"tokenExpirationTime");
    }
}
