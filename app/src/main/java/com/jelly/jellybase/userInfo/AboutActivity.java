package com.jelly.jellybase.userInfo;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.applicationUtil.AppUtils;
import com.base.httpmvp.mvpContact.AboutContact;
import com.base.httpmvp.mvpPresenter.AboutUsPresenter;
import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.base.model.AboutUs;
import com.base.multiClick.AntiShake;
import com.base.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.trello.rxlifecycle3.android.ActivityEvent;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/10/19.
 */

public class AboutActivity extends BaseActivityImpl<AboutContact.View,AboutContact.Presenter> implements AboutContact.View {
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.versions_tv)
    TextView versions_tv;
    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.address_tv)
    TextView address_tv;
    @BindView(R.id.phone_tv)
    TextView phone_tv;

    private AboutUs aboutUs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
        presenter.aboutUs(true);
    }
    @Override
    public int getLayoutId(){
        return R.layout.user_about_activity;
    }
    private void iniView(){

    }
    @Override
    public void onBackPressed() {
        closeProgress();
        super.onBackPressed();
    }
    private void iniData(){
        versions_tv.setText("V "+ AppUtils.getAppVersion(this)+" ("+ AppUtils.getVersionCode(this)+")");
        name_tv.setText(aboutUs.getCompanyname());
        address_tv.setText(aboutUs.getAddress());
        phone_tv.setText(aboutUs.getTelephone());
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
    @OnClick({R.id.left_back})
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
