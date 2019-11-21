package com.base.httpmvp.retrofitapi.methods;


import com.google.gson.annotations.SerializedName;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpStateData<T> extends HttpState {
    @SerializedName("data")
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
