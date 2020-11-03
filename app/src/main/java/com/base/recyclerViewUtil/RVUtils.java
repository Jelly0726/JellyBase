package com.base.recyclerViewUtil;


import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.ObjectStreamException;

public class RVUtils {
    private RVUtils(){}
    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder{
        private static final RVUtils instance = new RVUtils();
    }
    /**
     * 单一实例
     */
    public static RVUtils getInstance() {
        return RVUtils.SingletonHolder.instance;
    }
    /**
     * 要杜绝单例对象在反序列化时重新生成对象，那么必须加入如下方法：
     * @return
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return RVUtils.SingletonHolder.instance;
    }
    /**
     * LinearLayoutManager 获取屏幕内可见条目的起始位置
     * @param manager
     * @param isCompletely 是否完全可见
     * @return
     */
    public int[] findRangeLinear(@NonNull LinearLayoutManager manager, boolean isCompletely) {
        //findFirstVisibleItemPosition()  返回当前第一个可见Item的position
        // findFirstCompletelyVisibleItemPosition() 返回当前第一个完全可见Item的position
        int[] range = new int[2];
        if (manager==null)return range;
        if (isCompletely){
            range[0] = manager.findFirstCompletelyVisibleItemPosition();
            range[1] = manager.findLastCompletelyVisibleItemPosition();
        }else {
            range[0] = manager.findFirstVisibleItemPosition();
            range[1] = manager.findLastVisibleItemPosition();
        }
        return range;
    }
    /**
     * GridLayoutManager 获取屏幕内可见条目的起始位置
     * @param manager
     * @param isCompletely 是否完全可见
     * @return
     */
    public int[] findRangeGrid(@NonNull GridLayoutManager manager, boolean isCompletely) {
        //findFirstVisibleItemPosition()  返回当前第一个可见Item的position
        // findFirstCompletelyVisibleItemPosition() 返回当前第一个完全可见Item的position
        int[] range = new int[2];
        if (manager==null)return range;
        if (isCompletely){
            range[0] = manager.findFirstCompletelyVisibleItemPosition();
            range[1] = manager.findLastCompletelyVisibleItemPosition();
        }else {
            range[0] = manager.findFirstVisibleItemPosition();
            range[1] = manager.findLastVisibleItemPosition();
        }
        return range;
    }

    /**
     * StaggeredGridLayoutManager 获取屏幕内可见条目的起始位置
     * @param manager
     * @param isCompletely 是否完全可见
     * @return
     */
    public int[] findRangeStaggeredGrid(@NonNull StaggeredGridLayoutManager manager, boolean isCompletely) {
        int[] range = new int[2];
        if (manager==null)return range;
        //findFirstVisibleItemPosition()  返回当前第一个可见Item的position
        // findFirstCompletelyVisibleItemPosition() 返回当前第一个完全可见Item的position
        int[] startPos = new int[manager.getSpanCount()];
        int[] endPos = new int[manager.getSpanCount()];
        if (isCompletely){
            manager.findFirstCompletelyVisibleItemPositions(startPos);
            manager.findLastCompletelyVisibleItemPositions(endPos);
        }else {
            manager.findFirstVisibleItemPositions(startPos);
            manager.findLastVisibleItemPositions(endPos);
        }
        range = findRange(startPos, endPos);
        return range;
    }

    private int[] findRange(int[] startPos, int[] endPos) {
        int start = startPos[0];
        int end = endPos[0];
        for (int i = 1; i < startPos.length; i++) {
            if (start > startPos[i]) {
                start = startPos[i];
            }
        }
        for (int i = 1; i < endPos.length; i++) {
            if (end < endPos[i]) {
                end = endPos[i];
            }
        }
        int[] res = new int[]{start, end};
        return res;
    }

