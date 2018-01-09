package com.base.bankcard;

import com.google.gson.annotations.SerializedName;
import com.jelly.jellybase.R;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/27.
 */

public class BankCardInfo implements Serializable {
    @SerializedName(value = "bankType",alternate = "type")
    private String type;
    @SerializedName("bankname")
    private String bankName;
    @SerializedName("bankno")
    private String bankNo;
    @SerializedName("isdefault")
    private boolean isdefault;
    private int bankDraw= R.drawable.yinlian;
    private String bankLogo;
    private int bankid;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(boolean isdefault) {
        this.isdefault = isdefault;
    }

    public int getBankid() {
        return bankid;
    }

    public void setBankid(int bankid) {
        this.bankid = bankid;
    }
}
