package com.jelly.jellybase.userInfo;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.applicationUtil.AppUtils;
import com.base.applicationUtil.ToastUtils;
import com.base.httpmvp.mode.databean.AboutUs;
import com.base.httpmvp.presenter.AboutUsPresenter;
import com.base.httpmvp.view.IAboutUsView;
import com.base.mprogressdialog.MProgressUtil;
import com.base.multiClick.AntiShake;
import com.base.view.MyActivity;
import com.jelly.jellybase.R;
import com.maning.mndialoglibrary.MProgressDialog;
import com.trello.rxlifecycle2.android.ActivityEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/19.
 */

public class AboutActivity extends MyActivity implements IAboutUsView {
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

    private AboutUsPresenter aboutUsPresenter;
    private MProgressDialog progressDialog;
    private AboutUs aboutUs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_about_activity);
        //绑定id
        ButterKnife.bind(this);
        iniView();
        iniProgress();
        aboutUsPresenter=new AboutUsPresenter(this);
        aboutUsPresenter.aboutUs(true,lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
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
    private void iniProgress(){
        progressDialog= MProgressUtil.getInstance().getMProgressDialog(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog=null;
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
    public void showProgress() {
        if (progressDialog!=null){
                progressDialog.show();
        }
    }

    @Override
    public void closeProgress() {
        if (progressDialog!=null){
                progressDialog.dismiss();
        }
    }

    @Override
    public Object getAboutUsParam() {
        return null;
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
