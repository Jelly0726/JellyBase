package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.base.BaseApplication;
import com.base.httpmvp.mvpView.BaseFragmentImpl;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.jiguang.TagAliasOperatorHelper;
import com.google.gson.Gson;
import com.jelly.baselibrary.applicationUtil.AppPrefs;
import com.jelly.baselibrary.config.ConfigKey;
import com.jelly.baselibrary.config.IntentAction;
import com.jelly.baselibrary.encrypt.MD5;
import com.jelly.baselibrary.social.SocialUtil;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.baselibrary.token.GlobalToken;
import com.jelly.baselibrary.token.TokenModel;
import com.jelly.annotation.OnClick;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.UserDyloginFragmentBinding;
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

public class UserDyLoginFragment extends BaseFragmentImpl<LoginContact.View
        , LoginContact.Presenter, UserDyloginFragmentBinding>
        implements LoginContact.View {
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
        initCountDownBtn();
        iniData();
    }

    private void iniData() {
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password) && from == 0) {
            getBinding().phoneEdit.setText(phone);
            getBinding().phoneEdit.setSelection(phone.length());
            getBinding().passwordEdit.setText(password);
            getBinding().passwordEdit.setSelection(password.length());
            presenter.userLogin();
        }
    }

    private void initCountDownBtn() {
        getBinding().btnGetVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = getBinding().phoneEdit.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showToast(getContext(), "请输入手机号");
                    return;
                }
                presenter.getVerifiCode();
            }
        });
    }

    @OnClick({R.id.login_tv, R.id.forget_pwd, R.id.wechat_tv, R.id.qq_tv})
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.login_tv:
                phone = getBinding().phoneEdit.getText().toString().trim();
                password = getBinding().passwordEdit.getText().toString().trim();
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
        String phone = getBinding().phoneEdit.getText().toString().trim();
        Map map = new TreeMap<>();
        map.put("phone", phone);
        map.put("flag", 1);//验证码标识：1注册，2忘记密码，3修改手机号
        return map;
    }

    @Override
    public void verifiCodeSuccess(Object mCallBackVo) {
        HttpResult httpResultAll = (HttpResult) mCallBackVo;
        ToastUtils.showToast(getContext(), httpResultAll.getMsg());
        getBinding().btnGetVer.setStartCountDownText("再次获取");//设置倒计时开始时按钮上的显示文字
        getBinding().btnGetVer.startCountDownTimer(60000, 1000);//设置倒计时时间，间隔
    }

    @Override
    public void verifiCodeFailed(String message) {
        ToastUtils.showToast(getContext(), message);
    }
}
