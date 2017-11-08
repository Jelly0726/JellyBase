package com.base.httpmvp.retrofitapi;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/7.
 */

public class HttpStatus {

    @SerializedName("returnState")
    private boolean returnState;
    @SerializedName("message")
    private String mMessage;

    public String getMessage() {
        return mMessage;
    }
    /**
     * API是否请求失败
     *
     * @return 成功返回true, 失败返回false
     */
    public boolean isReturnState() {
        return returnState;
    }
}