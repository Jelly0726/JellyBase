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

package com.jelly.jellybase.adpater;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.addressmodel.Area;
import com.base.applicationUtil.AppUtils;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.recyclerview.BaseRecyclerAdapter;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

public class AddressAdapter extends BaseRecyclerAdapter<AddressAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private List<? extends Area> mList;

    public AddressAdapter(Context context, List<? extends Area> mList) {
        this.context=context;
        mInflater = LayoutInflater.from(context);
        this.mList=mList;
    }
    public void setData(List<? extends Area> mList){
        this.mList=mList;
        notifyDataSetChanged();
    }
    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {
        return mList.size();
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        TextView textView=new TextView(context);
        textView.setOnClickListener(listener);
        textView.setHeight(AppUtils.dipTopx(context,50));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.parseColor("#313131"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, boolean isItem) {
        holder.textView.setTag(position);
        holder.textView.setText(mList.get(position).getAreaName());
    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    };
    /**
     * item的ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            AutoUtils.autoSize(itemView);
            textView= (TextView) itemView;
        }
    }
    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
