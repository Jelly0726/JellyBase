package com.jelly.jellybase.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.jelly.jellybase.R;


/**
 * Created by JELLY on 2017/9/26.
 */

public class PayTypePicker implements Parcelable {
    private int id;
    private String title="使用银联充值";
    private int payLogo= R.drawable.yinlian;
    public PayTypePicker(int id, String title,int payLogo){
        this.id=id;
        this.title=title;
        this.payLogo=payLogo;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        //重写该方法，作为选择器显示的名称
        return title;
    }

    public int getPayLogo() {
        return payLogo;
    }

    public void setPayLogo(int payLogo) {
        this.payLogo = payLogo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.payLogo);
    }

    protected PayTypePicker(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.payLogo = in.readInt();
    }

    public static final Creator<PayTypePicker> CREATOR = new Creator<PayTypePicker>() {
        @Override
        public PayTypePicker createFromParcel(Parcel source) {
            return new PayTypePicker(source);
        }

        @Override
        public PayTypePicker[] newArray(int size) {
            return new PayTypePicker[size];
        }
    };
}
