package com.base.recyclerViewUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

/**
 * Created by YH on 2017/10/10.
 * <p>
 * <p>
 * //优化RecyclerView嵌套卡顿
 * holder.mRecyclerView.setHasFixedSize(true);
 * holder.mRecyclerView.setNestedScrollingEnabled(false);
 * holder.mRecyclerView.setItemViewCacheSize(600);
 * RecyclerView.RecycledViewPool recycledViewPool = new
 * RecyclerView.RecycledViewPool();
 * holder.mRecyclerView.setRecycledViewPool(recycledViewPool);
 */

public class RecyclerScrollView extends ScrollView {
    private int slop;
    private int touch;
    private static long lastEdgesTime = 0l;//上一次达到边界的时间
    private OnScrollListenter onScrollListenter;

    public RecyclerScrollView(Context context) {
        super(context);
        setSlop(context);
    }

    public RecyclerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSlop(context);
    }

    public RecyclerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSlop(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = (View) getChildAt(getChildCount() - 1);
        int a = view.getBottom();//
        //获取View的高度和 竖直屏幕上竖直移动的距离
        int b = getHeight() + getScrollY();
        a = a - b;

        if (a == 0) {
            //触底事件
            //上一次达到边界的时间间隔大于500毫秒算一次达到边界
            if (System.currentTimeMillis() - lastEdgesTime > 500) {
                if (onScrollListenter != null)
                    onScrollListenter.onBottom();
            }
            lastEdgesTime = System.currentTimeMillis();
        }
        if (getScrollY() == 0) {
            //触顶事件
            //上一次达到边界的时间间隔大于500毫秒算一次达到边界
            if (System.currentTimeMillis() - lastEdgesTime > 500) {
                if (onScrollListenter != null)
                    onScrollListenter.onTop();
            }
            lastEdgesTime = System.currentTimeMillis();
        } else
            super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * 是否intercept当前的触摸事件
     *
     * @param ev 触摸事件
     * @return true：调用onMotionEvent()方法，并完成滑动操作
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //  保存当前touch的纵坐标值
                touch = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //  滑动距离大于slop值时，返回true
                if (Math.abs((int) ev.getRawY() - touch) > slop) return true;
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 获取相应context的touch slop值（即在用户滑动之前，能够滑动的以像素为单位的距离）
     *
     * @param context ScrollView对应的context
     */
    private void setSlop(Context context) {
        slop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public OnScrollListenter getOnScrollListenter() {
        return onScrollListenter;
    }

    public void setOnScrollListenter(OnScrollListenter onScrollListenter) {
        this.onScrollListenter = onScrollListenter;
    }

    /**
     * 监听是否触底与触顶
     */
    public interface OnScrollListenter {
        /**
         * 触顶了
         */
        void onTop();

        /**
         * 触底了
         */
        void onBottom();
    }
}