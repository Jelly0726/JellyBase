package com.base.zxing;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/9/22.
 * 生成二维码的参数
 */

public class CreateQRParams implements Parcelable {
    /**
     *生成二维码的宽
     */
    public int QR_WIDTH=0;
    /**
     *生成二维码的高
     */
    public int QR_HEIGHT=0;
    /**
     * 生成二维码的内容
     */
    public String str;
    /**
     * 生成二维码的中心图标Bitmap
     */
    public Bitmap conterBitmap;
    /**
     * 生成二维码的下方水印图标Bitmap
     */
    public Bitmap watermarkBitmap;
    /**
     * 生成二维码的下方水印文字
     */
    public String watermarkText;
    /**
     * 生成二维码的边框宽度
     */
    public int margin=4;

    public CreateQRParams() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.QR_WIDTH);
        dest.writeInt(this.QR_HEIGHT);
        dest.writeString(this.str);
        dest.writeParcelable(this.conterBitmap, flags);
        dest.writeParcelable(this.watermarkBitmap, flags);
        dest.writeString(this.watermarkText);
        dest.writeInt(this.margin);
    }

    protected CreateQRParams(Parcel in) {
        this.QR_WIDTH = in.readInt();
        this.QR_HEIGHT = in.readInt();
        this.str = in.readString();
        this.conterBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.watermarkBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.watermarkText = in.readString();
        this.margin = in.readInt();
    }

    public static final Creator<CreateQRParams> CREATOR = new Creator<CreateQRParams>() {
        @Override
        public CreateQRParams createFromParcel(Parcel source) {
            return new CreateQRParams(source);
        }

        @Override
        public CreateQRParams[] newArray(int size) {
            return new CreateQRParams[size];
        }
    };
}
