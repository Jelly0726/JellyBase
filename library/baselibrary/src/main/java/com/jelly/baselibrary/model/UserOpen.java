package com.jelly.baselibrary.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/18.
 */

public class UserOpen implements Serializable{
    private String mobile;//手机
    @SerializedName(value = "user_mediaUID")
    private String wechatUID;//微信openid
    @SerializedName(value = "user_mediaID")
    private String qqUID;//QQ openid

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWechatUID() {
        return wechatUID;
    }

    public void setWechatUID(String wechatUID) {
        this.wechatUID = wechatUID;
    }

    public String getQqUID() {
        return qqUID;
    }

    public void setQqUID(String qqUID) {
        this.qqUID = qqUID;
    }
}
