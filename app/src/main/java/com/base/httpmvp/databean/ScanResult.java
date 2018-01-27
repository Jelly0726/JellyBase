package com.base.httpmvp.databean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/22.
 */

public class ScanResult implements Serializable{
    @SerializedName("qrtype")
    private int type;//(1卖家发货,2买家收货,3买家代收货,4发展零售高级会员)
    @SerializedName("code")
    private String result;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
