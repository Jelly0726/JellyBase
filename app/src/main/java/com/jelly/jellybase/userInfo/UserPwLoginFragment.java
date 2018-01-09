package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.base.httpmvp.view.BaseFragmentImpl;
import com.base.jiguang.TagAliasOperatorHelper;
import com.base.multiClick.AntiShake;
import com.google.gson.Gson;
import com.jelly.jellybase.R;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;
import systemdb.Login;

/**
 * Created by Administrator on 2018/1/6.
 */

public class UserPwLoginFragment extends BaseFragmentImpl<LoginContact.Presenter>
        implements LoginContact.View{
    private View mRootView;
    private Unbinder unbinder;
    @BindView(R.id.login_tv)
    TextView login_tv;
    @BindView(R.id.forget_pwd)
    TextView forget_pwd;
    @BindView(R.id.phone_edit)
    EditText phone_edit;
    @BindView(R.id.password_edit)
    EditText password_edit;

    private String phone="";
    private String password;
    private double from=-1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.user_pwlogin_fragment, container, false);
        unbinder= ButterKnife.bind(this,mRootView);
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
    @Override
    public void setData(String json) {
        Map map=new Gson().fromJson(json,Map.class);
        phone= (String) map.get("phone");
        password= (String) map.get("password");
        from= (Double) map.get("from");
        if (getActivity()!=null){
            iniData();
        }
    }
    @Override
    public boolean onBackPressed() {
        return false;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniData();
    }
    private void iniData(){
        if (!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(password)&&from==0){
            phone_edit.setText(phone);
            phone_edit.setSelection(phone.length());
            password_edit.setText(password);
            password_edit.setSelection(password.length());
            presenter.userLogin(lifecycleProvider
                    .<Long>bindUntilEvent(ActivityEvent.DESTROY));
        }
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
                    ToastUtils.showToast(getContext(),"请输入您的手机号和动态密码!");
                    return;
                }
                presenter.userLogin(lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
                break;
            case R.id.forget_pwd:
                intent=new Intent(MyApplication.getMyApp(), ForgetActivity.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public LoginContact.Presenter initPresenter() {
        return new LoginActivityPresenter(this);
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
    public void loginSuccess(Object mCallBackVo) {
        Login login= (Login) mCallBackVo;

        //↓↓↓↓↓↓极光设置tag↓↓↓↓↓↓
        if (!AppPrefs.getBoolean(MyApplication.getMyApp(), ConfigKey.IS_SET_TAG,false)){
            if (!TextUtils.isEmpty(login.getCompanyno())){
                Set<String> tagSet = new LinkedHashSet<String>();
                tagSet.add(login.getCompanyno());
                TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
                tagAliasBean.action = TagAliasOperatorHelper.ACTION_SET;
                tagAliasBean.tags = tagSet;
                tagAliasBean.isAliasAction = false;
                TagAliasOperatorHelper.sequence++;
                TagAliasOperatorHelper.getInstance().handleAction(getContext().getApplicationContext(),
                        TagAliasOperatorHelper.sequence,tagAliasBean);
            }
        }
        //↑↑↑↑↑↑极光设置tag↑↑↑↑↑↑
        //↓↓↓↓↓↓极光ID↓↓↓↓↓↓
        String RegistrationID= AppPrefs.getString(MyApplication.getMyApp(), ConfigKey.JPUSHID);
        if (TextUtils.isEmpty(RegistrationID)){
            RegistrationID= JPushInterface.getRegistrationID(MyApplication.getMyApp());
            AppPrefs.putString(MyApplication.getMyApp(), ConfigKey.JPUSHID,RegistrationID);
        }
        //↑↑↑↑↑↑极光ID↑↑↑↑↑↑
        TokenModel tokenModel=new TokenModel();
        tokenModel.setTokenExpirationTime(login.getTokenExpirationTime());
        tokenModel.setToken(login.getToken());
        tokenModel.setCreateTime(login.getCreateTime());
        GlobalToken.updateToken(tokenModel);

        Intent intent=new Intent();
        intent.setAction(IntentAction.ACTION_MAIN);
        startActivity(intent);
        (getActivity()).finish();
    }

    @Override
    public void loginFailed(String message) {
        ToastUtils.showToast(getContext(),message);
    }

    @Override
    public Object getVerifiCodeParam() {
        return null;
    }

    @Override
    public void verifiCodeSuccess(Object mCallBackVo) {

    }

    @Override
    public void verifiCodeFailed(String message) {

    }
}