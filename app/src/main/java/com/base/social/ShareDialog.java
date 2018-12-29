package com.base.social;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.base.circledialog.BaseCircleDialog;
import com.shadow.seewashed.R;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * 分享选择框
 * Created by hupei on 2017/4/5.
 */
public class ShareDialog extends BaseCircleDialog {

    private OnConfirmListener onConfirmListener;
    private SwipeMenuRecyclerView mRecyclerView;
    private ShareAdapter adapter;
    private List<ShareItem>  items=new ArrayList<>();
    private ShareItem shareItem;
    public static ShareDialog getInstance() {
        ShareDialog dialogFragment = new ShareDialog();
        dialogFragment.setCanceledBack(true);
        dialogFragment.setCanceledOnTouchOutside(true);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setDimEnabled(true);
        dialogFragment.setWidth(1f);
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        SwipeMenuRecyclerView mRecyclerView=new SwipeMenuRecyclerView(context);
        LinearLayout.LayoutParams lpy = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mRecyclerView.setLayoutParams(lpy);

        mRecyclerView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        linearLayout.addView(mRecyclerView);
        return linearLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayout view = (LinearLayout) getView();
        adapter=new ShareAdapter(getActivity(),items);
        mRecyclerView= (SwipeMenuRecyclerView) view.getChildAt(0);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(getActivity()
                , R.color.white)));
        mRecyclerView.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                shareItem=items.get(position);
                if (onConfirmListener != null) {
                    onConfirmListener.OnConfirm(shareItem);
                }
                dismiss();
            }
        });
        mRecyclerView.setAdapter(adapter);
    }
    public void add(int index,ShareItem item){
        items.add(index,item);
        if (adapter!=null)
        adapter.notifyDataSetChanged();
    }
    public void add(ShareItem item){
        items.add(item);
        if (adapter!=null)
        adapter.notifyDataSetChanged();
    }
    public void remove(ShareItem item){
        items.remove(item);
        if (adapter!=null)
        adapter.notifyDataSetChanged();
    }
    public void remove(int position){
        items.remove(position);
        if (adapter!=null)
        adapter.notifyDataSetChanged();
    }
    public void clear(){
        items.clear();
        if (adapter!=null)
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    public OnConfirmListener getOnConfirmListener() {
        return onConfirmListener;
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    /**
     * 确定按钮监听
     */
    public interface OnConfirmListener {
        void OnConfirm(ShareItem payment);
    }
}