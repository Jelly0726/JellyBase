package com.jelly.jellybase.userInfo;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.applicationUtil.AppUtils;
import com.base.toast.ToastUtils;
import com.base.httpmvp.contact.AboutContact;
import com.base.httpmvp.databean.AboutUs;
import com.base.httpmvp.presenter.AboutUsPresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.multiClick.AntiShake;
import com.jelly.jellybase.R;
import com.trello.rxlifecycle3.android.ActivityEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/19.
 */

public class AboutActivity extends BaseActivityImpl<AboutContact.Presenter> implements AboutContact.View {
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
        setContentView(R.layout.user_about_activity);
        //绑定id
        ButterKnife.bind(this);
        iniView();
        presenter.aboutUs(true,lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
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
        return new AboutUsPresenter(this);
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
