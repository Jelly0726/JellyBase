package com.base.webview;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * web工具类
 * Created by Administrator on 2015/12/26.
 */
public class WebTools implements Parcelable {
    private static final String URL = "http://www.baidu.com/";                  //地址
    private static final String TITLE = "0";                                    //台头
    public String url;
    public String title;
    private int from = 0;

    public WebTools() {
        url = URL;
        title = TITLE;
    }

    public WebTools(String url_0, String title_0) {
        url = url_0;
        title = title_0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.title);
    }

    protected WebTools(Parcel in) {
        this.url = in.readString();
        this.title = in.readString();
    }

    public static final Creator<WebTools> CREATOR = new Creator<WebTools>() {
        @Override
        public WebTools createFromParcel(Parcel source) {
            return new WebTools(source);
        }

        @Override
        public WebTools[] newArray(int size) {
            return new WebTools[size];
        }
    };

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }
}
