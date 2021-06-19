package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.jelly.baselibrary.bankcard.BankCardInfo;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.BankcardEditActivityBinding;
import com.jelly.mvp.contact.BankCartContact;
import com.jelly.mvp.presenter.BankCartPresenter;

import java.util.Map;
import java.util.TreeMap;


/**
 * Created by Administrator on 2017/9/27.
 */

public class BankCardEditActivity extends BaseActivityImpl<BankCartContact.View
        ,BankCartContact.Presenter, BankcardEditActivityBinding>
        implements BankCartContact.View , View.OnClickListener {
    private BankCardInfo bankCardInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bankCardInfo= (BankCardInfo) getIntent().getSerializableExtra("bankCardInfo");
        if (bankCardInfo==null){
            finish();
            return;
        }
        iniView();
    }
    private void iniView(){
        getBinding().leftBack.setOnClickListener(this);
        getBinding().commitTv.setOnClickListener(this);
        getBinding().bankCardName.setText(bankCardInfo.getBankName()+"·"+bankCardInfo.getType());
        getBinding().bankCardId.setText(bankCardInfo.getBankNo());
    }
    @Override
    public void onBackPressed() {
        closeProgress();
        super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public BankCartContact.Presenter initPresenter() {
        return new BankCartPresenter();
    }
    @Override
    public BankCartContact.View initIBView() {
        return this;
    }

    @Override
    public LifecycleOwner bindLifecycle() {
        return this;
    }
    public void onClick(View v) {
        if (AntiShake.check(v.getId())) {    //判断是否多次点击
            return;
        }
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.commit_tv:
                presenter.deletebank();
                break;
        }
    }

    @Override
    public Object deletebankParam() {
        Map map=new TreeMap();
        map.put("bankid",bankCardInfo.getBankid());
        return map;
    }

    @Override
    public void deletebankSuccess( Object mCallBackVo) {
        ToastUtils.showToast(this, (String) mCallBackVo);
        finish(2000);
    }

    @Override
    public void deletebankFailed(String message) {
        ToastUtils.showToast(this,message);
    }
}
