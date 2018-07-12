package com.base.SwipeRefresh.stick;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ItemDecoration for Stick Header.
 * <p>
 * 使用时只需要将你的Adapter实现AdapterStick接口即可
 * 也就是主要实现这个方法isPinnedViewType()返回需要固定的Type
 * 如果存在多个Header情况请实现getHeaderCount()这个方法并返回Header数量总长度
 *
 * @author wubo
 */
public class StickItemDecoration extends RecyclerView.ItemDecoration {

    private final static String TAG = StickItemDecoration.class.getSimpleName();

    /**
     * 当前绘制的pinnedheaderview
     */
    View mPinnedHeaderView = null;

    /**
     * pinnedheaderview的位置
     */
    int mHeaderPosition = -1;

    /**
     * 装载所有的viewtype
     */
    Map<Integer, Boolean> mPinnedViewTypes = new HashMap<Integer, Boolean>();

    /**
     * pinnedheaderview的上边距
     */
    private int mPinnedHeaderTop;

    /**
     * pinnedheaderview的裁剪区域
     */
    private Rect mClipBounds;
    private Builder mBuilder;
    private int mFirstVisiblePosition;

    private Drawable mDivider;
    private int mDividerWidth;
    private int mDividerHeight;
    private List<Integer> mViewTypeList = new ArrayList<>();
    private StickItemDecoration(Builder builder) {
        this(builder,Color.argb(0,153, 153, 153));
        //mBuilder = builder;
    }
    private StickItemDecoration(Builder builder,@ColorInt int color) {
        this(builder,color, 2, 2, -1);
        //mBuilder = builder;
    }
    private StickItemDecoration(Builder builder,@ColorInt int color, int dividerWidth, int dividerHeight, int... excludeViewType) {
        //super(color,dividerWidth,dividerHeight,excludeViewType);
        mDivider = new ColorDrawable(color);
        mDividerWidth = dividerWidth;
        mDividerHeight = dividerHeight;
        for (int i : excludeViewType) {
            mViewTypeList.add(i);
        }
        mBuilder = builder;
    }

    /**
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //super.onDraw(c,parent,state);
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            drawHorizontal(c, parent);
        } else if (layoutManager instanceof GridLayoutManager) {
            drawHorizontal(c, parent);
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
            drawVertical(c, parent);
        }

        createPinnedHeader(parent);

        if (mPinnedHeaderView != null) {
            // check overlap section view.
            //TODO support only vertical header currently.
            final int headerEndAt = mPinnedHeaderView.getTop() + mPinnedHeaderView.getHeight() + 1;
            final View v = parent.findChildViewUnder(c.getWidth() / 2, headerEndAt);
            if (isPinnedView(parent, v)) {
                mPinnedHeaderTop = v.getTop() - mPinnedHeaderView.getHeight();
            } else {
                mPinnedHeaderTop = 0;
            }

            if (isHeaderView(mFirstVisiblePosition)) {
                return;
            }
            mClipBounds = c.getClipBounds();
            mClipBounds.top = mPinnedHeaderTop + mPinnedHeaderView.getHeight();

            c.clipRect(mClipBounds);
        }
    }
    public void drawHorizontal(Canvas c, RecyclerView parent) {
        c.save();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);
            if (childPosition < 0) continue;
            if (mViewTypeList.contains(parent.getAdapter().getItemViewType(childPosition))) continue;
            if (child instanceof SwipeMenuRecyclerView.LoadMoreView) continue;
            final int left = child.getLeft();
            final int top = child.getBottom();
            final int right = child.getRight();
            final int bottom = top + mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
        c.restore();
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        c.save();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);
            if (childPosition < 0) continue;
            if (mViewTypeList.contains(parent.getAdapter().getItemViewType(childPosition))) continue;
            if (child instanceof SwipeMenuRecyclerView.LoadMoreView) continue;
            final int left =  child.getRight();
            final int top = child.getTop();
            final int right = left + mDividerWidth;
            final int bottom = child.getBottom();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
        c.restore();
    }

    /**
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mPinnedHeaderView != null && !isHeaderView(mFirstVisiblePosition)) {
            c.save();
            mClipBounds.top = 0;

            c.clipRect(mClipBounds, Region.Op.UNION);
            c.translate(0, mPinnedHeaderTop);
            mPinnedHeaderView.draw(c);

            c.restore();
        }
    }

    private void createPinnedHeader(RecyclerView parent) {
        checkCache(parent);

        // get LinearLayoutManager.
        //final LinearLayoutManager linearLayoutManager = getLayoutManager(parent);
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            mFirstVisiblePosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            mFirstVisiblePosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else {
            return;
        }

        // mFirstVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition();

        final int headerPosition = findPinnedHeaderPosition(mFirstVisiblePosition);

        if (isHeaderView(mFirstVisiblePosition)) {
            return;
        }

        if (headerPosition >= 0 && mHeaderPosition != headerPosition) {
            mHeaderPosition = headerPosition;
            final int viewType = mBuilder.mStickProvider.getItemViewType(headerPosition);

            final RecyclerView.ViewHolder pinnedViewHolder = mBuilder.mStickProvider.createViewHolder(parent, viewType);
            mBuilder.mStickProvider.bindViewHolder(pinnedViewHolder, headerPosition);
            mPinnedHeaderView = pinnedViewHolder.itemView;

            // read layout parameters
            ViewGroup.LayoutParams layoutParams = mPinnedHeaderView.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mPinnedHeaderView
                        .setLayoutParams(layoutParams);
            }


            int heightMode = View.MeasureSpec.getMode(layoutParams.height);
            int heightSize = View.MeasureSpec.getSize(layoutParams.height);

            if (heightMode == View.MeasureSpec.UNSPECIFIED) {
                heightMode = View.MeasureSpec.EXACTLY;
            }

            final int maxHeight = parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom();
            if (heightSize > maxHeight) {
                heightSize = maxHeight;
            }

            // measure & layout
            final int ws = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.EXACTLY);
            final int hs = View.MeasureSpec.makeMeasureSpec(heightSize, heightMode);
            mPinnedHeaderView.measure(ws, hs);

            mPinnedHeaderView.layout(0, 0, mPinnedHeaderView.getMeasuredWidth(), mPinnedHeaderView.getMeasuredHeight());
        }
    }


    /**
     * return the first visible view position is headerview
     *
     * @param firstVisiblePosition first visible view position
     */
    private boolean isHeaderView(int firstVisiblePosition) {
        final int position = firstVisiblePosition - mBuilder.mStickProvider.getHeaderCount();
        if (position < 0) {
            return true;
        }
        return false;
    }


