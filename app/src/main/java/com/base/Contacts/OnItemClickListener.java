package com.base.Contacts;

import android.view.View;

public interface  OnItemClickListener{
    /**
     * @param view target view.
     * @param adapterPosition position of item.
     */
    void onItemClick(View view, int adapterPosition);
}