package com.jelly.jellybase.datamodel;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/27.
 */

public class AccountDetails implements Serializable{
    private int type;
    private String title;
    private String time;
    private double amount;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
