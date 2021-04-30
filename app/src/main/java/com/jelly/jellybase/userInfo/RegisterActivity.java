package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.webview.BaseWebViewActivity;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.baselibrary.webview.WebConfig;
import com.jelly.baselibrary.webview.WebTools;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.UserRegisterActivityBinding;
import com.jelly.mvp.contact.RegisterContact;
import com.jelly.mvp.presenter.RegisterActivityPresenter;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/9/28.
 */

public class RegisterActivity extends BaseActivityImpl<RegisterContact.View
        ,RegisterContact.Presenter, UserRegisterActivityBinding>
        implements RegisterContact.View , View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
        initCountDownBtn();
    }
    private void iniView(){
        getBinding().agree.setChecked(false);
        getBinding().leftBack.setOnClickListener(this);
        getBinding().nextTv.setOnClickListener(this);
        getBinding().loginTv.setOnClickListener(this);
       getBinding().clause.setOnClickListener(this);
    }
    private void initCountDownBtn() {
        getBinding().btnGetVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone= getBinding().phoneEdit.getText().toString().trim();
                if (TextUtils.isEmpty(phone))
                {
                    ToastUtils.showToast(RegisterActivity.this,"请输入手机号");
                    return;
                }
                presenter.getVerifiCode();
            }
        });
    }
    @Override
    protected void onDestroy() {
        if ( getBinding().btnGetVer!=null) {
            getBinding().btnGetVer.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public RegisterContact.Presenter initPresenter() {
        return new RegisterActivityPresenter();
    }
    @Override
    public RegisterContact.View initIBView() {
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
        Intent intent;
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.login_tv:
                finish();
                break;
            case R.id.next_tv:
                String phone= getBinding().phoneEdit.getText().toString().trim();
                String verificationCode= getBinding().verificationCodeEdit.getText().toString().trim();
                if (TextUtils.isEmpty(phone)||TextUtils.isEmpty(verificationCode))
                {
                    ToastUtils.showToast(RegisterActivity.this,"请输入手机号和验证码");
                    return;
                }
                if (getBinding().agree.isChecked())
                {
                    presenter.userRegister();
                }else {
                    ToastUtils.showToast(RegisterActivity.this,"请先阅读服务协议，并同意！");
                }
                break;
            case R.id.clause:
                intent=new Intent(RegisterActivity.this,BaseWebViewActivity.class);
                WebTools webTools=new WebTools();
                webTools.url="https://www.baidu.com/";
                webTools.title="服务协议";
                intent.putExtra(WebConfig.CONTENT,webTools);
                startActivity(intent);
                getBinding().agree.setChecked(true);
                break;
        }
    }
    @Override
    public Object getRegParam() {
        String phone= getBinding().phoneEdit.getText().toString().trim();
        String verificationCode= getBinding().verificationCodeEdit.getText().toString().trim();
        Map map=new TreeMap();
        map.put("account",phone);
        map.put("vericode",verificationCode);
        return map;
    }

    @Override
    public void excuteSuccess( Object mCallBackVo) {
        String phone= getBinding().phoneEdit.getText().toString().trim();
        Intent intent=new Intent(RegisterActivity.this,SetPWDActivity.class);
        intent.putExtra("phone",phone);
        startActivity(intent);
    }

    @Override
    public void excuteFailed( String message) {
        ToastUtils.showToast(this,message);
    }

    @Override
    public Object getVerifiCodeParam() {
        String phone= getBinding().phoneEdit.getText().toString().trim();
        Map map=new TreeMap<>();
        map.put("phone",phone);
        map.put("flag",1);//验证码标识：1注册，2忘记密码，3修改手机号
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
    public void verifiCodeFailed( String message) {
        ToastUtils.showToast(this,message);
    }
}
