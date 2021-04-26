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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jelly.baselibrary.BaseAdapter;
import com.jelly.baselibrary.applicationUtil.AppPrefs;
import com.jelly.baselibrary.bankcard.BankCardInfo;
import com.jelly.baselibrary.bankcard.BankUtil;
import com.jelly.baselibrary.config.ConfigKey;
import com.jelly.baselibrary.xrefreshview.listener.OnItemClickListener;
import com.jelly.jellybase.R;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class BankCardListAdapter extends BaseAdapter<BankCardListAdapter.ViewHolder> {

    private List<BankCardInfo> mList;

    public BankCardListAdapter(Context context, List<BankCardInfo> mList) {
        super(context);
        this.mList=mList;
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
        this.mList.addAll((Collection<? extends BankCardInfo>) dataList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = getInflater().inflate(R.layout.bankcardlist_item, parent, false);
        view.setOnClickListener(listener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.bank_name.setText(mList.get(position).getBankName());
        holder.bank_no.setText(BankUtil.getBankCardID(mList.get(position).getBankNo()));
        holder.card_type.setText(mList.get(position).getType());
        if (mList.get(position).getIsdefault()){
            holder.tag_tv.setVisibility(View.VISIBLE);
            Gson gson=new Gson();
            String json=gson.toJson(mList.get(position));
            AppPrefs.putString(getContext().getApplicationContext(),
                    ConfigKey.DEFAULT_BANK,json);
        }else {
            holder.tag_tv.setVisibility(View.GONE);
        }

        if(mList.get(position).getBankLogo()!=null){
            if (mList.get(position).getBankLogo().trim().length()>6){
                Glide.with(getContext())
                        .load(mList.get(position).getBankLogo().trim())
                        .placeholder(R.drawable.yinlian)
                        .error(R.drawable.yinlian)
                        .dontAnimate()
                        .centerCrop()
                        .into(holder.bank_logo);
                return;
            }else {
                holder.bank_logo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.yinlian));
            }
        }else {
            holder.bank_logo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.yinlian));
        }
        holder.bank_logo.setImageResource(mList.get(position).getBankDraw());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

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
