package com.base.circledialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jelly.jellybase.R;


/**
 * 支付方式选择框
 * Created by hupei on 2017/4/5.
 */
public class PaymentDialog extends BaseCircleDialog implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    //余额支付
    public static final int BALANCE = 0;
    //微信支付
    public static final int WECHAT = 1;
    //支付宝支付
    public static final int ALIPAY = 2;
    //银联支付
    public static final int UNIONPAY = 3;
    //确定
    private TextView confirm_tv;
    //取消
    private TextView cancle_tv;
    //余额
    private LinearLayout balance_layout;
    private TextView balance_title;
    private TextView balance_tv;
    private CheckBox balance_cb;
    private int balanceVisibility = View.VISIBLE;
    //微信
    private LinearLayout weixin_layout;
    private CheckBox weixin_cb;
    private int weixinVisibility = View.VISIBLE;
    //支付宝
    private LinearLayout alipay_layout;
    private CheckBox alipay_cb;
    private int alipayVisibility = View.VISIBLE;
    //银联
    private LinearLayout unionpay_layout;
    private CheckBox unionpay_cb;
    private int unionpayVisibility = View.VISIBLE;
    //账户余额
    private float balance = 0;
    //需支付金额，用以判断余额是否充足
    private float amount = 0;
    //支付方式组
    private int[] payment = new int[4];
    //选中的支付方式
    private int selectId = -1;

    private OnConfirmListener onConfirmListener;

    public static PaymentDialog getInstance() {
        PaymentDialog dialogFragment = new PaymentDialog();
        dialogFragment.setCanceledBack(false);
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

        balance_layout = (LinearLayout) view.findViewById(R.id.balance_layout);
        balance_layout.setVisibility(balanceVisibility);
        balance_title = (TextView) view.findViewById(R.id.balance_title);
        balance_tv = (TextView) view.findViewById(R.id.balance_tv);
        balance_tv.setText("￥" + balance);
        balance_cb = (CheckBox) view.findViewById(R.id.balance_cb);
        payment[0] = R.id.balance_cb;
        balance_cb.setOnCheckedChangeListener(this);

        weixin_layout = (LinearLayout) view.findViewById(R.id.weixin_layout);
        weixin_layout.setVisibility(weixinVisibility);
        weixin_cb = (CheckBox) view.findViewById(R.id.weixin_cb);
        payment[1] = R.id.weixin_cb;
        weixin_cb.setOnCheckedChangeListener(this);

        alipay_layout = (LinearLayout) view.findViewById(R.id.alipay_layout);
        alipay_layout.setVisibility(alipayVisibility);
        alipay_cb = (CheckBox) view.findViewById(R.id.alipay_cb);
        payment[2] = R.id.alipay_cb;
        alipay_cb.setOnCheckedChangeListener(this);

        unionpay_layout = (LinearLayout) view.findViewById(R.id.unionpay_layout);
        unionpay_layout.setVisibility(unionpayVisibility);
        unionpay_cb = (CheckBox) view.findViewById(R.id.unionpay_cb);
        payment[3] = R.id.unionpay_cb;
        unionpay_cb.setOnCheckedChangeListener(this);

        if (amount > balance) {
            balance_tv.setText("￥" + balance + "，余额不足");
            balance_cb.setEnabled(false);
            //设置字体颜色
            balance_title.setTextColor(Color.parseColor("#727272"));
            //设置字体和粗细、斜体
            Typeface font = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC);
            balance_title.setTypeface(font);
            //设置字体颜色
            balance_tv.setTextColor(Color.parseColor("#727272"));
            //设置字体和粗细、斜体
            balance_tv.setTypeface(font);

            weixin_cb.setChecked(true);
            selectId = R.id.weixin_cb;
        } else {
            balance_cb.setChecked(true);
            selectId = R.id.balance_cb;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancle_tv://取消
                dismiss();
                break;
            case R.id.confirm_tv://确定
                selectId = -1;
                for (int id : payment) {
                    CheckBox tempButton = (CheckBox) getView().findViewById(id);
                    if (tempButton.isChecked()) {
                        selectId = id;
                    }
                }
                if (selectId == -1) {
                    Toast.makeText(this.getActivity(), "没有选中", Toast.LENGTH_SHORT).show();
                } else {
                    if (selectId == R.id.balance_cb) {//余额支付
                        selectId = BALANCE;
                    } else if (selectId == R.id.weixin_cb) {//微信支付
                        selectId = WECHAT;
                    } else if (selectId == R.id.alipay_cb) {//支付宝支付
                        selectId = ALIPAY;
                    } else if (selectId == R.id.unionpay_cb) {//银联支付
                        selectId = UNIONPAY;
                    }
                }
                if (onConfirmListener != null) {
                    onConfirmListener.OnConfirm(selectId);
                }
                dismiss();
                break;
        }
    }

    /**
     * CheckBox变化监听
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            for (int id : payment) {
                CheckBox tempButton = (CheckBox) getView().findViewById(id);
                if (id == buttonView.getId()) {
                    tempButton.setChecked(true);
                } else {
                    tempButton.setChecked(false);
                }
            }
        } else {
            selectId = -1;
            for (int id : payment) {
                CheckBox tempButton = (CheckBox) getView().findViewById(id);
                if (tempButton.isChecked()) {
                    selectId = 1;
                }
            }
            if (selectId == -1) {
                buttonView.setChecked(true);
            } else {
                buttonView.setChecked(false);
            }

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

    public int getBalanceVisibility() {
        return balanceVisibility;
    }

    public void setBalanceVisibility(int balanceVisibility) {
        this.balanceVisibility = balanceVisibility;
    }

    public int getWeixinVisibility() {
        return weixinVisibility;
    }

    public void setWeixinVisibility(int weixinVisibility) {
        this.weixinVisibility = weixinVisibility;
    }

    public int getAlipayVisibility() {
        return alipayVisibility;
    }

    public void setAlipayVisibility(int alipayVisibility) {
        this.alipayVisibility = alipayVisibility;
    }

    public int getUnionpayVisibility() {
        return unionpayVisibility;
    }

    public void setUnionpayVisibility(int unionpayVisibility) {
        this.unionpayVisibility = unionpayVisibility;
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
