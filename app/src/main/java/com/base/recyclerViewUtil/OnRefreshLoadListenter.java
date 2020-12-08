package com.base.recyclerViewUtil;


import com.base.BaseAdapter;

public interface OnRefreshLoadListenter {
    /**
     * 刷新
     * @param position
     */
    public void onRefresh(BaseAdapter mAdapter, int position);
    /**
     * 加载更多
     */
    public void onLoad(BaseAdapter mAdapter, int position);
}
