package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;

import com.base.BaseApplication;
import com.base.httpmvp.mvpView.BaseFragmentImpl;
import com.base.jiguang.TagAliasOperatorHelper;
import com.google.gson.Gson;
import com.jelly.baselibrary.applicationUtil.AppPrefs;
import com.jelly.baselibrary.config.ConfigKey;
import com.jelly.baselibrary.config.IntentAction;
import com.jelly.baselibrary.encrypt.MD5;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.social.SocialUtil;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.baselibrary.token.GlobalToken;
import com.jelly.baselibrary.token.TokenModel;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.UserPwloginFragmentBinding;
import com.jelly.mvp.contact.LoginContact;
import com.jelly.mvp.presenter.LoginActivityPresenter;
import com.trello.rxlifecycle3.android.FragmentEvent;

import net.arvin.socialhelper.callback.SocialLoginCallback;
import net.arvin.socialhelper.entities.ThirdInfoEntity;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.ObservableTransformer;
import systemdb.Login;

/**
 * Created by Administrator on 2018/1/6.
 */

public class UserPwLoginFragment extends BaseFragmentImpl<LoginContact.View
        , LoginContact.Presenter, UserPwloginFragmentBinding>
        implements LoginContact.View, View.OnClickListener {
    private String phone = "";
    private String password;
    private double from = -1;
    private ThirdInfoEntity openInfo;

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
        Map map = new Gson().fromJson(json, Map.class);
        phone = (String) map.get("phone");
        password = (String) map.get("password");
        from = (Double) map.get("from");
        if (getActivity() != null) {
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

    private void iniData() {
        getViewBinding().loginTv.setOnClickListener(this);
        getViewBinding().forgetPwd.setOnClickListener(this);
        getViewBinding().registerAccount.setOnClickListener(this);
        getViewBinding().pwdVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //选择状态 显示明文--设置为可见的密码
                    //mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    /**
                     * 第二种
                     */
                    getViewBinding().passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    //mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    /**
                     * 第二种
                     */
                    getViewBinding().passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                if (!TextUtils.isEmpty(getViewBinding().passwordEdit.getText().toString()))
                    getViewBinding().passwordEdit.setSelection(getViewBinding().passwordEdit.getText().toString().length());
            }
        });
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password) && from == 0) {
            getViewBinding().phoneEdit.setText(phone);
            getViewBinding().phoneEdit.setSelection(phone.length());
            getViewBinding().passwordEdit.setText(password);
            getViewBinding().passwordEdit.setSelection(password.length());
            presenter.userLogin();
        }
    }

    public void onClick(View v) {
        if (AntiShake.check(v.getId())) {    //判断是否多次点击
            return;
        }
        Intent intent;
        switch (v.getId()) {
            case R.id.login_tv:
                phone = getViewBinding().phoneEdit.getText().toString().trim();
                password = getViewBinding().passwordEdit.getText().toString().trim();
                if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                    ToastUtils.showToast(getContext(), "请输入您的手机号和动态密码!");
                    return;
                }
                presenter.userLogin();
                break;
            case R.id.forget_pwd:
                intent = new Intent(BaseApplication.getInstance(), ForgetActivity.class);
                startActivity(intent);
                break;
            case R.id.wechat_tv:
                SocialUtil.getInstance().socialHelper().loginWX(getActivity(), new SocialLoginCallback() {
                    @Override
                    public void loginSuccess(ThirdInfoEntity info) {
                        openInfo = info;
                    }

                    @Override
                    public void socialError(String msg) {
                        ToastUtils.showToast(getContext(), msg);
                    }
                });
                break;
            case R.id.qq_tv:
                SocialUtil.getInstance().socialHelper().loginQQ(getActivity(), new SocialLoginCallback() {
                    @Override
                    public void loginSuccess(ThirdInfoEntity info) {
                        openInfo = info;
                    }

                    @Override
                    public void socialError(String msg) {
                        ToastUtils.showToast(getContext(), msg);
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
        String RegistrationID = AppPrefs.getString(BaseApplication.getInstance(), ConfigKey.JPUSHID);
        if (TextUtils.isEmpty(RegistrationID)) {
            RegistrationID = JPushInterface.getRegistrationID(BaseApplication.getInstance());
            AppPrefs.putString(BaseApplication.getInstance(), ConfigKey.JPUSHID, RegistrationID);
        }
        //↑↑↑↑↑↑极光ID↑↑↑↑↑↑
        password = MD5.MD5Encode(password);
        Map map = new TreeMap();
        map.put("salesphone", phone);
        map.put("password", password);
        map.put("isparallel", getViewBinding().isparallel.isChecked());

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
        Login login = (Login) mCallBackVo;

        //↓↓↓↓↓↓极光设置tag↓↓↓↓↓↓
        if (!AppPrefs.getBoolean(BaseApplication.getInstance(), ConfigKey.IS_SET_TAG, false)) {
            if (!TextUtils.isEmpty(login.getCompanyno())) {
                Set<String> tagSet = new LinkedHashSet<String>();
                tagSet.add(login.getCompanyno());
                TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
                tagAliasBean.action = TagAliasOperatorHelper.ACTION_SET;
                tagAliasBean.tags = tagSet;
                tagAliasBean.isAliasAction = false;
                TagAliasOperatorHelper.sequence++;
                TagAliasOperatorHelper.getInstance().handleAction(getContext().getApplicationContext(),
                        TagAliasOperatorHelper.sequence, tagAliasBean);
            }
        }
        //↑↑↑↑↑↑极光设置tag↑↑↑↑↑↑
        TokenModel tokenModel = new TokenModel();
        tokenModel.setTokenExpirationTime(login.getTokenExpirationTime());
        tokenModel.setToken(login.getToken());
        tokenModel.setCreateTime(login.getCreateTime());
        GlobalToken.updateToken(tokenModel);

        Intent intent = new Intent();
        intent.setAction(IntentAction.ACTION_MAIN);
        startActivity(intent);
        (getActivity()).finish();
    }

    @Override
    public void loginFailed(String message) {
        ToastUtils.showToast(getContext(), message);
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
