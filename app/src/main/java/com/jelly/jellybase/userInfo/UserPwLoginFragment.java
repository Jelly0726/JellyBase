package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.base.appManager.BaseApplication;
import com.base.applicationUtil.AppPrefs;
import com.base.config.ConfigKey;
import com.base.config.IntentAction;
import com.base.encrypt.MD5;
import com.base.httpmvp.contact.LoginContact;
import com.base.httpmvp.presenter.LoginActivityPresenter;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.httpmvp.retrofitapi.token.TokenModel;
import com.base.httpmvp.view.BaseFragmentImpl;
import com.base.jiguang.TagAliasOperatorHelper;
import com.base.multiClick.AntiShake;
import com.base.social.SocialUtil;
import com.base.toast.ToastUtils;
import com.google.gson.Gson;
import com.jelly.jellybase.R;
import com.trello.rxlifecycle3.android.FragmentEvent;

import net.arvin.socialhelper.callback.SocialLoginCallback;
import net.arvin.socialhelper.entities.ThirdInfoEntity;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import io.reactivex.ObservableTransformer;
import systemdb.Login;

/**
 * Created by Administrator on 2018/1/6.
 */

public class UserPwLoginFragment extends BaseFragmentImpl<LoginContact.View,LoginContact.Presenter>
        implements LoginContact.View{
    @BindView(R.id.login_tv)
    TextView login_tv;
    @BindView(R.id.forget_pwd)
    TextView forget_pwd;
    @BindView(R.id.phone_edit)
    EditText phone_edit;
    @BindView(R.id.password_edit)
    EditText password_edit;
    @BindView(R.id.pwd_visible)
    CheckBox pwd_visible;
    @BindView(R.id.isparallel)
    Switch isparallel;
    @BindView(R.id.wechat_tv)
    ImageView wechat_tv;
    @BindView(R.id.qq_tv)
    ImageView qq_tv;
    private String phone="";
    private String password;
    private double from=-1;
    private ThirdInfoEntity openInfo;
    @Override
    public int getLayoutId() {
        return R.layout.user_pwlogin_fragment;
    }
    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {

    }

    @Override
    public void onDestroyView() {
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
        pwd_visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    //选择状态 显示明文--设置为可见的密码
                    //mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    /**
                     * 第二种
                     */
                    password_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    //mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    /**
                     * 第二种
                     */
                    password_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                if (!TextUtils.isEmpty(password_edit.getText().toString()))
                    password_edit.setSelection(password_edit.getText().toString().length());
            }
        });
        if (!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(password)&&from==0){
            phone_edit.setText(phone);
            phone_edit.setSelection(phone.length());
            password_edit.setText(password);
            password_edit.setSelection(password.length());
            presenter.userLogin();
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
                presenter.userLogin();
                break;
            case R.id.forget_pwd:
                intent=new Intent(BaseApplication.getInstance(), ForgetActivity.class);
                startActivity(intent);
                break;
            case R.id.wechat_tv:
                SocialUtil.getInstance().socialHelper().loginWX(getActivity(), new SocialLoginCallback() {
                    @Override
                    public void loginSuccess(ThirdInfoEntity info) {
                        openInfo=info;
                    }

                    @Override
                    public void socialError(String msg) {
                        ToastUtils.showToast(getContext(),msg);
                    }
                });
                break;
            case R.id.qq_tv:
                SocialUtil.getInstance().socialHelper().loginQQ(getActivity(), new SocialLoginCallback() {
                    @Override
                    public void loginSuccess(ThirdInfoEntity info) {
                        openInfo=info;
                    }

                    @Override
                    public void socialError(String msg) {
                        ToastUtils.showToast(getContext(),msg);
                    }
                });
                break;
        }
    }
    @Override
    public LoginContact.Presenter initPresenter() {
        return new LoginActivityPresenter();
    }
    @Override
    public LoginContact.View initIBView() {
        return this;
    }

    @Override
    public <T> ObservableTransformer<T, T> bindLifecycle() {
        return lifecycleProvider.<Long>bindUntilEvent(FragmentEvent.DESTROY_VIEW);
    }
    @Override
    public Object getLoginParam() {
        //↓↓↓↓↓↓极光ID↓↓↓↓↓↓
        String RegistrationID= AppPrefs.getString(BaseApplication.getInstance(), ConfigKey.JPUSHID);
        if (TextUtils.isEmpty(RegistrationID)){
            RegistrationID= JPushInterface.getRegistrationID(BaseApplication.getInstance());
            AppPrefs.putString(BaseApplication.getInstance(), ConfigKey.JPUSHID,RegistrationID);
        }
        //↑↑↑↑↑↑极光ID↑↑↑↑↑↑
        password= MD5.MD5Encode(password);
        Map map=new TreeMap();
        map.put("salesphone",phone);
        map.put("password",password);
        map.put("isparallel",isparallel.isChecked());

//        if (openInfo.getPlatform().equals(ThirdInfoEntity.PLATFORM_QQ)){
//            map.put("openid",openInfo.getOpenId());
//            map.put("type",2);//openid的类型（1：微信2：qq）
//        }else {
//            map.put("openid",openInfo.getUnionId());
//            map.put("type",1);//openid的类型（1：微信2：qq）
//        }
        return map;
    }

    @Override
    public void loginSuccess(Object mCallBackVo) {
        Login login= (Login) mCallBackVo;

        //↓↓↓↓↓↓极光设置tag↓↓↓↓↓↓
        if (!AppPrefs.getBoolean(BaseApplication.getInstance(), ConfigKey.IS_SET_TAG,false)){
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
