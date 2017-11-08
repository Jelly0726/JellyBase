package com.jelly.jellybase.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.base.applicationUtil.ToastUtils;
import com.base.httpmvp.mode.databean.RegisterParam;
import com.base.httpmvp.presenter.RegisterActivityPresenter;
import com.base.httpmvp.view.IRegisterActivityView;
import com.jelly.jellybase.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/7.
 */

public class MvpRetrofitRXAndroidActivity extends AppCompatActivity implements IRegisterActivityView{
    @BindView(R.id.query)Button query;
    @BindView(R.id.iptv)TextView iptv;
    private RegisterActivityPresenter registerActivityPresenter;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mvp_retrofit_rxandroid_activity);
        // 进行绑定
        ButterKnife.bind(this);
        iniView();
        iniProgress();
        registerActivityPresenter=new RegisterActivityPresenter(this);
    }
    private void iniView(){
    }
    private void iniProgress(){
        progressDialog=new ProgressDialog(this);
        //progressDialog.setTitle("加载提示");
        progressDialog.setMessage("正在加载.....");
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }
    @OnClick(R.id.query)
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.query:
                //netIPPresenter.getData(false);
                registerActivityPresenter.userRegister(true);
                break;
        }
    }

    @Override
    public Object getParamenters() {
        RegisterParam registerParam=new RegisterParam();
        registerParam.setAccount("18850221236");
        registerParam.setPassword("C4CA4238A0B923820DCC509A6F75849B");
        return registerParam;
    }

    @Override
    public void showProgress() {
        if (progressDialog!=null){
            if (!progressDialog.isShowing()){
                progressDialog.show();
            }
        }
    }

    @Override
    public void closeProgress() {
        if (progressDialog!=null){
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void excuteSuccessCallBack(boolean isRefresh, Object mCallBackVo) {
    }

    @Override
    public void excuteFailedCallBack(boolean isRefresh, String message) {
        ToastUtils.showToast(this,message);
    }
}
