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

import com.base.httpmvp.databean.AccountDetail;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.recyclerview.BaseRecyclerAdapter;
import com.jelly.jellybase.R;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

public class AccountDetailsAdapter extends BaseRecyclerAdapter<AccountDetailsAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private List<AccountDetail> mList;

    public AccountDetailsAdapter(Context context, List<AccountDetail> mList) {
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
        return mList.size();
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        final View view = mInflater.inflate(R.layout.account_details_item, parent, false);
        //view.setOnClickListener(listener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, boolean isItem) {
        //holder.itemView.setTag(position);
        if(mList.get(position).getAmount()>=0){
            holder.amount_tv.setText("" + mList.get(position).getAmount());
            holder.amount_tv.setTextColor(context.getResources().getColor(R.color.chonzhi));
        }else {
            holder.amount_tv.setText("" + mList.get(position).getAmount());
            holder.amount_tv.setTextColor(context.getResources().getColor(R.color.tixian));
        }
        holder.time_tv.setText(mList.get(position).getAddtime());
        holder.type_tv.setText(mList.get(position).getType());
        holder.state_tv.setText(mList.get(position).getStatus());

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
        public TextView state_tv;
        public TextView type_tv;
        public TextView time_tv;
        public TextView amount_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            AutoUtils.autoSize(itemView);
            type_tv = (TextView) itemView.findViewById(R.id.type_tv);
            state_tv = (TextView) itemView.findViewById(R.id.state_tv);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            amount_tv = (TextView) itemView.findViewById(R.id.amount_tv);
        }
    }
    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
