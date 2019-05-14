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
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.base.multiClick.AntiShake;
import com.base.multiClick.OnMultiClickListener;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.recyclerview.BaseRecyclerAdapter;
import com.jelly.jellybase.R;
import com.jelly.jellybase.activity.AddressEditActivity;
import com.jelly.jellybase.datamodel.RecevierAddress;

import java.util.List;

public class AddressListAdapter extends BaseRecyclerAdapter<AddressListAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private List<RecevierAddress> mList;
    private View defaultView;
    public AddressListAdapter(Context context, List<RecevierAddress> mList) {
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
        final View view = mInflater.inflate(R.layout.addressrecevier_item, parent, false);
        view.setOnClickListener(listener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position, boolean isItem) {
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);

        holder.default_checkBox.setChecked(mList.get(position).isDefault());
        holder.default_checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(holder.default_checkBox.isChecked()){
                        for(RecevierAddress recevierAddress:mList){
                            recevierAddress.setIsdefault(false);
                        }
                        mList.get(position).setIsdefault(true);
                        notifyDataSetChanged();
                    }
                }catch (Exception e){

                }
            }
        });
        holder.address_edit.setTag(position);
        holder.address_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AntiShake.check(v.getId()))return;
                Intent intent=new Intent(context,AddressEditActivity.class);
                intent.putExtra("recevierAddress",mList.get((int) v.getTag()));
                context.startActivity(intent);
            }
        });
        holder.address_delete.setTag(position);
        holder.address_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AntiShake.check(v.getId()))return;
                int id=mList.get((int) v.getTag()).getAddressid();
                if (deleteAddress!=null){
                    deleteAddress.delete(id, (Integer) v.getTag());
                }
            }
        });
        holder.address_tv.setText(mList.get(position).getProvince()+
                mList.get(position).getCity()+
                mList.get(position).getArea()+
                mList.get(position).getAddress());
        holder.name_tv.setText(mList.get(position).getName());
        holder.phone_tv.setText(mList.get(position).getPhone());

    }
    private OnMultiClickListener listener=new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    };

    private DeleteAddress deleteAddress;

    public void setDeleteAddress(DeleteAddress deleteAddress) {
        this.deleteAddress = deleteAddress;
    }

    public interface DeleteAddress{
        public void delete(int id, int position);
    }
    /**
     * item的ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name_tv;
        public TextView phone_tv;
        public TextView address_tv;
        public CheckBox default_checkBox;
        public TextView address_edit;
        public TextView address_delete;
        public ViewHolder(View itemView) {
            super(itemView);
            name_tv= (TextView) itemView.findViewById(R.id.name_tv);
            phone_tv= (TextView) itemView.findViewById(R.id.phone_tv);
            address_tv= (TextView) itemView.findViewById(R.id.address_tv);
            default_checkBox= (CheckBox) itemView.findViewById(R.id.default_checkBox);
            address_edit= (TextView) itemView.findViewById(R.id.address_edit);
            address_delete= (TextView) itemView.findViewById(R.id.address_delete);
        }
    }
    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
