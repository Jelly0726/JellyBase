package com.base.httpmvp.retrofitapi.methods;


import com.google.gson.annotations.SerializedName;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpStateJson extends HttpState{
    @SerializedName("data")
    private String json;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
