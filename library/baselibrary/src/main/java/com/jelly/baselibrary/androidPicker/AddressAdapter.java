/*
 * Copyright 2014 Eduardo Barrenechea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jelly.baselibrary.androidPicker;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jelly.baselibrary.BaseAdapter;
import com.jelly.baselibrary.addressmodel.Area;
import com.jelly.baselibrary.applicationUtil.AppUtils;
import com.yanzhenjie.album.impl.OnItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddressAdapter extends BaseAdapter<AddressAdapter.ViewHolder> {
    private List<? extends Area> mList;

    public AddressAdapter(Context context, List<? extends Area> mList) {
        super(context);
        this.mList=mList;
    }
    public void setData(List<? extends Area> mList){
        this.mList=mList;
        notifyDataSetChanged();
    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    };

    @Override
    public void notifyDataSetChanged(@NotNull List<?> dataList) {
        this.mList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView=new TextView(getContext());
        textView.setOnClickListener(listener);
        textView.setHeight(AppUtils.dipTopx(getContext(),50));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.parseColor("#313131"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setTag(position);
        holder.textView.setText(mList.get(position).getAreaName());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * item的ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView= (TextView) itemView;
        }
    }
    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
