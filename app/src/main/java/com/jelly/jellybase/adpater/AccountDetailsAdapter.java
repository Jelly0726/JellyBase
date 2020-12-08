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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.base.BaseAdapter;
import com.base.model.AccountDetail;
import com.jelly.jellybase.R;

import java.util.ArrayList;
import java.util.List;

public class AccountDetailsAdapter extends BaseAdapter<AccountDetailsAdapter.ViewHolder> {

    private List<AccountDetail> mList=new ArrayList<>();

    public AccountDetailsAdapter(Context context) {
        super(context);
    }

    @Override
    public void notifyDataSetChanged(List dataList) {
        //adapter.notifyDataSetChanged没有反应，触摸滑动屏幕才刷新
        this.mList.clear();
        this.mList.addAll(dataList);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(getInflater().inflate(R.layout.account_details_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mList.get(position).getAmount()>=0){
            holder.amount_tv.setText("+" + mList.get(position).getAmount());
//            holder.amount_tv.setTextColor(getContext().getResources().getColor(R.color.income));
        }else {
            holder.amount_tv.setText("-" + mList.get(position).getAmount());
//            holder.amount_tv.setTextColor(getContext().getResources().getColor(R.color.disburse));
        }
        holder.time_tv.setText(mList.get(position).getAddtime());
        holder.type_tv.setText(mList.get(position).getType());
        holder.state_tv.setText(mList.get(position).getStatus());

    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

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
            type_tv = (TextView) itemView.findViewById(R.id.type_tv);
            state_tv = (TextView) itemView.findViewById(R.id.state_tv);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            amount_tv = (TextView) itemView.findViewById(R.id.amount_tv);
        }
    }
}
