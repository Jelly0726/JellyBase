package com.base.httpmvp.retrofitapi.token;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/7.
 */
public class TokenModel {
    @SerializedName("token")
    private String token;
    @SerializedName(value = "tokenExpirationTime",alternate = "time")
    private int validTime;//有效时间
    @SerializedName("createTime")
    private long createTime;//创建时间
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getValidTime() {
        return validTime;
    }

    public void setValidTime(int validTime) {
        this.validTime = validTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
