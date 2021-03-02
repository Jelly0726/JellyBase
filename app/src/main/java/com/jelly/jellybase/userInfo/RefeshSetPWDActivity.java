package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.appManager.AppSubject;
import com.base.appManager.BaseApplication;
import com.base.config.IntentAction;
import com.base.encrypt.MD5;
import com.base.httpmvp.mvpContact.SetPwdContact;
import com.base.httpmvp.mvpPresenter.SetPassWordActivityPresenter;
import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.base.multiClick.AntiShake;
import com.base.password.PwdCheckUtil;
import com.base.sqldao.LoginDaoUtils;
import com.base.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/9/28.
 */

public class RefeshSetPWDActivity extends BaseActivityImpl<SetPwdContact.View,SetPwdContact.Presenter>
        implements SetPwdContact.View {

    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.next_tv)
    TextView next_tv;
    @BindView(R.id.password_edit)
    EditText password_edit;
    @BindView(R.id.password1_edit)
    EditText password1_edit;
    private String phone;
    private String password;
    private String password1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phone=getIntent().getStringExtra("phone");
    }
    @Override
    public int getLayoutId(){
        return R.layout.user_setpwd_activity;
    }
    @OnClick({ R.id.next_tv, R.id.left_back})
    public void onClick(View v) {
        if (AntiShake.check(v.getId())) {    //判断是否多次点击
            return;
        }
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.next_tv:
                String password=password_edit.getText().toString().trim();
                String password1=password1_edit.getText().toString().trim();
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
        String password=password_edit.getText().toString().trim();
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
        String password=password_edit.getText().toString().trim();
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
