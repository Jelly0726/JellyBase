package com.base.httpmvp.retrofitapi.token;

import android.text.TextUtils;

import com.base.applicationUtil.AppPrefs;
import com.base.appManager.MyApplication;

/**
 * Created by Administrator on 2017/11/7.
 */
public class GlobalToken {
    private static TokenModel tokenModel;

    public static synchronized void updateToken(TokenModel token) {
        tokenModel = token;
        AppPrefs.putString(MyApplication.getMyApp(),"Token",tokenModel.getToken());
        AppPrefs.putLong(MyApplication.getMyApp(),"CreateTime",tokenModel.getCreateTime());
        AppPrefs.putInt(MyApplication.getMyApp(),"tokenExpirationTime",tokenModel.getTokenExpirationTime());
    }
    public static TokenModel getToken() {
        if(tokenModel==null){
            tokenModel=new TokenModel();
        }
        if (TextUtils.isEmpty(tokenModel.getToken())) {
            tokenModel.setToken(AppPrefs.getString(MyApplication.getMyApp(), "Token"));
            tokenModel.setCreateTime(AppPrefs.getLong(MyApplication.getMyApp(), "CreateTime", 0l));
            tokenModel.setTokenExpirationTime(AppPrefs.getInt(MyApplication.getMyApp(), "tokenExpirationTime", 0));
        }
        return tokenModel;
    }
    public static void removeToken(){
        tokenModel=null;
        AppPrefs.remove(MyApplication.getMyApp(),"Token");
        AppPrefs.remove(MyApplication.getMyApp(),"CreateTime");
        AppPrefs.remove(MyApplication.getMyApp(),"tokenExpirationTime");
    }
}
