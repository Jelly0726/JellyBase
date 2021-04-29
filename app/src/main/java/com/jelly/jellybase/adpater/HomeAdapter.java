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
import androidx.recyclerview.widget.RecyclerView;

import com.jelly.baselibrary.BaseAdapter;
import com.jelly.baselibrary.multiClick.OnMultiClickListener;
import com.jelly.jellybase.R;
import com.jelly.jellybase.datamodel.Product;
import com.yanzhenjie.album.impl.OnItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class HomeAdapter extends BaseAdapter<HomeAdapter.ViewHolder> {

    private List<Product> mList;
    public HomeAdapter(Context context, List<Product> mList) {
        super(context);
        this.mList=mList;
    }
    private OnMultiClickListener listener=new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    };

    @Override
    public void notifyDataSetChanged(@NotNull List<?> dataList) {
        this.mList.clear();
        this.mList.addAll((Collection<? extends Product>) dataList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = getInflater().inflate(R.layout.home_item_product, parent, false);
        view.setOnClickListener(listener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * item的ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView product_img;
        public TextView price_tv;
        public TextView minshipment_tv;
        public TextView repertory_tv;
        public ViewHolder(View itemView) {
            super(itemView);
            product_img= (ImageView) itemView.findViewById(R.id.product_img);
            price_tv = (TextView) itemView.findViewById(R.id.price_tv);
            minshipment_tv = (TextView) itemView.findViewById(R.id.minshipment_tv);
            repertory_tv = (TextView) itemView.findViewById(R.id.repertory_tv);
        }
    }
    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
