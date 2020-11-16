package com.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.jelly.jellybase.R;


/**
 * ScrollView限制最大高度
 * ScrollView 限制子布局最大高度。
 * 子布局未达到最大高度时，自适应高度且不能滚动
 * 子布局达到最大高度时，可滚动。
 */
public class MaxHeightScrollView extends ScrollView {
    private int maxHeight;
    public MaxHeightScrollView(Context context) {
        this(context, null);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView);
        maxHeight = typedArray.getDimensionPixelSize(R.styleable.MaxHeightScrollView_max_height,
                200);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST));
    }
    private int dip2px(int dipVal)
    {
        float scale = getResources().getDisplayMetrics().density;
        return (int)(dipVal * scale + 0.5f);
    }
}