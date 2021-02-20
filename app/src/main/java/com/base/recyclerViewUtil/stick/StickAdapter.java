package com.base.recyclerViewUtil.stick;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;

/**
 * Created by wubo on 2017/8/22.
 */

public interface StickAdapter<T extends ViewHolder> extends Stick{
    public void onBindViewHolder(T holder, int position);
    public ViewHolder createViewHolder(ViewGroup parent, int viewType);
    public int getItemViewType(int position);
    public int getItemCount();
    public void bindViewHolder(T holder, int position);
    public int getHeaderCount();
}