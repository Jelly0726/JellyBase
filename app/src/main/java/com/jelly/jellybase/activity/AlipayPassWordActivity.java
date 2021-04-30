package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.passwordView.Callback;
import com.jelly.baselibrary.passwordView.PasswordKeypad;
import com.jelly.jellybase.databinding.AlipayPasswordActivityBinding;

/**
 * Created by JELLY on 2017/11/3.
 */

public class AlipayPassWordActivity extends BaseActivity<AlipayPasswordActivityBinding> {
    private PasswordKeypad mKeypad;
    private boolean state;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
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
        getBinding().button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeypad.show(getSupportFragmentManager(), "PasswordKeypad");
            }
        });
    }
}
