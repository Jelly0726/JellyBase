package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.jelly.baselibrary.encrypt.MD5;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.password.PwdCheckUtil;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.UserChangepwdActivityBinding;
import com.jelly.mvp.contact.UpdataPwdContact;
import com.jelly.mvp.presenter.UpdatePasswordPresenter;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/9/27.
 */

public class ChangePWDActivity extends BaseActivityImpl<UpdataPwdContact.View
        ,UpdataPwdContact.Presenter, UserChangepwdActivityBinding>
        implements UpdataPwdContact.View, View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    private void iniView(){
        getViewBinding().leftBack.setOnClickListener(this);
        getViewBinding().commitTv.setOnClickListener(this);
    }
    @Override
    public void onBackPressed() {
        closeProgress();
        super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public UpdataPwdContact.Presenter initPresenter() {
        return new UpdatePasswordPresenter();
    }
    @Override
    public UpdataPwdContact.View initIBView() {
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
            case R.id.commit_tv:
                String newPwd=getViewBinding().newPwd.getText().toString().trim();
                String newPwd1=getViewBinding().newPwd1.getText().toString().trim();
                if (!PwdCheckUtil.validPwd4(newPwd)){
                    ToastUtils.showShort(this,"请输入6-16位非纯数字、字母、符号新密码!");
                    return;
                }
                if (!newPwd.equals(newPwd1)){
                    ToastUtils.showToast(this,"两次密码不一致，请重新输入!");
                    return;
                }
                presenter.updatePassword();
                break;
        }
    }


    @Override
    public Object getUpdatePasswordParam() {
        String oldPwd=getViewBinding().oldPwd.getText().toString().trim();
        oldPwd= MD5.MD5Encode(oldPwd);
        String newPwd=getViewBinding().newPwd.getText().toString().trim();
        newPwd= MD5.MD5Encode(newPwd);
        Map<String,String> map=new TreeMap();
        map.put("password",newPwd);
        map.put("oldpassword",oldPwd);
        return map;
    }

    @Override
    public void updatePasswordSuccess(Object mCallBackVo) {
        ToastUtils.showToast(this, (String) mCallBackVo);
        finish(2000);
    }

    @Override
    public void updatePasswordFailed(String message) {
        ToastUtils.showToast(this,message);
    }
}
