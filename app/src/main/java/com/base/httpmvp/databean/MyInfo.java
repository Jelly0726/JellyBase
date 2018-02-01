package com.base.httpmvp.databean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/15.
 */

public class MyInfo implements Serializable{
    private String mobile;//手机
    private String alias;//昵称
    @SerializedName(value = "user_headimg")
    private String userheadimg;//头像
    private String IP;//
    private int discount;//优惠
    private int task;//积分任务
    private int collect;//收藏
    private int footprint;//足迹
    @SerializedName(value = "user_money")
    private float usermoney;//余额
    @SerializedName(value = "user_integral")
    private float userintegral;//积分

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserheadimg() {
        return userheadimg;
    }

    public void setUserheadimg(String userheadimg) {
        this.userheadimg = userheadimg;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getFootprint() {
        return footprint;
    }

    public void setFootprint(int footprint) {
        this.footprint = footprint;
    }

    public float getUsermoney() {
        return usermoney;
    }

    public void setUsermoney(float usermoney) {
        this.usermoney = usermoney;
    }

    public float getUserintegral() {
        return userintegral;
    }

    public void setUserintegral(float userintegral) {
        this.userintegral = userintegral;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }
}
