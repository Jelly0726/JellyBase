package com.base.zxing;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class ScanResult implements Parcelable {
    private String result;

    public String getResult() {
        return result;
    }

    public ScanResult setResult(String result) {
        this.result = result;
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.result);
    }
    public ScanResult() {

    }
    protected ScanResult(Parcel in) {
        this.result = in.readString();
    }

    public static final Creator<ScanResult> CREATOR = new Creator<ScanResult>() {
        @Override
        public ScanResult createFromParcel(Parcel source) {
            return new ScanResult(source);
        }

        @Override
        public ScanResult[] newArray(int size) {
            return new ScanResult[size];
        }
    };
}
