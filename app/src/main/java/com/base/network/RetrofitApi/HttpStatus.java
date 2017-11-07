package com.base.network.RetrofitApi;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/7.
 */

//HttpStatus.java
public class HttpStatus {
    @SerializedName(value = "status", alternate = "code")
    private int mCode;
    @SerializedName(value = "message", alternate = "msg")
    private String mMessage;

    public int getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }

    /**
     * API是否请求失败
     *
     * @return 失败返回true, 成功返回false
     */
    public boolean isCodeInvalid() {
        return mCode != HttpCode.SUCCEED;
    }
}