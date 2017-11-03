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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.multiClick.OnMultiClickListener;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.recyclerview.BaseRecyclerAdapter;
import com.jelly.jellybase.R;
import com.zhy.autolayout.utils.AutoUtils;

public class MainAdapter extends BaseRecyclerAdapter<MainAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private String[] mList;
    private View defaultView;
    public MainAdapter(Context context,String[] mList) {
        this.context=context;
        mInflater = LayoutInflater.from(context);
        this.mList=mList;
    }
    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }
    @Override
    public int getAdapterItemCount() {
        return mList.length;
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        final View view = mInflater.inflate(R.layout.main_item, parent, false);
        view.setOnClickListener(listener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position, boolean isItem) {
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
        holder.tv.setText(mList[position]);
    }
    private OnMultiClickListener listener=new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    };
    /**
     * item的ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;
        public ViewHolder(View itemView) {
            super(itemView);
            AutoUtils.autoSize(itemView);
            tv= (TextView) itemView.findViewById(R.id.tv);
        }
    }
    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
