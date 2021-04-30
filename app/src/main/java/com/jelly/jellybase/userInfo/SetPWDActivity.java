package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.base.BaseApplication;
import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.base.sqldao.LoginDaoUtils;
import com.jelly.baselibrary.appManager.AppSubject;
import com.jelly.baselibrary.config.IntentAction;
import com.jelly.baselibrary.encrypt.MD5;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.password.PwdCheckUtil;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.UserSetpwdActivityBinding;
import com.jelly.mvp.contact.SetPwdContact;
import com.jelly.mvp.presenter.SetPassWordActivityPresenter;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/9/28.
 */

public class SetPWDActivity extends BaseActivityImpl<SetPwdContact.View
        ,SetPwdContact.Presenter, UserSetpwdActivityBinding>
        implements SetPwdContact.View , View.OnClickListener {

    private String phone;
    private String password;
    private String password1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phone=getIntent().getStringExtra("phone");
        getBinding().leftBack.setOnClickListener(this);
        getBinding().nextTv.setOnClickListener(this);
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
                String password= getBinding().passwordEdit.getText().toString().trim();
                String password1= getBinding().password1Edit.getText().toString().trim();
                if (TextUtils.isEmpty(password)||TextUtils.isEmpty(password1)){
                    ToastUtils.showToast(this,"请输入密码!");
                    return;
                }
                if (!PwdCheckUtil.validPwd4(password)){
                    ToastUtils.showShort(this,"请输入6-16位非纯数字、字母、符号新密码!");
                    return;
                }
                if (!password.equals(password1)){
                    ToastUtils.showToast(this,"两次密码输入不一致,请重新输入!");
                    return;
                }
                presenter.setPassword();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public SetPwdContact.Presenter initPresenter() {
        return new SetPassWordActivityPresenter();
    }
    @Override
    public SetPwdContact.View initIBView() {
        return this;
    }

    @Override
    public <T> ObservableTransformer<T, T> bindLifecycle() {
        return lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY);
    }
    @Override
    public Object getSetPassWordParam() {
        String password= getBinding().passwordEdit.getText().toString().trim();
        password= MD5.MD5Encode(password);
        Map map=new TreeMap();
        map.put("salesphone",phone);
        map.put("password",password);
        return map;
    }

    @Override
    public void excuteSuccess( Object mCallBackVo) {
        LoginDaoUtils.getInstance(BaseApplication.getInstance()).clear();
        AppSubject.getInstance().detachAll();
        String password= getBinding().passwordEdit.getText().toString().trim();
        Intent intent = new Intent();
        //intent.setClass(this, LoginActivity.class);
        intent.putExtra("phone",phone);
        intent.putExtra("password",password);
        intent.putExtra("from",0);
        intent.setAction(IntentAction.ACTION_LOGIN);
        startActivity(intent);
        finish();
    }

    @Override
    public void excuteFailed( String message) {
        ToastUtils.showToast(this,message);
    }
}
