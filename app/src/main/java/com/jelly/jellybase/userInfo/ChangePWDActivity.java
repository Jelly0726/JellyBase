package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.encrypt.MD5;
import com.base.httpmvp.contact.UpdataPwdContact;
import com.base.httpmvp.presenter.UpdatePasswordPresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.multiClick.AntiShake;
import com.base.password.PwdCheckUtil;
import com.base.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/9/27.
 */

public class ChangePWDActivity extends BaseActivityImpl<UpdataPwdContact.Presenter>
        implements UpdataPwdContact.View {
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.old_pwd)
    EditText old_pwd;
    @BindView(R.id.new_pwd)
    EditText new_pwd;
    @BindView(R.id.new_pwd1)
    EditText new_pwd1;
    @BindView(R.id.commit_tv)
    TextView commit_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.user_changepwd_activity;
    }
    private void iniView(){
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
        return new UpdatePasswordPresenter(this);
    }

    @OnClick({R.id.left_back,R.id.commit_tv})
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
                String newPwd=new_pwd.getText().toString().trim();
                String newPwd1=new_pwd1.getText().toString().trim();
                if (!PwdCheckUtil.validPwd4(newPwd)){
                    ToastUtils.showShort(this,"请输入6-16位非纯数字、字母、符号新密码!");
                    return;
                }
                if (!newPwd.equals(newPwd1)){
                    ToastUtils.showToast(this,"两次密码不一致，请重新输入!");
                    return;
                }
                presenter.updatePassword(lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
                break;
        }
    }


    @Override
    public Object getUpdatePasswordParam() {
        String oldPwd=old_pwd.getText().toString().trim();
        oldPwd= MD5.MD5Encode(oldPwd);
        String newPwd=new_pwd.getText().toString().trim();
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
