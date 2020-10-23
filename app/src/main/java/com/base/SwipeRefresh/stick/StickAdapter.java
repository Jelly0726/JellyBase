package com.base.SwipeRefresh.stick;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

/**
 * Created by wubo on 2017/8/22.
 */

public interface StickAdapter<T extends ViewHolder> extends Stick{
    void onBindViewHolder(T holder, int position);
    ViewHolder createViewHolder(ViewGroup parent, int viewType);
    int getItemViewType(int position);
    int getItemCount();
    void bindViewHolder(T holder, int position);
    int getHeaderCount();
}