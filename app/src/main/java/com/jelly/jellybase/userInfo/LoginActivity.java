package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.base.applicationUtil.AppPrefs;
import com.base.applicationUtil.MD5;
import com.base.applicationUtil.MyApplication;
import com.base.applicationUtil.ToastUtils;
import com.base.config.ConfigKey;
import com.base.config.IntentAction;
import com.base.httpmvp.contact.LoginContact;
import com.base.httpmvp.presenter.LoginActivityPresenter;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.httpmvp.retrofitapi.token.TokenModel;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.jiguang.TagAliasOperatorHelper;
import com.base.multiClick.AntiShake;
import com.jelly.jellybase.R;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import systemdb.Login;

/**
 * Created by Administrator on 2017/9/18.
 */

public class LoginActivity extends BaseActivityImpl<LoginContact.Presenter>
        implements LoginContact.View {
    @BindView(R.id.login_tv)
    TextView login_tv;
    @BindView(R.id.forget_pwd)
    TextView forget_pwd;
    @BindView(R.id.register_account)
    TextView register_account;
    private String phone="";
    private String password;
    private int from=-1;
    @BindView(R.id.phone_edit)
    EditText phone_edit;
    @BindView(R.id.password_edit)
    EditText password_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_activity);
        ButterKnife.bind(this);
        iniView();
        phone=getIntent().getStringExtra("phone");
        password=getIntent().getStringExtra("password");
        from=getIntent().getIntExtra("from",-1);
    }
    private void iniView (){
    }
    private void iniData(){
        Log.i("ss","phone="+phone);
        Log.i("ss","password="+password);
        Log.i("ss","from="+from);
        if (!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(password)&&from==0){
            phone_edit.setText(phone);
            phone_edit.setSelection(phone.length());
            password_edit.setText(password);
            password_edit.setSelection(password.length());
            presenter.userLogin(true,lifecycleProvider
                    .<Long>bindUntilEvent(ActivityEvent.DESTROY));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        iniData();
    }

    @Override
    public LoginContact.Presenter initPresenter() {
        return new LoginActivityPresenter(this);
    }

    @OnClick({R.id.login_tv,R.id.forget_pwd,R.id.register_account})
    public void onClick(View v) {
        if (AntiShake.check(v.getId())) {    //判断是否多次点击
            return;
        }
        Intent intent;
        switch (v.getId()){
            case R.id.login_tv:
                phone=phone_edit.getText().toString().trim();
                password=password_edit.getText().toString().trim();
                if (TextUtils.isEmpty(phone)||TextUtils.isEmpty(password))
                {
                    ToastUtils.showToast(this,"请输入您的手机号和密码!");
                    return;
                }
                presenter.userLogin(true,lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
                break;
            case R.id.forget_pwd:
                intent=new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent);
                break;
            case R.id.register_account:
                intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApplication.getMyApp().exit();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public Object getLoginParam() {
        password= MD5.MD5Encode(password);
        Map map=new TreeMap();
        map.put("salesphone",phone);
        map.put("password",password);
        return map;
    }

    @Override
    public void loginSuccess(boolean isRefresh, Object mCallBackVo) {
        Login login= (Login) mCallBackVo;

        //↓↓↓↓↓↓极光设置tag↓↓↓↓↓↓↓
        if (!AppPrefs.getBoolean(MyApplication.getMyApp(), ConfigKey.IS_SET_TAG,false)){
            if (!TextUtils.isEmpty(login.getCompanyno())){
                Set<String> tagSet = new LinkedHashSet<String>();
                tagSet.add(login.getCompanyno());
                TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
                tagAliasBean.action = TagAliasOperatorHelper.ACTION_SET;
                tagAliasBean.tags = tagSet;
                tagAliasBean.isAliasAction = false;
                TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(),0,tagAliasBean);
            }
        }
        //↑↑↑↑↑↑极光设置tag↑↑↑↑↑↑

        TokenModel tokenModel=new TokenModel();
        tokenModel.setTokenExpirationTime(login.getTokenExpirationTime());
        tokenModel.setToken(login.getToken());
        tokenModel.setCreateTime(login.getCreateTime());
        GlobalToken.updateToken(tokenModel);

        Intent intent=new Intent();
        intent.setAction(IntentAction.ACTION_MAIN);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginFailed(boolean isRefresh, String message) {
        ToastUtils.showToast(this,message);
    }
}
