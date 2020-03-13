package com.base.recyclerViewUtil;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.base.Utils.ColorUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;


/**
 * 万能分隔线
 * StaggeredGridLayoutManager布局使用注意
 * //定义瀑布流管理器，第一个参数是列数，第二个是方向。
 * final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
 *         layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);//不设置的话，图片闪烁错位，有可能有整列错位的情况。
 *         mRecyclerView.setLayoutManager(layoutManager);//设置瀑布流管理器
 *         mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(40));//边距和分割线，需要自己定义
 *         mRecyclerView.setAdapter(new MyAdapter(this));//设置适配器
 *         mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
 *             @Override
 *             public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
 *                 super.onScrollStateChanged(recyclerView, newState);
 *                 layoutManager.invalidateSpanAssignments();//这行主要解决了当加载更多数据时，底部需要重绘，否则布局可能衔接不上。
 *             }
 *         });
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
    public ItemDecoration(Rect outRect, int span, int type, @ColorInt int color) {
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
        int hCounnt=0;
        int fCounnt=0;
        if (parent instanceof SwipeMenuRecyclerView) {
            hCounnt=((SwipeMenuRecyclerView)parent).getHeaderItemCount();
            fCounnt=((SwipeMenuRecyclerView)parent).getFooterItemCount();
        }
        if (hCounnt>0&&fCounnt>0){
            type=ALL_HAVE;
        }else if (hCounnt>0){
            type = HEAD;
        }else if (fCounnt>0 &&(view instanceof SwipeMenuRecyclerView.LoadMoreView)){
            type = FOOT;
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
            /**
             *
             */
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            final GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            final int childPosition = parent.getChildAdapterPosition(view);
            final int spanCount = layoutManager.getSpanCount();
            if (layoutManager.getOrientation() == GridLayoutManager.VERTICAL) {
                //判断是否在第一排
                if (layoutManager.getSpanSizeLookup().getSpanGroupIndex(childPosition, spanCount) == 0) {//第一排的需要上面
                    outRect.top = this.outRect.top;
                }
                outRect.bottom = this.outRect.bottom;
                //这里忽略和合并项的问题，只考虑占满和单一的问题
                if (lp.getSpanSize() == spanCount) {//占满
                    outRect.left = this.outRect.left;
                    outRect.right = this.outRect.right;
                } else {
                    outRect.left = (int) (((float) (spanCount - lp.getSpanIndex())) / spanCount * this.outRect.left);
                    outRect.right = (int) (((float) this.outRect.right * (spanCount + 1) / spanCount) - outRect.left);
                }
            } else {
                if (layoutManager.getSpanSizeLookup().getSpanGroupIndex(childPosition, spanCount) == 0) {//第一排的需要left
                    outRect.left = this.outRect.left;
                }
                outRect.right = this.outRect.right;
                //这里忽略和合并项的问题，只考虑占满和单一的问题
                if (lp.getSpanSize() == spanCount) {//占满
                    outRect.top = this.outRect.top;
                    outRect.bottom = this.outRect.bottom;
                } else {
                    outRect.top = (int) (((float) (spanCount - lp.getSpanIndex())) / spanCount * this.outRect.top);
                    outRect.bottom = (int) (((float) this.outRect.bottom * (spanCount + 1) / spanCount) - outRect.top);
                }
            }
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager){
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            final int childPosition = parent.getChildAdapterPosition(view);
            int orientation=layoutManager.getOrientation();
            int childCount = parent.getAdapter().getItemCount();
            if (orientation== LinearLayoutManager.VERTICAL) {
                if (childPosition+1==childCount){
                    outRect.set(this.outRect.left, this.outRect.top, this.outRect.right,0);
                }else {
                    if (this.outRect.top>0)
                        outRect.set(this.outRect.left, this.outRect.top, this.outRect.right,0);
                    else
                        outRect.set(this.outRect.left, this.outRect.top, this.outRect.right,this.outRect.bottom);
                }
            }else {
                if (childPosition+1==childCount){
                    outRect.set(this.outRect.left, this.outRect.top,0, this.outRect.bottom);
                }else {
                    if (this.outRect.left>0)
                        outRect.set(this.outRect.left, this.outRect.top, 0,this.outRect.bottom);
                    else
                        outRect.set(this.outRect.left, this.outRect.top, this.outRect.right,this.outRect.bottom);
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
     * @param parent RecyclerView
     * @param position    对应的位置
     * @return
     */
    private boolean isFirstRaw(RecyclerView parent, int position) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int spanCount = getSpanCount(layoutManager);//总列数
        if (getOrientation(layoutManager) == RecyclerView.VERTICAL) {
            return position < spanCount;
        } else {
            if (spanCount == 1) return true;
            return position % spanCount == 0;
        }
    }
    /**
     * 判断是否是最后一行
     * @param parent RecyclerView
     * @param position    对应的位置
     * @return
     */
    private boolean isLastRaw(RecyclerView parent, int position) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int spanCount = getSpanCount(layoutManager);//总列数
        int childCount = layoutManager.getChildCount();//总网格数
        if (getOrientation(layoutManager) == RecyclerView.VERTICAL) {
            if (spanCount == 1) {
                return position + 1 == childCount;
            } else {
                int lastRawItemCount = childCount % spanCount;
                int rawCount = (childCount - lastRawItemCount) / spanCount + (lastRawItemCount > 0 ? 1 : 0);

                int rawPositionJudge = (position + 1) % spanCount;
                if (rawPositionJudge == 0) {
                    int positionRaw = (position + 1) / spanCount;
                    return rawCount == positionRaw;
                } else {
                    int rawPosition = (position + 1 - rawPositionJudge) / spanCount + 1;
                    return rawCount == rawPosition;
                }
            }
        } else {
            if (spanCount == 1) return true;
            return (position + 1) % spanCount == 0;
        }
    }
    /**
     * 判断是否是第一列
     * @param parent RecyclerView
     * @param position    对应的位置
     * @return
     */
    private boolean isFirstColumn(RecyclerView parent, int position) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int spanCount = getSpanCount(layoutManager);//总列数
        if (getOrientation(layoutManager) == RecyclerView.VERTICAL) {
            if (spanCount == 1) return true;
            return position % spanCount == 0;
        } else {
            return position < spanCount;
        }
    }
    /**
     * 判断是否是最后一列
     * @param parent RecyclerView
     * @param position    对应的位置
     * @return
     */
    private boolean isLastColumn(RecyclerView parent, int position) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int spanCount = getSpanCount(layoutManager);//总列数
        int childCount = layoutManager.getChildCount();//总网格数
        if (getOrientation(layoutManager) == RecyclerView.VERTICAL) {
            if (spanCount == 1) return true;
            return ((position + 1) % spanCount == 0)
                    ||(position + 1 == childCount);
        } else {
            if (spanCount == 1) {
                return position + 1 == childCount;
            } else {
                int lastRawItemCount = childCount % spanCount;
                int rawCount = (childCount - lastRawItemCount) / spanCount + (lastRawItemCount > 0 ? 1 : 0);

                int rawPositionJudge = (position + 1) % spanCount;
                if (rawPositionJudge == 0) {
                    int positionRaw = (position + 1) / spanCount;
                    return rawCount == positionRaw;
                } else {
                    int rawPosition = (position + 1 - rawPositionJudge) / spanCount + 1;
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
     * 判断是否是 HeaderView 或 FooterView
     * @param view         当前View
     * @param parent      RecyclerView
     * @return            false 不是 true 是
     */
    private boolean isHanderOrFooter(View view,RecyclerView parent){
        int hCounnt=0;
        int fCounnt=0;
        if (parent instanceof SwipeMenuRecyclerView) {
            hCounnt=((SwipeMenuRecyclerView)parent).getHeaderItemCount();
            fCounnt=((SwipeMenuRecyclerView)parent).getFooterItemCount();
        }
        if ( parent.getChildAdapterPosition(view)<hCounnt||parent.getChildLayoutPosition(view)
                >= parent.getAdapter().getItemCount()-fCounnt) {
            if(type==NONE){
                return false;
            }else if(type==ALL_HAVE || type == HEAD || type == FOOT || (view instanceof SwipeMenuRecyclerView.LoadMoreView)){
                return true;
            }
        }
        return false;
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
            //判断是 HeaderView 或 FooterView 不画分割线
            if (isHanderOrFooter(mChild,parent))continue;
            if (this.outRect.left>0)
                drawLeft(c, mChild, parent);
            else
                drawRight(c, mChild, parent);
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
            //判断是 HeaderView 或 FooterView 不画分割线
            if (isHanderOrFooter(mChild,parent))continue;
            if (this.outRect.top>0)
                drawTop(c, mChild, parent);
            else
                drawBottom(c, mChild, parent);
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
            //判断是 HeaderView 或 FooterView 不画分割线
            if (isHanderOrFooter(mChild,parent))continue;
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
            int position = recyclerView.getChildLayoutPosition(mChild);
            boolean firstRaw = isFirstRaw(recyclerView, position);
            boolean lastRaw = isLastRaw(recyclerView, position);
            boolean firstColumn = isFirstColumn(recyclerView, position);
            boolean lastColumn = isLastColumn(recyclerView, position);
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
