package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.jelly.baselibrary.dialog.PaymentDialog;
import com.jelly.baselibrary.model.PayMothod;
import com.jelly.baselibrary.passwordView.Callback;
import com.jelly.baselibrary.passwordView.PasswordKeypad;
import com.base.BaseActivity;
import com.jelly.jellybase.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JELLY on 2017/11/3.
 */

public class PaymentActivity extends BaseActivity {
    private PasswordKeypad mKeypad;
    private boolean state;
    private List<PayMothod> mList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.payment_activity;
    }
    private void iniView(){
        mKeypad = new PasswordKeypad();
        mKeypad.setPasswordCount(6);
        mKeypad.setMsgCount(4);//短信验证码长度
        mKeypad.setKeyType(0);//0 只用支付密码，1 只用短信验证码 2使用支付密码或短信验证码
        mKeypad.setCallback(new Callback() {
            @Override
            public void onForgetPassword() {
                Toast.makeText(getApplicationContext(),"忘记密码",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onInputCompleted(boolean type,CharSequence password) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (state) {
                            mKeypad.setPasswordState(true);
                            state = false;
                        } else {
                            mKeypad.setPasswordState(false, "密码输入错误");
                            state = true;
                        }
                    }
                },1000);
            }

            @Override
            public void onPasswordCorrectly() {
                mKeypad.dismiss();
            }

            @Override
            public void onCancel() {
                //todo:做一些埋点类的需求
            }
        });
        mList=new ArrayList<>();
        mList.add(new PayMothod().setName("余额").setPayType(0).setIcon(R.mipmap.payment_zhanghu).setMark("¥0"));
        mList.add(new PayMothod().setName("微信").setPayType(1).setIcon(R.mipmap.payment_weixin).setMark("推荐使用"));
        mList.add(new PayMothod().setName("支付宝").setPayType(2).setIcon(R.mipmap.payment_zhifubao).setMark(""));
        mList.add(new PayMothod().setName("银联").setPayType(3).setIcon(R.mipmap.payment_yinglian).setMark(""));
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentDialog paymentDialog=PaymentDialog.getInstance();
                paymentDialog.setItem(mList);
                paymentDialog.setOnConfirmListener(new PaymentDialog.OnConfirmListener() {
                    @Override
                    public void OnConfirm(int payment) {
                        if(payment==0){
                            mKeypad.show(getSupportFragmentManager(), "PasswordKeypad");
                        }
                    }
                });
                paymentDialog.show(getSupportFragmentManager(),"paymentDialog");

            }
        });
    }
}
