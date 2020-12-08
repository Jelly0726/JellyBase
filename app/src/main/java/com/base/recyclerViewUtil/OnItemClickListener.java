package com.base.recyclerViewUtil;

import android.view.View;

import com.base.BaseAdapter;


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
