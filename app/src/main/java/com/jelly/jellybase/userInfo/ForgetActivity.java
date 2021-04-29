package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.UserForgetActivityBinding;
import com.jelly.mvp.contact.ForgetPwdContact;
import com.jelly.mvp.presenter.ForgetPasswordPresenter;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/9/28.
 */

public class ForgetActivity extends BaseActivityImpl<ForgetPwdContact.View
        ,ForgetPwdContact.Presenter, UserForgetActivityBinding>
        implements ForgetPwdContact.View, View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
        initCountDownBtn();
    }
    private void iniView(){
        getViewBinding().leftBack.setOnClickListener(this);
        getViewBinding().nextTv.setOnClickListener(this);
        getViewBinding().btnGetVer.setOnClickListener(this);
    }
    private void initCountDownBtn() {
        getViewBinding().btnGetVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=getViewBinding().phoneEdit.getText().toString().trim();
                if (TextUtils.isEmpty(phone))
                {
                    ToastUtils.showToast(ForgetActivity.this,"请输入手机号");
                    return;
                }
                presenter.getVerifiCode();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getViewBinding().btnGetVer.onDestroy();
    }

    @Override
    public ForgetPwdContact.Presenter initPresenter() {
        return new ForgetPasswordPresenter();
    }
    @Override
    public ForgetPwdContact.View initIBView() {
        return this;
    }

    @Override
    public <T> ObservableTransformer<T, T> bindLifecycle() {
        return lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY);
    }
    public void onClick(View v) {
        if (AntiShake.check(v.getId())) {    //判断是否多次点击
            return;
        }
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.next_tv:
                String phone=getViewBinding().phoneEdit.getText().toString().trim();
                String verificationCode=getViewBinding().verificationCodeEdit.getText().toString().trim();
                if (TextUtils.isEmpty(phone)||TextUtils.isEmpty(verificationCode))
                {
                    ToastUtils.showToast(ForgetActivity.this,"请输入手机号和验证码");
                    return;
                }
                presenter.forgetPwd();
                break;
        }
    }

    @Override
    public Object getVerifiCodeParam() {
        String phone=getViewBinding().phoneEdit.getText().toString().trim();
        Map map=new TreeMap<>();
        map.put("phone",phone);
        map.put("flag",2);//验证码标识：1注册，2忘记密码，3修改手机号
        return map;
    }

    @Override
    public void verifiCodeSuccess( Object mCallBackVo) {
        HttpResult httpResultAll= (HttpResult)mCallBackVo;
        ToastUtils.showToast(this,httpResultAll.getMsg());
        getViewBinding().btnGetVer.setStartCountDownText("再次获取");//设置倒计时开始时按钮上的显示文字
        getViewBinding().btnGetVer.startCountDownTimer(60000,1000);//设置倒计时时间，间隔
    }

    @Override
    public void verifiCodeFailed( String message) {
        ToastUtils.showToast(this,message);
    }

    @Override
    public Object forgetPasswordParam() {
        String phone=getViewBinding().phoneEdit.getText().toString().trim();
        String verificationCode=getViewBinding().verificationCodeEdit.getText().toString().trim();
        Map map=new TreeMap<>();
        map.put("phone",phone);
        map.put("vericode",verificationCode);
        return map;
    }

    @Override
    public void forgetPasswordSuccess( Object mCallBackVo) {
        String phone=getViewBinding().phoneEdit.getText().toString().trim();
        Intent intent=new Intent(this,RefeshSetPWDActivity.class);
        intent.putExtra("phone",phone);
        startActivity(intent);
    }

    @Override
    public void forgetPasswordFailed(String message) {
        ToastUtils.showToast(this,message);
    }
}
