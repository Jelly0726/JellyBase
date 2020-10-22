package com.base.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/8.
 */

public class PersonalInfo implements Serializable{
    private String birthday;
    @SerializedName(value = "reg_time")//注册时间
    private String createTime;
    private boolean sex;
    @SerializedName(value = "alias")
    private String nickname;
    @SerializedName(value = "user_idcard")
    private String idcard;
    @SerializedName(value = "user_headimg")
    private String photo;
    @SerializedName(value = "last_login")
    private String lastLogin;//上次登录时间
    @SerializedName(value = "last_ip")
    private String lastIp;//上次登录ip
    @SerializedName(value = "user_real_name")
    private String name;
    private String IP;
    private String mobile;
    @SerializedName(value = "user_email")
    private String email;
    @SerializedName(value = "is_validated_mobile")
    private boolean isValidatedMobile =false;//手机是否验证 BIT
    @SerializedName(value = "user_rank")
    private int userRank;//用户等级

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean getSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public int getUserRank() {
        return userRank;
    }

    public void setUserRank(int userRank) {
        this.userRank = userRank;
    }

    public boolean isValidatedMobile() {
        return isValidatedMobile;
    }

    public void setValidatedMobile(boolean validatedMobile) {
        this.isValidatedMobile = validatedMobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
