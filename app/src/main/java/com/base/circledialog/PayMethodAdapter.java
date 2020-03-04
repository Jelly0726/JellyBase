package com.base.circledialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.BaseAdapter;
import com.base.Utils.StringUtil;
import com.base.model.PayMothod;
import com.jelly.jellybase.R;

import java.util.List;

/**
 * 支付方式
 */
public class PayMethodAdapter extends BaseAdapter<PayMethodAdapter.ViewHolder> {
    private List<PayMothod> mDataList;
    private ViewHolder couponVH;
    private int mSelectedPos = -1;
    private OnCheckListen onCheckListen;
    public PayMethodAdapter(Context context) {
        super(context);
    }

    @Override
    public void notifyDataSetChanged(List dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.pay_method_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        PayMothod payMothod = mDataList.get(position);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                //实现单选方法三： RecyclerView另一种定向刷新方法：不会有白光一闪动画 也不会重复onBindVIewHolder
                if (couponVH != null) {//还在屏幕里
                    couponVH.checkbox.setChecked(false);
                } else {
                    //add by 2016 11 22 for 一些极端情况，holder被缓存在Recycler的cacheView里，
                    //此时拿不到ViewHolder，但是也不会回调onBindViewHolder方法。所以add一个异常处理
                    if (mSelectedPos != -1)
                        notifyItemChanged(mSelectedPos);
                }
                if (mSelectedPos != -1)
                    mDataList.get(mSelectedPos).setCheck(false); //不管在不在屏幕里 都需要改变数据
                //设置新Item的勾选状态
                couponVH = holder;
                mSelectedPos = position;
                mDataList.get(mSelectedPos).setCheck(true);
                holder.checkbox.setChecked(true);
                if (onCheckListen != null)
                    onCheckListen.onChecked(position);
            }
        });
        holder.payName.setText(payMothod.getName());
        holder.checkbox.setChecked(payMothod.isCheck());
        if (StringUtil.isEmpty(payMothod.getMark())){
            holder.mark.setVisibility(View.GONE);
            holder.payName.setGravity(Gravity.CENTER_VERTICAL);
        }else{
            holder.mark.setText(payMothod.getMark());
            holder.mark.setVisibility(View.VISIBLE);
            holder.payName.setGravity(Gravity.BOTTOM);
        }
        holder.icon.setImageDrawable(getContext().getResources().getDrawable(payMothod.getIcon()));
        if (payMothod.isCheck() && onCheckListen != null) {
            onCheckListen.onChecked(position);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null? 0 : mDataList.size();
    }

    public OnCheckListen getOnCheckListen() {
        return onCheckListen;
    }

    public void setOnCheckListen(OnCheckListen onCheckListen) {
        this.onCheckListen = onCheckListen;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public CheckBox checkbox;
        public TextView payName;
        public TextView mark;
        public ViewHolder(View itemView){
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            checkbox = itemView.findViewById(R.id.select_box);
            payName = itemView.findViewById(R.id.payName);
            mark = itemView.findViewById(R.id.mark);
        }

    }
    interface OnCheckListen {
        public void  onChecked(int position);
    }
}
