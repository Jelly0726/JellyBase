package com.base.recyclerViewUtil;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.base.Utils.ColorUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

/**
 * 项目名称：RefreshAndLoad
 * 类描述：对适配器的item布局的装饰
 * 作者：峰哥
 * 创建时间：2016/11/30 16:54
 * 邮箱：470794349@qq.com
 * 修改简介：
 */
public class ItemDecoration extends RecyclerView.ItemDecoration{

    private Rect outRect;
    private int span;
    public static final int NONE=-1;//都没有
    public static final int HEAD=0;//表示第一个item是head
    public static final int FOOT=1;//表示最后一个item是foot
    public static final int ALL_HAVE=2;//表示都有
    private int type=-1;//-1 表示都没有 0 表示第一个item是head 1表示最后一个item是foot 2表示都有
    private Paint mPaint;
    /**
     * @param outRect 代表item距离左右以及上下的距离
     * @param span  代表item的列数
     * @param type 代表是否有head、foot、间距类型
     */
    public ItemDecoration(Rect outRect, int span, int type,@ColorInt int color) {
        this.outRect = outRect;
        this.span = span;
        this.type=type;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        String col= ColorUtils.toHexColor(color);
        if (ColorUtils.isColor(col))
            mPaint.setColor(Color.parseColor(col));
        else
            mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setStyle(Paint.Style.FILL);
    }
    /**
     * 可以通过outRect.set(l,t,r,b)设置指定itemview的paddingLeft，paddingTop， paddingRight， paddingBottom
     * @param outRect   全为0的rect，用来指定偏移区域
     * @param view      指RecyclerView中的Item
     * @param parent    指RecyclerView本身
     * @param state     状态
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0,0,0);
        int hCounnt=1;
        int fCounnt=1;
        if (parent instanceof SwipeMenuRecyclerView) {
            hCounnt=((SwipeMenuRecyclerView)parent).getHeaderItemCount();
            fCounnt=((SwipeMenuRecyclerView)parent).getFooterItemCount();
        }
        if ( parent.getChildAdapterPosition(view)<hCounnt||parent.getChildLayoutPosition(view)
                >= parent.getAdapter().getItemCount()-fCounnt) {
            if(type==NONE){
                parentLayoutManager(outRect,view,parent,state);
            }else if(type==ALL_HAVE){
                outRect.set(0, 0,0,0);
            }else if(type == HEAD || type == FOOT){
                if (type == HEAD && parent.getChildAdapterPosition(view) <hCounnt) {
                    outRect.set(0, 0,0,0);
                }else if (type == FOOT && parent.getChildAdapterPosition(view)
                        >= parent.getAdapter().getItemCount()-fCounnt) {
                    outRect.set(0, 0,0,0);
                }else {
                    parentLayoutManager(outRect,view,parent,state);
                }
            }
        }else {
            parentLayoutManager(outRect,view,parent,state);
        }
    }
    private void parentLayoutManager(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager
                    .LayoutParams)view.getLayoutParams();
            int spanIndex = lp.getSpanIndex();
            //判断当前的位置，如果是最后设置右边距
            if (spanIndex == span-1) {
                outRect.set(this.outRect.left,0, this.outRect.right,0);
            } else {//其余的位置右边距为0
                outRect.set(this.outRect.left, 0, 0,0);
            }
        } else if (parent.getLayoutManager() instanceof GridLayoutManager){
            final int position = parent.getChildAdapterPosition(view);
            /**
             * 表格格局分割线
             * <p>
             *      1：当是第一个Item的时候，四周全部需要分割线
             *      2：当是第一行Item的时候，需要额外添加顶部的分割线
             *      3：当是第一列Item的时候，需要额外添加左侧的分割线
             *      4：默认情况全部添加底部和右侧的分割线
             * </p>
             */
            RecyclerView.LayoutManager mLayoutManager = parent.getLayoutManager();
            if (mLayoutManager instanceof GridLayoutManager) {
                GridLayoutManager mGridLayoutManager = (GridLayoutManager) mLayoutManager;
                int mSpanCount = mGridLayoutManager.getSpanCount();
                if (position == 0) {//1
                    outRect.set(this.outRect.left, this.outRect.top, this.outRect.right, this.outRect.bottom);
                } else if ((position + 1) <= mSpanCount) {//2
                    outRect.set(0, this.outRect.top, this.outRect.right, this.outRect.bottom);
                } else if (((position + mSpanCount) % mSpanCount) == 0) {//3
                    outRect.set(this.outRect.left, 0, this.outRect.right, this.outRect.bottom);
                } else {//4
                    outRect.set(0, 0, this.outRect.right, this.outRect.bottom);
                }
            }
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager){
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            final int childPosition = parent.getChildAdapterPosition(view);
            int orientation=layoutManager.getOrientation();
            int childCount = parent.getAdapter().getItemCount();
            if (orientation== LinearLayoutManager.VERTICAL) {
                if (childPosition+1==childCount){
                    outRect.set(this.outRect.left, this.outRect.top, this.outRect.right, this.outRect.bottom);
                }else {
                    outRect.set(this.outRect.left, this.outRect.top, this.outRect.right,0);
                }
            }else {
                if (childPosition+1==childCount){
                    outRect.set(this.outRect.left, this.outRect.top, this.outRect.right, this.outRect.bottom);
                }else {
                    outRect.set(this.outRect.left, this.outRect.top,0,this.outRect.bottom);
                }
            }
        }
    }

    /**
     * 获取对应的方向
     * @param layoutManager
     * @return
     */
    private int getOrientation(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager)layoutManager).getOrientation();
        }else if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager)layoutManager).getOrientation();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager)layoutManager).getOrientation();
        }
        return RecyclerView.VERTICAL;
    }

    /**
     * 获取对应的列数
     * @param layoutManager
     * @return
     */
    private int getSpanCount(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager)layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager)layoutManager).getSpanCount();
        }
        return 1;
    }

    /**
     * 判断是否是第一行
     * @param orientation 方向
     * @param position    对应的位置
     * @param columnCount 总列数
     * @param childCount  总网格数
     * @return
     */
    private boolean isFirstRaw(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            return position < columnCount;
        } else {
            if (columnCount == 1) return true;
            return position % columnCount == 0;
        }
    }
    /**
     * 判断是否是最后一行
     * @param orientation 方向
     * @param position    对应的位置
     * @param columnCount 总列数
     * @param childCount  总网格数
     * @return
     */
    private boolean isLastRaw(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            if (columnCount == 1) {
                return position + 1 == childCount;
            } else {
                int lastRawItemCount = childCount % columnCount;
                int rawCount = (childCount - lastRawItemCount) / columnCount + (lastRawItemCount > 0 ? 1 : 0);

                int rawPositionJudge = (position + 1) % columnCount;
                if (rawPositionJudge == 0) {
                    int positionRaw = (position + 1) / columnCount;
                    return rawCount == positionRaw;
                } else {
                    int rawPosition = (position + 1 - rawPositionJudge) / columnCount + 1;
                    return rawCount == rawPosition;
                }
            }
        } else {
            if (columnCount == 1) return true;
            return (position + 1) % columnCount == 0;
        }
    }
    /**
     * 判断是否是第一列
     * @param orientation 方向
     * @param position    对应的位置
     * @param columnCount 总列数
     * @param childCount  总网格数
     * @return
     */
    private boolean isFirstColumn(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            if (columnCount == 1) return true;
            return position % columnCount == 0;
        } else {
            return position < columnCount;
        }
    }
    /**
     * 判断是否是最后一列
     * @param orientation 方向
     * @param position    对应的位置
     * @param columnCount 总列数
     * @param childCount  总网格数
     * @return
     */
    private boolean isLastColumn(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            if (columnCount == 1) return true;
            return ((position + 1) % columnCount == 0)
                    ||(position + 1 == childCount);
        } else {
            if (columnCount == 1) {
                return position + 1 == childCount;
            } else {
                int lastRawItemCount = childCount % columnCount;
                int rawCount = (childCount - lastRawItemCount) / columnCount + (lastRawItemCount > 0 ? 1 : 0);

                int rawPositionJudge = (position + 1) % columnCount;
                if (rawPositionJudge == 0) {
                    int positionRaw = (position + 1) / columnCount;
                    return rawCount == positionRaw;
                } else {
                    int rawPosition = (position + 1 - rawPositionJudge) / columnCount + 1;
                    return rawCount == rawPosition;
                }
            }
        }
    }
    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        assert layoutManager != null;
        int orientation = getOrientation(layoutManager);
        int spanCount = getSpanCount(layoutManager);
        int childCount = layoutManager.getChildCount();

        if (layoutManager instanceof GridLayoutManager) {
            canvas.save();
            //表格格局分割线
            drawGrid(canvas, parent);
            canvas.restore();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            canvas.save();
            //表格格局分割线
            drawGrid(canvas, parent);
            canvas.restore();
        }else if (layoutManager instanceof LinearLayoutManager) {
            canvas.save();
            if (orientation == RecyclerView.VERTICAL) {
                //纵向布局分割线
                drawVertical(canvas, parent);
            } else {
                //横向布局分割线
                drawHorizontal(canvas, parent);
            }
            canvas.restore();
        }
    }

    /**
     * 绘制横向列表分割线
     *
     * @param c      绘制容器
     * @param parent RecyclerView
     */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int mChildCount = parent.getChildCount();
        for (int i = 0; i < mChildCount; i++) {
            View mChild = parent.getChildAt(i);
            drawLeft(c, mChild, parent);
        }
    }

    /**
     * 绘制纵向列表分割线
     *
     * @param c      绘制容器
     * @param parent RecyclerView
     */
    private void drawVertical(Canvas c, RecyclerView parent) {
        int mChildCount = parent.getChildCount();
        for (int i = 0; i < mChildCount; i++) {
            View mChild = parent.getChildAt(i);
            drawTop(c, mChild, parent);
        }
    }

    /**
     * 绘制表格类型分割线
     *
     * @param c      绘制容器
     * @param parent RecyclerView
     */
    private void drawGrid(Canvas c, RecyclerView parent) {
        int mChildCount = parent.getChildCount();
        for (int i = 0; i < mChildCount; i++) {
            View mChild = parent.getChildAt(i);
            RecyclerView.LayoutManager mLayoutManager = parent.getLayoutManager();
            if (mLayoutManager instanceof GridLayoutManager) {
                GridLayoutManager mGridLayoutManager = (GridLayoutManager) mLayoutManager;
                int mSpanCount = mGridLayoutManager.getSpanCount();
                if (i == 0) {
                    drawTop(c, mChild, parent);
                    drawLeft(c, mChild, parent);
                }else
                if ((i + 1) <= mSpanCount) {
                    drawTop(c, mChild, parent);
                }else
                if (((i + mSpanCount) % mSpanCount) == 0) {
                    drawLeft(c, mChild, parent);
                }
                drawRight(c, mChild, parent);
                drawBottom(c, mChild, parent);
            }
        }
    }

    /**
     * 绘制右边分割线
     *
     * @param c            绘制容器
     * @param mChild       对应ItemView
     * @param recyclerView RecyclerView
     */
    private void drawLeft(Canvas c, View mChild, RecyclerView recyclerView) {
        RecyclerView.LayoutParams mChildLayoutParams = (RecyclerView.LayoutParams) mChild.getLayoutParams();
        int left = mChild.getLeft() - outRect.left - mChildLayoutParams.leftMargin;
        int top = mChild.getTop() - mChildLayoutParams.topMargin;
        int right = mChild.getLeft() - mChildLayoutParams.leftMargin;
        int bottom;
        if (isGridLayoutManager(recyclerView)) {
            bottom = mChild.getBottom() + mChildLayoutParams.bottomMargin + outRect.bottom;
        } else {
            bottom = mChild.getBottom() + mChildLayoutParams.bottomMargin;
        }
        c.drawRect(left, top, right, bottom, mPaint);
    }

    /**
     * 绘制顶部分割线
     *
     * @param c            绘制容器
     * @param mChild       对应ItemView
     * @param recyclerView RecyclerView
     */
    private void drawTop(Canvas c, View mChild, RecyclerView recyclerView) {
        RecyclerView.LayoutParams mChildLayoutParams = (RecyclerView.LayoutParams) mChild.getLayoutParams();
        int left;
        int top = mChild.getTop() - mChildLayoutParams.topMargin - outRect.top;
        int right = mChild.getRight() + mChildLayoutParams.rightMargin;
        int bottom = mChild.getTop() - mChildLayoutParams.topMargin;
        if (isGridLayoutManager(recyclerView)) {
            left = mChild.getLeft() - mChildLayoutParams.leftMargin - outRect.left;
        } else {
            left = mChild.getLeft() - mChildLayoutParams.leftMargin;
        }
        c.drawRect(left, top, right, bottom, mPaint);
    }

    /**
     * 绘制右边分割线
     *
     * @param c            绘制容器
     * @param mChild       对应ItemView
     * @param recyclerView RecyclerView
     */
    private void drawRight(Canvas c, View mChild, RecyclerView recyclerView) {
        RecyclerView.LayoutParams mChildLayoutParams = (RecyclerView.LayoutParams) mChild.getLayoutParams();
        int left = mChild.getRight() + mChildLayoutParams.rightMargin;
        int top= mChild.getTop() - mChildLayoutParams.topMargin;
        int right = left + outRect.right;
        int bottom = mChild.getBottom() + mChildLayoutParams.bottomMargin;
        if (isGridLayoutManager(recyclerView)) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            assert layoutManager != null;
            int orientation = getOrientation(layoutManager);
            int spanCount = getSpanCount(layoutManager);
            int childCount = layoutManager.getChildCount();
            int position = recyclerView.getChildLayoutPosition(mChild);
            boolean firstRaw = isFirstRaw(orientation, position, spanCount, childCount);
            boolean lastRaw = isLastRaw(orientation, position, spanCount, childCount);
            boolean firstColumn = isFirstColumn(orientation, position, spanCount, childCount);
            boolean lastColumn = isLastColumn(orientation, position, spanCount, childCount);
            if (firstRaw&&lastColumn)
                top = mChild.getTop() - mChildLayoutParams.topMargin - outRect.top;
        }
        c.drawRect(left, top, right, bottom, mPaint);
    }

    /**
     * 绘制底部分割线
     *
     * @param c            绘制容器
     * @param mChild       对应ItemView
     * @param recyclerView RecyclerView
     */
    private void drawBottom(Canvas c, View mChild, RecyclerView recyclerView) {
        RecyclerView.LayoutParams mChildLayoutParams = (RecyclerView.LayoutParams) mChild.getLayoutParams();
        int left = mChild.getLeft() - mChildLayoutParams.leftMargin;
        int top = mChild.getBottom() + mChildLayoutParams.bottomMargin;
        int bottom = top + outRect.bottom;
        int right;
        if (isGridLayoutManager(recyclerView)) {
            right = mChild.getRight() + mChildLayoutParams.rightMargin + outRect.right;
        } else {
            right = mChild.getRight() + mChildLayoutParams.rightMargin;
        }
        c.drawRect(left, top, right, bottom, mPaint);
    }

    /**
     * 判断RecyclerView所加载LayoutManager是否为GridLayoutManager
     *
     * @param recyclerView RecyclerView
     * @return 是GridLayoutManager返回true，否则返回false
     */
    private boolean isGridLayoutManager(RecyclerView recyclerView) {
        RecyclerView.LayoutManager mLayoutManager = recyclerView.getLayoutManager();
        return (mLayoutManager instanceof GridLayoutManager);
    }
}
