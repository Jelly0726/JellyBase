package com.base.circledialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.passwordView.PasswordEditText;
import com.jelly.jellybase.R;


/**
 * 支付密码输入框
 * Created by hupei on 2017/4/5.
 */
public class PayPassWordDialog extends BaseCircleDialog implements View.OnClickListener {
    //确定
    private TextView confirm_tv;
    //取消
    private TextView cancle_tv;
    //余额
    private LinearLayout balance_layout;
    private int balanceVisibility = View.VISIBLE;
    //微信
    private LinearLayout weixin_layout;
    private int weixinVisibility = View.VISIBLE;
    //支付宝
    private LinearLayout alipay_layout;
    private int alipayVisibility = View.VISIBLE;
    //银联
    private LinearLayout unionpay_layout;
    private int unionpayVisibility = View.VISIBLE;

    //选中的支付方式
    private int selectId = -1;
    //密码输入框
    private PasswordEditText mPswEditText;

    private OnConfirmListener onConfirmListener;

    public static PayPassWordDialog getInstance() {
        PayPassWordDialog dialogFragment = new PayPassWordDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(1f);
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.payment_password_dialog, container, false);
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

        weixin_layout = (LinearLayout) view.findViewById(R.id.weixin_layout);
        weixin_layout.setVisibility(weixinVisibility);

        alipay_layout = (LinearLayout) view.findViewById(R.id.alipay_layout);
        alipay_layout.setVisibility(alipayVisibility);

        unionpay_layout = (LinearLayout) view.findViewById(R.id.unionpay_layout);
        unionpay_layout.setVisibility(unionpayVisibility);

        mPswEditText = (PasswordEditText) view.findViewById(R.id.mPswEditText);
        mPswEditText.setOnPasswordChangeListener(new PasswordEditText.OnPasswordChangeListener() {
            @Override
            public void onPasswordChange(String password) {
                //tvShow.setText(password);
                if (password.length() == 6) {

                }
            }
        });

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
                selectId = PaymentDialog.BALANCE;
                if (onConfirmListener != null) {
                    onConfirmListener.OnConfirm(selectId);
                }
                dismiss();
                break;
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
}
