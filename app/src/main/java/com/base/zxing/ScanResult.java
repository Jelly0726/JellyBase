package com.base.zxing;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;

public class ScanResult implements Parcelable {
    private String result;
    private BarcodeFormat type;//EAN_13 条形码  QR_CODE二维码
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

    public ScanResult() {

    }

    public BarcodeFormat getType() {
        return type;
    }

    public ScanResult setType(BarcodeFormat type) {
        this.type = type;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.result);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    protected ScanResult(Parcel in) {
        this.result = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : BarcodeFormat.values()[tmpType];
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
