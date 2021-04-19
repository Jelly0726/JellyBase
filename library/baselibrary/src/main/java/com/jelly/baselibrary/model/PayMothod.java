package com.jelly.baselibrary.model;


import com.jelly.baselibrary.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;

public class PayMothod implements Serializable {
    private int icon= R.drawable.ic_placeholder_figure;
    private String name="微信";//支付方式
    private int payType=1;//支付方式
    private String mark="";//说明
    private boolean check=false;//是否选中
    private boolean isEnable=true;//是否可用
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

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
    //深度复制 需要实现 Serializable
    public Object deepclone()  {
        try {
            //将对象写到流里
            ByteArrayOutputStream bo=new ByteArrayOutputStream();
            ObjectOutputStream oo=new ObjectOutputStream(bo);
            oo.writeObject(this);//从流里读出来
            ByteArrayInputStream bi=new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi=new ObjectInputStream(bi);
            return(oi.readObject());
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
