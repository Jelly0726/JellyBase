package com.base.Contacts;

import android.support.v7.widget.RecyclerView;

public interface  OnItemClickListener{
    /**
     * @param view target view.
     * @param adapterPosition position of item.
     */
    void onItemClick(RecyclerView.ViewHolder view, int adapterPosition);
}