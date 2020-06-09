package com.base.recyclerViewUtil;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

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
     * @return
     */
    public int[] findRangeLinear(LinearLayoutManager manager) {
        int[] range = new int[2];
        range[0] = manager.findFirstVisibleItemPosition();
        range[1] = manager.findLastVisibleItemPosition();
        return range;
    }
    /**
     * GridLayoutManager 获取屏幕内可见条目的起始位置
     * @param manager
     * @return
     */
    public int[] findRangeGrid(GridLayoutManager manager) {
        int[] range = new int[2];
        range[0] = manager.findFirstVisibleItemPosition();
        range[1] = manager.findLastVisibleItemPosition();
        return range;
    }

    /**
     * StaggeredGridLayoutManager 获取屏幕内可见条目的起始位置
     * @param manager
     * @return
     */
    public int[] findRangeStaggeredGrid(StaggeredGridLayoutManager manager) {
        int[] startPos = new int[manager.getSpanCount()];
        int[] endPos = new int[manager.getSpanCount()];
        manager.findFirstVisibleItemPositions(startPos);
        manager.findLastVisibleItemPositions(endPos);
        int[] range = findRange(startPos, endPos);
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
     */
    public void smoothMoveToPosition(RecyclerView recyclerView,int position) {
        int firstItem =((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        int lastItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
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
     */
    public void moveToMiddle(RecyclerView recyclerView, int position) {
        //先从RecyclerView的LayoutManager中获取当前第一项和最后一项的Position
        int firstItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        int lastItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
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
}
