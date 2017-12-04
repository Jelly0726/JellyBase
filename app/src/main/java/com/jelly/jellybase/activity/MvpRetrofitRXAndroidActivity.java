package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.base.applicationUtil.ToastUtils;
import com.base.httpmvp.presenter.RegisterActivityPresenter;
import com.base.httpmvp.view.IRegisterActivityView;
import com.base.mprogressdialog.MProgressUtil;
import com.base.multiClick.AntiShake;
import com.base.view.MyActivity;
import com.jelly.jellybase.R;
import com.maning.mndialoglibrary.MProgressDialog;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/7.
 */

public class MvpRetrofitRXAndroidActivity extends MyActivity implements IRegisterActivityView{
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
                registerActivityPresenter.userRegister(lifecycleProvider
                        .<Long>bindUntilEvent(ActivityEvent.DESTROY));
                break;
        }
    }

    @Override
    public Object getRegParam() {
        Map map=new TreeMap();
        return map;
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