    /**
     * 通过滚动的类型来进行相应的滚动
     * @param recyclerView
     * @param position
     * @param isCompletely  是否完全可见
     */
    public void smoothMoveToPosition(RecyclerView recyclerView, int position, boolean isCompletely) {
        if (recyclerView == null) return;
        if (position<0)return;
        LinearLayoutManager mLayoutManager= (LinearLayoutManager) recyclerView.getLayoutManager();
        if (mLayoutManager==null)return;
        //findFirstVisibleItemPosition()  返回当前第一个可见Item的position
        // findFirstCompletelyVisibleItemPosition() 返回当前第一个完全可见Item的position
        int firstItem =0;
        int lastItem = 0;
        if (isCompletely){
            firstItem =mLayoutManager.findFirstCompletelyVisibleItemPosition();
            lastItem =mLayoutManager.findLastCompletelyVisibleItemPosition();
        }else {
            firstItem =mLayoutManager.findFirstVisibleItemPosition();
            lastItem = mLayoutManager.findLastVisibleItemPosition();
        }
        if (position <= firstItem) {
            recyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            int top = recyclerView.getChildAt(position - firstItem).getTop();
            recyclerView.smoothScrollBy(0, top);
        } else {
            recyclerView.smoothScrollToPosition(position);
        }
    }

    /**
     *  Reyclerview移动到中间位置的方法
     * @param recyclerView
     * @param position
     * @param isCompletely 是否完全可见
     */
    public void moveToMiddle(RecyclerView recyclerView, int position,boolean isCompletely) {
        if (recyclerView == null) return;
        if (position<0)return;
        LinearLayoutManager mLayoutManager= (LinearLayoutManager) recyclerView.getLayoutManager();
        if (mLayoutManager==null)return;
        //先从RecyclerView的LayoutManager中获取当前第一项和最后一项的Position
        //findFirstVisibleItemPosition()  返回当前第一个可见Item的position
        // findFirstCompletelyVisibleItemPosition() 返回当前第一个完全可见Item的position
        int firstItem =0;
        int lastItem = 0;
        if (isCompletely){
            firstItem =mLayoutManager.findFirstCompletelyVisibleItemPosition();
            lastItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
        }else {
            firstItem =mLayoutManager.findFirstVisibleItemPosition();
            lastItem = mLayoutManager.findLastVisibleItemPosition();
        }
        //中间位置
        int middle = (firstItem + lastItem)/2;
        // 取绝对值，index下标是当前的位置和中间位置的差，下标为index的view的top就是需要滑动的距离
        int index = (position - middle) >= 0 ? position - middle : -(position - middle);
        //左侧列表一共有getChildCount个Item，如果>这个值会返回null，程序崩溃，如果>getChildCount直接滑到指定位置,或者,都一样啦
        if (index >= recyclerView.getChildCount()) {
            recyclerView.scrollToPosition(position);
        } else {
            //如果当前位置在中间位置上面，往下移动，这里为了防止越界
            if(position < middle) {
                recyclerView.scrollBy(0, -recyclerView.getChildAt(index).getTop());
                // 在中间位置的下面，往上移动
            } else {
                recyclerView.scrollBy(0, recyclerView.getChildAt(index).getTop());
            }
        }
    }
    /***
     * 解决列表图片被遮挡时 RecyclerView 滑动一段距离 显示出完整的预览图
     * 根据getLocalVisibleRect 相对view自身坐标计算偏移量 只要top和left都是0 right等于view宽度 bottom等于view高度 即图片没有被隐藏
     * 上面被隐藏 top肯定大于0 滑动偏移量即为-top
     * 下面被隐藏 bottom肯定小于 view高度 偏移量即为view高度-bottom
     *
     * 参照 飞聊app 解决方案
     */
    public void setFixHideForRecyclerView(RecyclerView recyclerView, View view) {
        if (null != recyclerView && null != view) {
            int height = view.getHeight();
            Rect rect = new Rect();
            view.getLocalVisibleRect(rect);
            if (rect.top > 0 && rect.left == 0 && rect.bottom == height) {
                //上面被遮挡
                int offset = rect.top;
                recyclerView.scrollBy(0, -offset);
            } else if (rect.top == 0 && rect.left == 0 && rect.bottom < height) {
                //下面被遮挡
                int offset = height - rect.bottom;
                recyclerView.scrollBy(0, offset);
            }
        }
    }

