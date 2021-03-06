package com.jelly.baselibrary.recyclerViewUtil;

import android.view.View;

import com.jelly.baselibrary.BaseAdapter;


/**
 * 自定义recyclerView的点击事件
 */
public interface OnItemClickListener {
    /**
     * @param view target view.
     * @param position position int[] of item.
     */
    void onItemClick(View view, BaseAdapter mAdapter, int... position);
}
