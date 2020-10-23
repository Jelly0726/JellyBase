package com.base.Contacts;

import androidx.recyclerview.widget.RecyclerView;

public interface  OnItemClickListener{
    /**
     * @param view target view.
     * @param adapterPosition position of item.
     */
    void onItemClick(RecyclerView.ViewHolder view, int adapterPosition);
}