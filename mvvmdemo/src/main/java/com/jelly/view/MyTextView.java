package com.jelly.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/4/16.
 */
public class MyTextView extends TextView {
    public MyTextView(Context context) {
        super(context);
    }
    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyTextView(Context context, AttributeSet attrs,
                      int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public boolean isFocused() {
        //return super.isFocused();
        return true;
    }
}
