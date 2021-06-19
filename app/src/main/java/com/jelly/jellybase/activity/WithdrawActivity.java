package com.jelly.jellybase.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.base.BaseApplication;
import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jelly.baselibrary.applicationUtil.AppPrefs;
import com.jelly.baselibrary.bankcard.BankCardInfo;
import com.jelly.baselibrary.config.ConfigKey;
import com.jelly.baselibrary.moneyedittext.MoneyTextWatcher;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.WithdrawActivityBinding;
import com.jelly.mvp.contact.WithdrawalsContact;
import com.jelly.mvp.presenter.WithdrawalsPresenter;

import java.util.Map;
import java.util.TreeMap;


/**
 * Created by JELLY on 2017/9/26.
 */

public class WithdrawActivity extends BaseActivityImpl<WithdrawalsContact.View
        ,WithdrawalsContact.Presenter, WithdrawActivityBinding> implements WithdrawalsContact.View{
    private static final int requestCode=0;
    private BankCardInfo bankInfo;
    private double charge=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        charge=getIntent().getDoubleExtra("charge",0);
        iniView();
    }
    @SuppressLint("WrongViewCast")
    private void iniView(){
        getBinding().leftBack.setOnClickListener(listener);
        getBinding().changeBank.setOnClickListener(listener);
        getBinding().bankTx.setOnClickListener(listener);
        getBinding().commitTv.setOnClickListener(listener);
        getBinding().amount.addTextChangedListener(new MoneyTextWatcher(getBinding().amount){
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString().trim())){
                    double acount=Double.parseDouble(s.toString().trim());
                    getBinding().chargeTv.setText("手续费：¥"+String.valueOf(acount*charge)+"元");
                }
            }
        }.setDigits(2));
        Gson gson=new Gson();
        String json= AppPrefs.getString(BaseApplication.getInstance(),
                ConfigKey.DEFAULT_BANK);
        bankInfo=gson.fromJson(json,BankCardInfo.class);
        if (bankInfo==null){
            getBinding().bankTx.setVisibility(View.VISIBLE);
            getBinding().changeBank.setVisibility(View.GONE);
        }else {
            getBinding().bankTx.setVisibility(View.GONE);
            getBinding().changeBank.setVisibility(View.VISIBLE);
            iniDataBank();
        }
    }
    private void iniDataBank(){
        getBinding().bankName.setText(bankInfo.getBankName());
        String bankNo=bankInfo.getBankNo().substring(bankInfo.getBankNo().length()-4,
                bankInfo.getBankNo().length());
        getBinding().bankNo.setText("尾号"+bankNo);
        if(bankInfo.getBankLogo()!=null){
            if (bankInfo.getBankLogo().trim().length()>6){
                Glide.with(this)
                        .load(bankInfo.getBankLogo().trim())
                        .placeholder(R.drawable.yinlian)
                        .error(R.drawable.yinlian)
                        .dontAnimate()
                        .centerCrop()
                        .into(getBinding().bankLogo);
                return;
            }
        }
        getBinding().bankLogo.setImageResource(R.drawable.yinlian);
    }
    @Override
    protected void onDestroy() {
        closeProgress();
        super.onDestroy();
    }

    @Override
    public WithdrawalsContact.Presenter initPresenter() {
        return new WithdrawalsPresenter();
    }
    @Override
    public WithdrawalsContact.View initIBView() {
        return this;
    }

    @Override
    public LifecycleOwner bindLifecycle() {
        return this;
    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (AntiShake.check(v.getId())) {    //判断是否多次点击
                return;
            }
            Intent intent;
            switch (v.getId()){
                case R.id.left_back:
                    finish();
                    break;
                case R.id.bank_tx:
                case R.id.change_bank:
                    intent=new Intent(WithdrawActivity.this,BankCardListActivity.class);
                    intent.putExtra("requestCode",requestCode);
                    startActivityForResult(intent,requestCode);
                    break;
                case R.id.commit_tv:
                    if (bankInfo==null){
                        ToastUtils.showToast(WithdrawActivity.this,"请选择提现银行卡!");
                        return;
                    }
                    String amounts= getBinding().amount.getText().toString().trim();
                    if (TextUtils.isEmpty(amounts)){
                        ToastUtils.showToast(WithdrawActivity.this,"请输入提现金额!");
                        return;
                    }
                    presenter.withdrawals();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(WithdrawActivity.this.requestCode==requestCode && resultCode==WithdrawActivity.this.requestCode){
            if (data!=null) {
                bankInfo= (BankCardInfo) data.getSerializableExtra("data");
                if(bankInfo==null){
                    getBinding().bankTx.setVisibility(View.VISIBLE);
                    getBinding().changeBank.setVisibility(View.GONE);
                    return;
                }
                getBinding().bankTx.setVisibility(View.GONE);
                getBinding().changeBank.setVisibility(View.VISIBLE);
                iniDataBank();
            }
        }
    }

    @Override
    public Object withdrawalsParam() {
        String amounts= getBinding().amount.getText().toString().trim();
        Map map=new TreeMap();
        map.put("amount",amounts);
        map.put("withdrawalaccount",bankInfo.getBankNo());
        map.put("withdrawalstype",3);//提现类型(1支付宝,2微信,3银行卡)
        return map;
    }

    @Override
    public void withdrawalsSuccess(Object mCallBackVo) {
        ToastUtils.showToast(this, (String) mCallBackVo);
        finish(3000);
    }

    @Override
    public void withdrawalsFailed(String message) {
        ToastUtils.showToast(this,message);
    }
}
