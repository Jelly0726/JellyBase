package com.base.httpmvp.mode.databean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/8.
 * 注册参数
 */

public class RegisterParam implements Serializable{
    private String account;
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
