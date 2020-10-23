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

package com.jelly.jellybase.seach;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.multiClick.OnMultiClickListener;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.recyclerview.BaseRecyclerAdapter;
import com.jelly.jellybase.R;
import com.jelly.jellybase.datamodel.Product;

import java.util.List;

public class SearchResultAdapter extends BaseRecyclerAdapter<SearchResultAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private List<Product> mList;
    public SearchResultAdapter(Context context, List<Product> mList) {
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
        final View view = mInflater.inflate(R.layout.searchresult_item, parent, false);
        view.setOnClickListener(listener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, boolean isItem) {
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
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
        public ImageView product_img;
        public TextView product_name;
        public TextView wholesale_price;
        public TextView gostore_tv;
        public TextView sales_volume;
        public TextView production_place;
        public TextView state_tv;
        public ViewHolder(View itemView) {
            super(itemView);
            product_img= (ImageView) itemView.findViewById(R.id.product_img);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            wholesale_price = (TextView) itemView.findViewById(R.id.wholesale_price);
            gostore_tv = (TextView) itemView.findViewById(R.id.gostore_tv);
            sales_volume = (TextView) itemView.findViewById(R.id.sales_volume);
            production_place = (TextView) itemView.findViewById(R.id.production_place);
            state_tv = (TextView) itemView.findViewById(R.id.state_tv);
        }
    }
    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
