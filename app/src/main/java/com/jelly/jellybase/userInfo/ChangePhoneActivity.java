package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.jelly.baselibrary.encrypt.MD5;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.UserChangephoneActivityBinding;
import com.jelly.mvp.contact.UpdatePhoneContact;
import com.jelly.mvp.presenter.UpdatePhonePresenter;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2017/9/27.
 */

public class ChangePhoneActivity extends BaseActivityImpl<UpdatePhoneContact.View
        ,UpdatePhoneContact.Presenter, UserChangephoneActivityBinding>
        implements UpdatePhoneContact.View , View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
        initCountDownBtn();
    }
    @Override
    public void onBackPressed() {
        closeProgress();
        super.onBackPressed();
    }
    private void iniView(){
        getBinding().leftBack.setOnClickListener(this);
        getBinding().okTv.setOnClickListener(this);
    }
    private void initCountDownBtn() {
        getBinding().btnGetVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone= getBinding().phoneEdit.getText().toString().trim();
                if (TextUtils.isEmpty(phone))
                {
                    ToastUtils.showToast(ChangePhoneActivity.this,"请输入手机号");
                    return;
                }
                presenter.getVerifiCode();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getBinding().btnGetVer.onDestroy();
    }

    @Override
    public UpdatePhoneContact.Presenter initPresenter() {
        return new UpdatePhonePresenter();
    }
    @Override
    public UpdatePhoneContact.View initIBView() {
        return this;
    }

    @Override
    public LifecycleOwner bindLifecycle() {
        return this;
    }
    public void onClick(View v) {
        if (AntiShake.check(v.getId())) {    //判断是否多次点击
            return;
        }
        Intent intent;
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.ok_tv:
                String phone= getBinding().phoneEdit.getText().toString().trim();
                String pw= getBinding().passwordEdit.getText().toString().trim();
                String vcode= getBinding().verificationCodeEdit.getText().toString().trim();
                if (TextUtils.isEmpty(phone)||TextUtils.isEmpty(pw)||
                        TextUtils.isEmpty(vcode)){
                    ToastUtils.showToast(this,"手机号、验证码、密码不能为空！");
                    return;
                }
                presenter.updatePhone();
                break;
        }
    }

    @Override
    public Object getVerifiCodeParam() {
        String phone= getBinding().phoneEdit.getText().toString().trim();
        Map map=new TreeMap<>();
        map.put("phone",phone);
        map.put("flag",3);//验证码标识：1注册，2忘记密码，3修改手机号
        return map;
    }

    @Override
    public void verifiCodeSuccess( Object mCallBackVo) {
        HttpResult httpResultAll= (HttpResult)mCallBackVo;
        ToastUtils.showToast(this,httpResultAll.getMsg());
        getBinding().btnGetVer.setStartCountDownText("再次获取");//设置倒计时开始时按钮上的显示文字
        getBinding().btnGetVer.startCountDownTimer(60000,1000);//设置倒计时时间，间隔
    }

    @Override
    public void verifiCodeFailed(String message) {
        ToastUtils.showToast(this,message);
    }

    @Override
    public Object getUpdatePhoneParam() {
        String phone= getBinding().phoneEdit.getText().toString().trim();
        String pw= getBinding().passwordEdit.getText().toString().trim();
        pw= MD5.MD5Encode(pw);
        String vcode= getBinding().verificationCodeEdit.getText().toString().trim();
        Map<String,String> map=new TreeMap<>();
        map.put("phone",phone);
        map.put("vericode",vcode);
        map.put("password",pw);
        return map;
    }

    @Override
    public void updatePhoneSuccess( Object mCallBackVo) {
        ToastUtils.showToast(this, (String) mCallBackVo);
        finish(2000);
    }

    @Override
    public void updatePhoneFailed(String message) {
        ToastUtils.showToast(this,message);
    }
}
