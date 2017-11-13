package com.base.httpmvp.retrofitapi.token;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/7.
 */
public class TokenModel {
    @SerializedName("token")
    private String token;
    @SerializedName("tokenExpirationTime")
    private int tokenExpirationTime;
    @SerializedName("createTime")
    private long createTime;
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(int tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
