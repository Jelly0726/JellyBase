package com.jelly.baselibrary.social;

import android.os.Parcel;
import android.os.Parcelable;

public class ShareItem implements Parcelable {
    private int id;
    private int icon;
    private String name;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public ShareItem(int id,int icon,String name) {
        this.id=id;
        this.icon=icon;
        this.name=name;
    }

    public ShareItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.icon);
        dest.writeString(this.name);
    }

    protected ShareItem(Parcel in) {
        this.id = in.readInt();
        this.icon = in.readInt();
        this.name = in.readString();
    }

    public static final Creator<ShareItem> CREATOR = new Creator<ShareItem>() {
        @Override
        public ShareItem createFromParcel(Parcel source) {
            return new ShareItem(source);
        }

        @Override
        public ShareItem[] newArray(int size) {
            return new ShareItem[size];
        }
    };
}
