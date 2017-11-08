package com.base.httpmvp.retrofitapi.token;

/**
 * Created by Administrator on 2017/11/7.
 */
public class GlobalToken {
    private static String sToken;

    public static synchronized void updateToken(String token) {
        sToken = token;
    }

    public static String getToken() {
        return sToken;
    }
}
