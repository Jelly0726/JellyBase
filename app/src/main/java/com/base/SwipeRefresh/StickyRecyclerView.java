package com.base.SwipeRefresh;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

/**
 * 分组悬浮的RecyclerView
 */
public class StickyRecyclerView extends SwipeMenuRecyclerView {
    public StickyRecyclerView(Context context) {
        super(context);
    }

    public StickyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public interface OnPinnedHeaderClickListener {

        void onPinnedHeaderClick(int adapterPosition);
    }

    private OnPinnedHeaderClickListener mPinnedHeaderClickListener;

    public void setOnPinnedHeaderClickListener(OnPinnedHeaderClickListener listener) {
        mPinnedHeaderClickListener = listener;
    }

    private boolean mPinnedHeaderHandle;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (mPinnedHeaderClickListener == null) {
            return super.onInterceptTouchEvent(e);
        }
        IStickyDecoration pinnedHeaderInterface = getPinnedHeaderDecoration();
        if (pinnedHeaderInterface == null) {
            return super.onInterceptTouchEvent(e);
        }
        Rect pinnedHeaderRect = pinnedHeaderInterface.getPinnedHeaderRect();
        int pinnedHeaderPosition = pinnedHeaderInterface.getPinnedHeaderPosition();
        if (pinnedHeaderRect == null || pinnedHeaderPosition == -1) {
            return super.onInterceptTouchEvent(e);
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (pinnedHeaderRect.contains((int) e.getX(), (int) e.getY())) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(e);
    }


    /**
     * 如果有固定的header的情况
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mPinnedHeaderClickListener == null) {
            return super.onTouchEvent(ev);
        }
        IStickyDecoration pinnedHeaderInterface = getPinnedHeaderDecoration();
        if (pinnedHeaderInterface == null) {
            return super.onTouchEvent(ev);
        }
        Rect pinnedHeaderRect = pinnedHeaderInterface.getPinnedHeaderRect();
        int pinnedHeaderPosition = pinnedHeaderInterface.getPinnedHeaderPosition();
        if (pinnedHeaderRect == null || pinnedHeaderPosition == -1) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPinnedHeaderHandle = false;
                if (pinnedHeaderRect.contains((int) ev.getX(), (int) ev.getY())) {
                    mPinnedHeaderHandle = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mPinnedHeaderHandle) {
                    if (!pinnedHeaderRect.contains((int) ev.getX(), (int) ev.getY())) {
                        MotionEvent cancel = MotionEvent.obtain(ev);
                        cancel.setAction(MotionEvent.ACTION_CANCEL);
                        super.dispatchTouchEvent(cancel);

                        MotionEvent down = MotionEvent.obtain(ev);
                        down.setAction(MotionEvent.ACTION_DOWN);
                        return super.dispatchTouchEvent(down);
                    } else {
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                float x = ev.getX();
                float y = ev.getY();
                // 如果 HeaderView 是可见的 , 点击在 HeaderView 内 , 那么触发pinned header 点击
                if (mPinnedHeaderHandle && pinnedHeaderRect.contains((int) x, (int) y)) {
                    mPinnedHeaderClickListener.onPinnedHeaderClick(pinnedHeaderPosition);
                    mPinnedHeaderHandle = false;
                    return true;
                }
                mPinnedHeaderHandle = false;
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    public IStickyDecoration getPinnedHeaderDecoration() {
        int decorationIndex = 0;
        ItemDecoration itemDecoration;
        do {
            itemDecoration = getItemDecorationAt(decorationIndex);
            if (itemDecoration instanceof IStickyDecoration) {
                return (IStickyDecoration) itemDecoration;
            }
            decorationIndex++;
        } while (itemDecoration != null);
        return null;
    }
}
