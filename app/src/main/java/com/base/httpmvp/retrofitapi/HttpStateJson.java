package com.base.httpmvp.retrofitapi;


import com.google.gson.annotations.SerializedName;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpStateJson {
    @SerializedName("returnState")
    private boolean returnState;
    @SerializedName(value ="msg", alternate = "message")
    private String message;
    @SerializedName("data")
    private Object json;
    /**
     * API是否请求失败
     *
     * @return 成功返回true, 失败返回false
     */
    public boolean isReturnState() {
        return returnState;
    }

    public void setReturnState(boolean status) {
        this.returnState = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getJson() {
        return json;
    }

    public void setJson(Object json) {
        this.json = json;
    }
}
