package com.base.httpmvp.retrofitapi;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpStateList<T> extends HttpState{
    @SerializedName("data")
    private List<T> data=new ArrayList<>();

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
