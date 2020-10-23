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
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.appManager.BaseApplication;
import com.base.model.Message;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.recyclerview.BaseRecyclerAdapter;
import com.jelly.jellybase.R;
import com.jelly.jellybase.activity.MessageDetailsActivity;

import java.util.List;

public class MessageAdapter extends BaseRecyclerAdapter<MessageAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private List<Message> mList;

    public MessageAdapter(Context context, List<Message> mList) {
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
        final View view = mInflater.inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, boolean isItem) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message=mList.get((Integer) v.getTag());
                Intent intent=new Intent(BaseApplication.getInstance(), MessageDetailsActivity.class);
                intent.putExtra("message",message);
                context.startActivity(intent);
            }
        });

    }
    /**
     * item的ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title_tv;
        TextView content_tv;
        TextView time_tv;
        public ViewHolder(View itemView) {
            super(itemView);
            title_tv=itemView.findViewById(R.id.title_tv);
            content_tv=itemView.findViewById(R.id.content_tv);
            time_tv=itemView.findViewById(R.id.time_tv);
        }
    }
    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
