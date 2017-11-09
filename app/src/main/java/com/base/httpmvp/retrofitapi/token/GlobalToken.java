package com.base.httpmvp.retrofitapi.token;

/**
 * Created by Administrator on 2017/11/7.
 */
public class GlobalToken {
    private static TokenModel tokenModel;

    public static synchronized void updateToken(TokenModel token) {
        tokenModel = token;
    }
    public static TokenModel getToken() {
        return tokenModel;
    }
}
