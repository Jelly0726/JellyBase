package com.base.xrefreshview.view;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.base.xrefreshview.recyclerview.BaseRecyclerAdapter;

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
    /**
     * @param outRect 代表item距离左右以及上下的距离
     * @param span  代表item的列数
     * @param type 代表是否有head、foot、间距类型
     */
    public ItemDecoration(Rect outRect,int span,int type) {
        this.outRect = outRect;
        this.span = span;
        this.type=type;
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
            }else if(type == HEAD || type == FOOT){
                if (type == HEAD && parent.getChildAdapterPosition(view) == 0) {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                }else if (type == FOOT && parent.getChildAdapterPosition(view)
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
                outRect.left = this.outRect.left;
                outRect.right = this.outRect.right;
            } else {//其余的位置右边距为0
                outRect.left = this.outRect.left;
                outRect.right = 0;
            }
        } else if (parent.getLayoutManager() instanceof GridLayoutManager){
            if(parent.getAdapter() instanceof BaseRecyclerAdapter){
                BaseRecyclerAdapter adapter= (BaseRecyclerAdapter) parent.getAdapter();
                if(adapter.isFooter(parent.getChildAdapterPosition(view))){
                    outRect.left = 0;
                    outRect.right = 0;
                }else if (parent.getChildLayoutPosition(view) % span == 0) {
                    if(span==1){
                        outRect.left = this.outRect.left;
                        outRect.right = this.outRect.right;
                    }else {
                        outRect.left = this.outRect.left;
                        outRect.right = this.outRect.right/2;
                    }

                } else {
                    outRect.left = this.outRect.left/2;
                    outRect.right = this.outRect.right;
                }
            }else {
                if (parent.getChildLayoutPosition(view) % span == 0) {
                    if(span==1){
                        outRect.left = this.outRect.left;
                        outRect.right = this.outRect.right;
                    }else {
                        outRect.left = this.outRect.left;
                        outRect.right = this.outRect.right/2;
                    }
                } else {
                    outRect.left = this.outRect.left/2;
                    outRect.right = this.outRect.right;
                }
            }
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager){
            if(parent.getAdapter() instanceof BaseRecyclerAdapter){
                BaseRecyclerAdapter adapter= (BaseRecyclerAdapter) parent.getAdapter();
                if(adapter.isFooter(parent.getChildAdapterPosition(view))){
                    outRect.left = 0;
                    outRect.right = 0;
                }else{
                    outRect.left = this.outRect.left;
                    outRect.right = this.outRect.right;
                }
            }else {
                outRect.left = this.outRect.left;
                outRect.right = this.outRect.right;
            }
        }
    }
}
