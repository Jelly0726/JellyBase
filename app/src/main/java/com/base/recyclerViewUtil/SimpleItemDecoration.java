package com.base.recyclerViewUtil;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
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
public class SimpleItemDecoration extends RecyclerView.ItemDecoration{

    private int space;
    private int span;
    public static final int NONE=-1;//都没有
    public static final int HEAD=0;//表示第一个item是head
    public static final int FOOT=1;//表示最后一个item是foot
    public static final int ALL_HAVE=2;//表示都有
    public static final int LR=3;//表示左右边距为0
    private int type=-1;//-1 表示都没有 0 表示第一个item是head 1表示最后一个item是foot 2表示都有
    private Drawable mDivider;
    /**
     *
     * @param space 代表item距离左右以及上下的距离
     * @param span  代表item的列数
     * @param type 代表是否有head、foot、间距类型
     */
    public SimpleItemDecoration(int space, int span, int type) {
        this(space,span,type, Color.argb(0,153, 153, 153));
    }
    /**
     *
     * @param space 代表item距离左右以及上下的距离
     * @param span  代表item的列数
     * @param type 代表是否有head、foot、间距类型
     * @param color 分割线颜色
     */
    public SimpleItemDecoration(int space, int span, int type, @ColorInt int color) {
        this.space = space;
        this.span = span;
        this.type=type;
        mDivider = new ColorDrawable(color);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
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
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        c.save();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);
            if (childPosition < 0) continue;
            final int left = child.getLeft();
            final int top = child.getBottom();
            final int right = child.getRight();
            final int bottom = top + space;
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
            final int left =  child.getRight();
            final int top = child.getTop();
            final int right = left + space;
            final int bottom = child.getBottom();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
        c.restore();
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space;
        if ( parent.getChildAdapterPosition(view)==0||parent.getChildLayoutPosition(view)
                == parent.getAdapter().getItemCount()-1) {
            if(type==NONE || type==LR){
                parentLayoutManager(outRect,view,parent,state);
            }else if(type==ALL_HAVE){
                outRect.left = 0;
                outRect.right = 0;
                outRect.top = 0;
            }else if(type == HEAD || type == FOOT){
                if (type == HEAD && parent.getChildAdapterPosition(view) == 0) {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                }else {
                    parentLayoutManager(outRect,view,parent,state);
                }
                if (type == FOOT && parent.getChildAdapterPosition(view)
                        == parent.getAdapter().getItemCount()-1) {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
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
                outRect.left = space;
                outRect.right = space;
            } else {//其余的位置右边距为0
                outRect.left = space;
                outRect.right = 0;
            }
        } else if (parent.getLayoutManager() instanceof GridLayoutManager){
            outRect.left = 0;
            outRect.right = 0;
            if (parent.getChildLayoutPosition(view) % span == 0) {
                if(span==1){
                    if(LR!=type) {
                        outRect.left = space;
                        outRect.right = space;
                    }
                }else {
                    if(LR!=type) {
                        outRect.left = space;
                    }
                    outRect.right = space/2;
                }
            } else {
                outRect.left = space/2;
                if(LR!=type) {
                    outRect.right = space;
                }
            }
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager){
            outRect.left = space;
            outRect.right = space;
        }
    }
}
