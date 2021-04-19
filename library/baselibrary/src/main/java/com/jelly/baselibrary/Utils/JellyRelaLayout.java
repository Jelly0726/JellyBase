package com.jelly.baselibrary.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class JellyRelaLayout extends RelativeLayout {
    public JellyRelaLayout(Context context) {
        super(context);
    }

    public JellyRelaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JellyRelaLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
private boolean flag=true;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (flag) {
            //获取宽高缩放比
            float x = 0;
            float y = 0;
            //获取所有子view个数
            int count = this.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = this.getChildAt(i);
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                layoutParams.width = (int) (layoutParams.width * x);
                layoutParams.height = (int) (layoutParams.height * y);
                layoutParams.leftMargin = (int) (layoutParams.leftMargin * x);
                layoutParams.rightMargin = (int) (layoutParams.rightMargin * x);
                layoutParams.topMargin = (int) (layoutParams.topMargin * y);
                layoutParams.bottomMargin = (int) (layoutParams.bottomMargin * y);

            }
        }
        flag=false;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
