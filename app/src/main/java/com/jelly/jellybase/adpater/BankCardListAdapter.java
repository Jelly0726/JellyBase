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
import android.widget.ImageView;
import android.widget.TextView;

import com.base.bankcard.BankCardInfo;
import com.base.bankcard.BankUtil;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.recyclerview.BaseRecyclerAdapter;
import com.bumptech.glide.Glide;
import com.jelly.jellybase.R;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

public class BankCardListAdapter extends BaseRecyclerAdapter<BankCardListAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private List<BankCardInfo> mList;

    public BankCardListAdapter(Context context, List<BankCardInfo> mList) {
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
        final View view = mInflater.inflate(R.layout.bankcardlist_item, parent, false);
        view.setOnClickListener(listener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, boolean isItem) {
        holder.itemView.setTag(position);
        holder.bank_name.setText(mList.get(position).getBankName());
        holder.bank_no.setText(BankUtil.getBankCardID(mList.get(position).getBankNo()));
        holder.card_type.setText(mList.get(position).getType());
        if(mList.get(position).getBankLogo()!=null){
            if (mList.get(position).getBankLogo().trim().length()>6){
                Glide.with(context)
                        .load(mList.get(position).getBankLogo().trim())
                        .placeholder(R.drawable.yinlian)
                        .error(R.drawable.yinlian)
                        .dontAnimate()
                        .centerCrop()
                        .into(holder.bank_logo);
                return;
            }
        }
        holder.bank_logo.setImageResource(mList.get(position).getBankDraw());
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
        ImageView bank_logo;
        TextView bank_name;
        TextView card_type;
        TextView bank_no;
        TextView tag_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            AutoUtils.autoSize(itemView);
            bank_logo= (ImageView) itemView.findViewById(R.id.bank_logo);
            bank_name = (TextView) itemView.findViewById(R.id.bank_name);
            card_type = (TextView) itemView.findViewById(R.id.card_type);
            bank_no = (TextView) itemView.findViewById(R.id.bank_no);
            tag_tv = (TextView) itemView.findViewById(R.id.tag_tv);
        }
    }
    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
