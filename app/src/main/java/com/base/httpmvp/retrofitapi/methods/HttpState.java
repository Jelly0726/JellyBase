package com.base.httpmvp.retrofitapi.methods;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/7.
 */

public class HttpState {

    @SerializedName("returnState")
    private boolean returnState;
    @SerializedName("errcode")
    private int errcode=-1;
    @SerializedName("errmsg")
    private String errmsg;
    @SerializedName(value = "message",alternate = "msg")
    private String message;

    public String getMessage() {
        if (TextUtils.isEmpty(message)){
            return errmsg;
        }
        return message;
    }
    /**
     * API是否请求失败
     *
     * @return 成功返回true, 失败返回false
     */
    public boolean isReturnState() {
        if (!returnState){
            if (errcode==0){
                return true;
            }
        }
        return returnState;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        if (TextUtils.isEmpty(errmsg)){
            return message;
        }
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}