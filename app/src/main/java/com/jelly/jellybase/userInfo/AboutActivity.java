package com.jelly.jellybase.userInfo;

import android.os.Bundle;
import android.view.View;

import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.jelly.baselibrary.applicationUtil.AppUtils;
import com.jelly.baselibrary.model.AboutUs;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.UserAboutActivityBinding;
import com.jelly.mvp.contact.AboutContact;
import com.jelly.mvp.presenter.AboutUsPresenter;
import com.trello.rxlifecycle3.android.ActivityEvent;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/10/19.
 */

public class AboutActivity extends BaseActivityImpl<AboutContact.View
        ,AboutContact.Presenter, UserAboutActivityBinding> implements AboutContact.View , View.OnClickListener {

    private AboutUs aboutUs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
        presenter.aboutUs(true);
    }
    private void iniView(){
getBinding().leftBack.setOnClickListener(this);
    }
    @Override
    public void onBackPressed() {
        closeProgress();
        super.onBackPressed();
    }
    private void iniData(){
        getBinding().versionsTv.setText("V "+ AppUtils.getAppVersion(this)+" ("+ AppUtils.getVersionCode(this)+")");
        getBinding().nameTv.setText(aboutUs.getCompanyname());
        getBinding().addressTv.setText(aboutUs.getAddress());
        getBinding().phoneTv.setText(aboutUs.getTelephone());
    }
    @Override
    protected void onDestroy() {
        closeProgress();
        super.onDestroy();
    }

    @Override
    public AboutContact.Presenter initPresenter() {
        return new AboutUsPresenter();
    }
    @Override
    public AboutContact.View initIBView() {
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
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
        }
    }

    @Override
    public void aboutUsSuccess(boolean isRefresh, Object mCallBackVo) {
        aboutUs= (AboutUs) mCallBackVo;
        if (aboutUs!=null){
            iniData();
        }
    }

    @Override
    public void aboutUsFailed(boolean isRefresh, String message) {
        ToastUtils.showToast(this,message);
    }
}