    /**
     * 计算某个位置的item的可见百分数
     * @param recyclerView
     * @param position
     * @return    可见百分数0 不可见  100 百分百可见
     */
    public int calculateItemVisiblePercent(RecyclerView recyclerView,int position) {
        if (position <0) return 0;
        if (recyclerView == null) return 0;
        LinearLayoutManager mLayoutManager= (LinearLayoutManager) recyclerView.getLayoutManager();
        if (mLayoutManager==null)return 0;
        int visiblePercent=0; //可见百分数0 不可见  100 百分百可见
        Rect rvRect = new Rect();
        //获取recyclerview可见区域相对屏幕左上角的位置坐标
        recyclerView.getGlobalVisibleRect(rvRect);
        //根据position获得对应的view
        View itemView = mLayoutManager.findViewByPosition(position);
        if (itemView!=null) {
            int itemHeight = itemView.getHeight();
            Rect rowRect = new Rect();
            //获取item可见区域相对屏幕左上角的位置坐标
            itemView.getGlobalVisibleRect(rowRect);
            if (rowRect.bottom >= rvRect.bottom) {
                //item在recyclerview底部且有部分不可见
                int visibleHeightFirst = rvRect.bottom - rowRect.top;
                visiblePercent = (visibleHeightFirst * 100) / itemHeight;
            } else {
                //item在recyclerview中或顶部
                int visibleHeightFirst = rowRect.bottom - rvRect.top;
                visiblePercent = (visibleHeightFirst * 100) / itemHeight;
            }
            if (visiblePercent > 100) visiblePercent = 100;
        }
        return visiblePercent;
    }

    /**
     * 获取可见百分数大于给定值的第一个item坐标
     * @param recyclerView
     * @param percent      给定百分数
     * @return
     */
    public int getItemVisiblePosition(RecyclerView recyclerView,int percent){
        if (recyclerView == null) return -1;
        LinearLayoutManager mLayoutManager= (LinearLayoutManager) recyclerView.getLayoutManager();
        if (mLayoutManager==null)return -1;
        //获取第一个可见item的位置
        final int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
        //获取最后一个可见item的位置
        final int lastPosition = mLayoutManager.findLastVisibleItemPosition();
        Rect rvRect = new Rect();
        //获取recyclerview可见区域相对屏幕左上角的位置坐标
        recyclerView.getGlobalVisibleRect(rvRect);
        //计算recyclerview可见区域的高度
        int rvHeight = rvRect.bottom - rvRect.top;
        int index=-1;
        for (int position = firstPosition; position <= lastPosition; position++) {
            int visiblePercent;
            //根据position获得对应的view
            View itemView = mLayoutManager.findViewByPosition(position);
            int itemHeight = itemView.getHeight();
            Rect rowRect = new Rect();
            //获取item可见区域相对屏幕左上角的位置坐标
            itemView.getGlobalVisibleRect(rowRect);
            if (rowRect.bottom >= rvRect.bottom) { //item在recyclerview底部且有部分不可见
                int visibleHeightFirst = rvRect.bottom - rowRect.top;
                visiblePercent = (visibleHeightFirst * 100) / itemHeight;
            } else { //item在recyclerview中或顶部
                int visibleHeightFirst = rowRect.bottom - rvRect.top;
                visiblePercent = (visibleHeightFirst * 100) / itemHeight;
            }
            if (visiblePercent > 100) visiblePercent = 100;
            if (visiblePercent>=percent){
                return position;
            }else {
                //如果当前item的高度大于等于recyclerview可见区域的高度
                if (itemHeight>=rvHeight){
                    //计算recyclerview可见区域占item高度的百分数
                    int vp=rvHeight*100/itemHeight;
                    if (vp<=percent&&vp>=15){
                        index=position;
                    }
                }
            }
        }
        return index;
    }
    /**
     * 获取第一个可见的item坐标
     * @param recyclerView
     * @return
     */
    public int getFirstVisibleItemPosition(RecyclerView recyclerView){
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        //判断是当前layoutManager是否为LinearLayoutManager
        // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            //获取第一个可见view的位置
            int firstItemPosition = linearManager.findFirstVisibleItemPosition();
            return firstItemPosition;
        }
        return 0;
    }
    /**
     * 获取最后一个可见的item坐标
     * @param recyclerView
     * @return
     */
    public int getLastVisibleItemPosition(RecyclerView recyclerView){
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        //判断是当前layoutManager是否为LinearLayoutManager
        // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            //获取最后一个可见view的位置
            int lastItemPosition = linearManager.findLastVisibleItemPosition();
            return lastItemPosition;
        }
        return 0;
    }
}
