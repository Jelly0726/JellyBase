package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.jelly.baselibrary.bankcard.BandCardEditText;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.BankcardAddActivityBinding;
import com.jelly.mvp.contact.AddBankCartContact;
import com.jelly.mvp.presenter.AddBankPresenter;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import io.reactivex.ObservableTransformer;


/**
 * Created by Administrator on 2017/9/27.
 */

public class AddBankCardActivity extends BaseActivityImpl<AddBankCartContact.View
        ,AddBankCartContact.Presenter, BankcardAddActivityBinding>
        implements AddBankCartContact.View , View.OnClickListener {

    private String bankName="没有查到所属银行";
    private String bankType="储蓄卡";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    private void iniView(){
        getViewBinding().leftBack.setOnClickListener(this);
        getViewBinding().commitTv.setOnClickListener(this);
        getViewBinding().bankCardId.setBankCardListener(new BandCardEditText.BankCardListener() {
            @Override
            public void success(String name,String type) {
                getViewBinding().bankCardName.setText(name+"·"+type);
                bankName=name;
                bankType=type;
            }

            @Override
            public void failure() {
                getViewBinding().bankCardName.setText("没有查到所属银行");
                bankName="没有查到所属银行";
                bankType="储蓄卡";
            }
        });
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
    public AddBankCartContact.Presenter initPresenter() {
        return new AddBankPresenter();
    }

    @Override
    public AddBankCartContact.View initIBView() {
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
            case R.id.commit_tv:
                String phone=getViewBinding().phoneEdit.getText().toString().trim();
                String cardholder=getViewBinding().cardholderEdit.getText().toString().trim();
                String bankCard=getViewBinding().bankCardId.getText().toString().trim();
                if (TextUtils.isEmpty(phone)||TextUtils.isEmpty(cardholder)||
                        TextUtils.isEmpty(bankCard)){
                    ToastUtils.showToast(this,"手机号、持卡人、卡号不能为空！");
                    return;
                }
                presenter.addBank();
                break;
        }
    }

    @Override
    public Object addBankParam() {
        String phone=getViewBinding().phoneEdit.getText().toString().trim();
        String cardholder=getViewBinding().cardholderEdit.getText().toString().trim();
        String bankCard=getViewBinding().bankCardId.getCardNo().toString().trim();
        Map map=new TreeMap();
        map.put("phone",phone);
        map.put("bankname",bankName);
        map.put("bankno",bankCard);
        map.put("bankuser",cardholder);
        map.put("type",bankType);
        map.put("isdefault",false);
        return map;
    }

    @Override
    public void addBankSuccess( Object mCallBackVo) {
        ToastUtils.showToast(this, (String) mCallBackVo);
        finish(2000);
    }

    @Override
    public void addBankFailed( String message) {
        ToastUtils.showToast(this,message);
    }
}
