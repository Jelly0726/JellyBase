package com.base.model;


import com.jelly.jellybase.R;

public class PayMothod {
    private int icon= R.drawable.ic_placeholder_figure;
    private String name="微信";
    private String mark="";
    private int payType=1;
    private boolean check=false;

    public int getIcon() {
        return icon;
    }

    public PayMothod setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    public String getName() {
        return name;
    }

    public PayMothod setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isCheck() {
        return check;
    }

    public PayMothod setCheck(boolean check) {
        this.check = check;
        return this;
    }

    public int getPayType() {
        return payType;
    }

    public PayMothod setPayType(int payType) {
        this.payType = payType;
        return this;
    }

    public String getMark() {
        return mark;
    }

    public PayMothod setMark(String mark) {
        this.mark = mark;
        return this;
    }
}