    private int findPinnedHeaderPosition(int fromPosition) {
        if (fromPosition > mBuilder.mStickProvider.getItemCount()) {
            return -1;
        }

        for (int position = fromPosition; position >= 0; position--) {
            final int viewType = mBuilder.mStickProvider.getItemViewType(position);
            if (isPinnedViewType(viewType)) {
                return position;
            }
        }

        return -1;
    }

    private boolean isPinnedViewType(int viewType) {
        if (!mPinnedViewTypes.containsKey(viewType)) {
            mPinnedViewTypes.put(viewType, mBuilder.mStickProvider.isPinnedViewType(viewType));
        }
        return mPinnedViewTypes.get(viewType);
    }

    private boolean isPinnedView(RecyclerView parent, View v) {
        final int position = parent.getChildAdapterPosition(v) /*- mBuilder.mStickProvider.getHeaderCount()*/;
        if (position == RecyclerView.NO_POSITION) {
            return false;
        }
        final int viewType = mBuilder.mStickProvider.getItemViewType(position);

        return isPinnedViewType(viewType);
    }

    private void checkCache(RecyclerView parent) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (mBuilder.mStickProvider != adapter) {
            disableCache();
        }
    }


    private void disableCache() {
        mPinnedHeaderView = null;
        mHeaderPosition = -1;
        mPinnedViewTypes.clear();
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {

        private StickAdapter mStickProvider;
        private StickItemDecoration mStickItemDecoration;


        public Builder adapterProvider(StickAdapter stickProvider) {
            mStickProvider = stickProvider;
            return this;
        }
        public StickItemDecoration build() {
            if (mStickItemDecoration == null) {
                mStickItemDecoration = new StickItemDecoration(this);
            }
            return mStickItemDecoration;
        }
        public StickItemDecoration build(@ColorInt int color) {
            if (mStickItemDecoration == null) {
                mStickItemDecoration = new StickItemDecoration(this,color);
            }
            return mStickItemDecoration;
        }
        public StickItemDecoration build(@ColorInt int color, int dividerWidth, int dividerHeight, int... excludeViewType) {
            if (mStickItemDecoration == null) {
                mStickItemDecoration = new StickItemDecoration(this,color,dividerWidth,dividerHeight,excludeViewType);
            }
            return mStickItemDecoration;
        }
    }

}