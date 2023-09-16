package com.jelly.iconfontview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
public class IconFontView extends AppCompatTextView {
    public IconFontView(Context context) {
        super(context);
        Init(context);
    }
    public IconFontView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }
    public IconFontView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }
    public void Init(Context context){
        Typeface icon = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
        this.setTypeface(icon);
    }

    /**
     *  设置字体图标ttf文件名称
     * @param ttfName
     */
    public void setTypeface(String ttfName){
        Typeface icon = Typeface.createFromAsset(getContext().getAssets(), ttfName);
        super.setTypeface(icon);
    }
}