package com.base.SwipeRefresh;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

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
}
