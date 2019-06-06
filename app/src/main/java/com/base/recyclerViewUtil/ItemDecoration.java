package com.base.recyclerViewUtil;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

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
    private Drawer mDrawer;
    /**
     * @param outRect 代表item距离左右以及上下的距离
     * @param span  代表item的列数
     * @param type 代表是否有head、foot、间距类型
     */
    public ItemDecoration(Rect outRect, int span, int type, int dividerColor) {
        this.outRect = outRect;
        this.span = span;
        this.type=type;
        this.mDrawer = new ColorDrawer(dividerColor, outRect.left, outRect.left);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = this.outRect.bottom;
        outRect.top = this.outRect.top;
        if ( parent.getChildAdapterPosition(view)==0||parent.getChildLayoutPosition(view)
                == parent.getAdapter().getItemCount()-1) {
            if(type==NONE){
                parentLayoutManager(outRect,view,parent,state);
            }else if(type==ALL_HAVE){
                outRect.left = 0;
                outRect.right = 0;
                outRect.top = 0;
                outRect.bottom=0;
            }else if(type == HEAD || type == FOOT){
                if (type == HEAD && parent.getChildAdapterPosition(view) == 0) {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom=0;
                }else if (type == FOOT && parent.getChildAdapterPosition(view)
                        == parent.getAdapter().getItemCount()-1) {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom=0;
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
                outRect.left = this.outRect.left;
                outRect.right = this.outRect.right;
            } else {//其余的位置右边距为0
                outRect.left = this.outRect.left;
                outRect.right = 0;
            }
        } else if (parent.getLayoutManager() instanceof GridLayoutManager){
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
            outRect.left = this.outRect.left;
            outRect.right = this.outRect.right;
        }
    }
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

    private int getSpanCount(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager)layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager)layoutManager).getSpanCount();
        }
        return 1;
    }

    private boolean isFirstRaw(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            return position < columnCount;
        } else {
            if (columnCount == 1) return true;
            return position % columnCount == 0;
        }
    }

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

    private boolean isFirstColumn(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            if (columnCount == 1) return true;
            return position % columnCount == 0;
        } else {
            return position < columnCount;
        }
    }

    private boolean isLastColumn(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            if (columnCount == 1) return true;
            return (position + 1) % columnCount == 0;
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
            for (int i = 0; i < childCount; i++) {
                View view = layoutManager.getChildAt(i);
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            }
            canvas.restore();
        }else if (layoutManager instanceof LinearLayoutManager) {
            canvas.save();
            for (int i = 0; i < childCount; i++) {
                View view = layoutManager.getChildAt(i);
                assert view != null;
                int position = parent.getChildLayoutPosition(view);

                if (orientation == RecyclerView.VERTICAL) {
                    drawVertical(canvas, view, position, spanCount, childCount);
                } else {
                    drawHorizontal(canvas, view, position, spanCount, childCount);
                }
            }
            canvas.restore();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            canvas.save();
            for (int i = 0; i < childCount; i++) {
                View view = layoutManager.getChildAt(i);
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            }
            canvas.restore();
        }
    }

    private void drawHorizontal(Canvas canvas, View view, int position, int spanCount, int childCount) {
        boolean firstRaw = isFirstRaw(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean lastRaw = isLastRaw(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean firstColumn = isFirstColumn(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean lastColumn = isLastColumn(RecyclerView.HORIZONTAL, position, spanCount, childCount);

        if (spanCount == 1) {
            if (firstRaw && lastColumn) { // xxxx
                // Nothing.
            } else if (firstColumn) { // xx|x
                mDrawer.drawRight(view, canvas);
            } else if (lastColumn) { // |xxx
                mDrawer.drawLeft(view, canvas);
            } else { // |x|x
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawRight(view, canvas);
            }
        } else {
            if (firstColumn && firstRaw) { // xx|-
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (firstColumn && lastRaw) { // x-|x
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
            } else if (lastColumn && firstRaw) { // |xx-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (lastColumn && lastRaw) { // |-xx
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
            } else if (firstColumn) { // x-|-
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (lastColumn) { // |-x-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (firstRaw) { // |x|-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (lastRaw) { // |-|x
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
            } else { // |-|-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            }
        }
    }

    private void drawVertical(Canvas canvas, View view, int position, int spanCount, int childCount) {
        boolean firstRaw = isFirstRaw(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean lastRaw = isLastRaw(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean firstColumn = isFirstColumn(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean lastColumn = isLastColumn(RecyclerView.VERTICAL, position, spanCount, childCount);

        if (spanCount == 1) {
            if (firstRaw && lastRaw) { // xxxx
                // Nothing.
            } else if (firstRaw) { // xxx-
                mDrawer.drawBottom(view, canvas);
            } else if (lastRaw) { // x-xx
                mDrawer.drawTop(view, canvas);
            } else { // x-x-
                mDrawer.drawTop(view, canvas);
                mDrawer.drawBottom(view, canvas);
            }
        } else {
            if (firstRaw && firstColumn) { // xx|-
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (firstRaw && lastColumn) { // |xx-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (lastRaw && firstColumn) { // x-|x
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
            } else if (lastRaw && lastColumn) { // |-xx
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
            } else if (firstRaw) { // |x|-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (lastRaw) { // |-|x
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
            } else if (firstColumn) { // x-|-
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (lastColumn) { // |-x-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else { // |-|-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            }
        }
    }
}
