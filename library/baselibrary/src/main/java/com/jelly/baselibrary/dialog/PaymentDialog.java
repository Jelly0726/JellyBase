package com.jelly.baselibrary.dialog;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jelly.baselibrary.BaseAdapter;
import com.jelly.baselibrary.R;
import com.jelly.baselibrary.applicationUtil.AppUtils;
import com.jelly.baselibrary.model.PayMothod;
import com.jelly.baselibrary.recyclerViewUtil.ItemDecoration;
import com.mylhyl.circledialog.AbsBaseCircleDialog;

import java.util.List;


/**
 * 支付方式选择框
 * Created by hupei on 2017/4/5.
 */
public class PaymentDialog extends AbsBaseCircleDialog implements View.OnClickListener {
    //确定
    private TextView confirm_tv;
    //取消
    private TextView cancle_tv;
    //账户余额
    private float balance = 0;
    //需支付金额，用以判断余额是否充足
    private float amount = 0;
    //选中的支付方式
    private int selectId = -1;
    private RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView.ItemDecoration mItemDecoration;
    protected BaseAdapter mAdapter;
    protected List<PayMothod> mList;
    private OnConfirmListener onConfirmListener;

    public static PaymentDialog getInstance() {
        PaymentDialog dialogFragment = new PaymentDialog();
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(1f);
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.payment_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        confirm_tv = (TextView) view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(this);
        cancle_tv = (TextView) view.findViewById(R.id.cancle_tv);
        cancle_tv.setOnClickListener(this);

        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        mLayoutManager = createLayoutManager();
        mItemDecoration = createItemDecoration();
        mAdapter = new PayMethodAdapter(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(mItemDecoration);
        ((PayMethodAdapter) mAdapter).setOnCheckListen(new PayMethodAdapter.OnCheckListen() {
            @Override
            public void onChecked(int position) {
                selectId = position;
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mList);
    }

    public void setItem(List<PayMothod> mList) {
        selectId = -1;
        this.mList = mList;
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged(mList);
    }

    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected RecyclerView.ItemDecoration createItemDecoration() {
        Rect rect = new Rect();
        rect.top = AppUtils.dipTopx(getContext().getApplicationContext(), 0f);
        rect.bottom = AppUtils.dipTopx(getContext().getApplicationContext(), 1f);
        rect.left = AppUtils.dipTopx(getContext().getApplicationContext(), 0f);
        rect.right = AppUtils.dipTopx(getContext().getApplicationContext(), 0f);
        return new ItemDecoration(rect, 1, ItemDecoration.HEAD, ContextCompat.getColor(getActivity(), R.color.main_bg));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancle_tv) {//取消
            dismiss();
        } else if (id == R.id.confirm_tv) {//确定
            if (selectId == -1) {
                Toast.makeText(this.getActivity(), "没有选中", Toast.LENGTH_SHORT).show();
            } else {
            }
            if (onConfirmListener != null) {
                onConfirmListener.OnConfirm(selectId);
            }
            dismiss();
        }
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
        void OnConfirm(int payment);
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
