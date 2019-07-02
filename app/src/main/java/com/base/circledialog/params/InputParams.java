package com.base.circledialog.params;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.base.circledialog.res.values.CircleColor;
import com.base.circledialog.res.values.CircleDimen;

/**
 * 输入框参数
 * Created by hupei on 2017/3/31.
 */
public class InputParams implements Parcelable {
    private static final int[] MARGINS = {50, 20, 50, 40};
    private static final int[] PADDING = {50, 20, 50, 40};
    public static final int INPUT_TEXT=0;//输入文本
    public static final int INPUT_MONEY=1;//输入金额
    /**
     * 输入框与body视图的距离
     */
    public int[] margins = MARGINS;
    /**
     *  输入框内边距
     */
    public int[] paddings = PADDING;
    /**
     * 输入框的高度
     */
    public int inputHeight = CircleDimen.INPUT_HEIGHT;
    /**
     * 输入框提示语
     */
    public String hintText;
    /**
     * 输入框提示语颜色
     */
    public int hintTextColor = CircleColor.content;
    /**
     * 输入框背景资源文件
     */
    public int inputBackgroundResourceId;
    /**
     * 输入框边框线条粗细
     */
    public int strokeWidth = 1;
    /**
     * 输入框边框线条颜色
     */
    public int strokeColor = CircleColor.inputStroke;
    /**
     * 输入框的背景
     */
    public int inputBackgroundColor = Color.TRANSPARENT;
    /**
     * body视图的背景色
     */
    public int backgroundColor;
    /**
     * 输入框字体大小
     */
    public int textSize = CircleDimen.CONTENT_TEXT_SIZE;
    /**
     * 输入框字体颜色
     */
    public int textColor = CircleColor.title;
    /**
     * 输入框输入类型
     */
    public int type =INPUT_TEXT;
    /**
     * 输入框输入金额小数点的位数默认保留2位小数
     */
    public int digits =2;
    /**
     * 输入框默认文本
     */
    public String text="";

    public InputParams() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.margins);
        dest.writeIntArray(this.paddings);
        dest.writeInt(this.inputHeight);
        dest.writeString(this.hintText);
        dest.writeInt(this.hintTextColor);
        dest.writeInt(this.inputBackgroundResourceId);
        dest.writeInt(this.strokeWidth);
        dest.writeInt(this.strokeColor);
        dest.writeInt(this.inputBackgroundColor);
        dest.writeInt(this.backgroundColor);
        dest.writeInt(this.textSize);
        dest.writeInt(this.textColor);
        dest.writeInt(this.type);
        dest.writeInt(this.digits);
        dest.writeString(this.text);
    }

    protected InputParams(Parcel in) {
        this.margins = in.createIntArray();
        this.paddings = in.createIntArray();
        this.inputHeight = in.readInt();
        this.hintText = in.readString();
        this.hintTextColor = in.readInt();
        this.inputBackgroundResourceId = in.readInt();
        this.strokeWidth = in.readInt();
        this.strokeColor = in.readInt();
        this.inputBackgroundColor = in.readInt();
        this.backgroundColor = in.readInt();
        this.textSize = in.readInt();
        this.textColor = in.readInt();
        this.type = in.readInt();
        this.digits = in.readInt();
        this.text = in.readString();
    }

    public static final Creator<InputParams> CREATOR = new Creator<InputParams>() {
        @Override
        public InputParams createFromParcel(Parcel source) {
            return new InputParams(source);
        }

        @Override
        public InputParams[] newArray(int size) {
            return new InputParams[size];
        }
    };
}
