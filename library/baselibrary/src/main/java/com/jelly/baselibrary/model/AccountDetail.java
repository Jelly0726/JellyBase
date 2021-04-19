package com.jelly.baselibrary.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/9.
 */

public class AccountDetail implements Serializable{
    private double amount;
    private String addtime;
    private String status;
    private String type;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
