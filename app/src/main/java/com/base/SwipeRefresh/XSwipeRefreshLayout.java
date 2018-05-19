package com.base.SwipeRefresh;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.zhy.autolayout.utils.AutoLayoutHelper;

/**
 * Created by Administrator on 2018/1/22.
 * 支持自动适配的SwipeRefreshLayout
 */

public class XSwipeRefreshLayout extends SwipeRefreshLayout {
    public XSwipeRefreshLayout(Context context) {
        super(context);
    }

    public XSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new ViewGroup.LayoutParams(getContext(), attrs);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (!isInEditMode())
        {
            mHelper.adjustChildren();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
