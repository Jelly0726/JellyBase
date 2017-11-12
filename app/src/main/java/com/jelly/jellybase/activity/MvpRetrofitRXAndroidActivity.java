package com.jelly.jellybase.activity;

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
import com.base.mprogressdialog.MProgressUtil;
import com.base.multiClick.AntiShake;
import com.jelly.jellybase.R;
import com.maning.mndialoglibrary.MProgressDialog;

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
    private MProgressDialog progressDialog;
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
        progressDialog= MProgressUtil.getInstance().getMProgressDialog(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog=null;
    }
    @OnClick(R.id.query)
    public void onClick(View v) {
        if (AntiShake.check(v.getId())) {    //判断是否多次点击
            return;
        }
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
    public void excuteSuccess(boolean isRefresh, Object mCallBackVo) {
    }

    @Override
    public void excuteFailed(boolean isRefresh, String message) {
        ToastUtils.showToast(this,message);
    }
}
