package com.jelly.jellybase.datamodel;


import com.jelly.jellybase.R;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/27.
 */

public class BankCardInfo implements Serializable{
    private String bankName;
    private String bankNo;
    private int bankDraw= R.drawable.yinlian;
    private String bankLogo;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public int getBankDraw() {
        return bankDraw;
    }

    public void setBankDraw(int bankDraw) {
        this.bankDraw = bankDraw;
    }

    public String getBankLogo() {
        return bankLogo;
    }

    public void setBankLogo(String bankLogo) {
        this.bankLogo = bankLogo;
    }

}
