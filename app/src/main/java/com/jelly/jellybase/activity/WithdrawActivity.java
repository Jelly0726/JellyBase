package com.jelly.jellybase.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.appManager.BaseApplication;
import com.base.applicationUtil.AppPrefs;
import com.base.bankcard.BankCardInfo;
import com.base.config.ConfigKey;
import com.base.httpmvp.contact.WithdrawalsContact;
import com.base.httpmvp.presenter.WithdrawalsPresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.moneyedittext.MoneyTextWatcher;
import com.base.multiClick.AntiShake;
import com.base.toast.ToastUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jelly.jellybase.R;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import io.reactivex.ObservableTransformer;


/**
 * Created by JELLY on 2017/9/26.
 */

public class WithdrawActivity extends BaseActivityImpl<WithdrawalsContact.View,WithdrawalsContact.Presenter> implements WithdrawalsContact.View{
    private static final int requestCode=0;
    private LinearLayout left_back;
    private LinearLayout change_bank;
    private ImageView bank_logo;
    private TextView bank_name;
    private TextView bank_no;
    private TextView bank_tx;
    private TextView commit_tv;
    private TextView charge_tv;
    private EditText amount;
    private BankCardInfo bankInfo;
    private double charge=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        charge=getIntent().getDoubleExtra("charge",0);
        iniView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.withdraw_activity;
    }
    @SuppressLint("WrongViewCast")
    private void iniView(){
        left_back= (LinearLayout) findViewById(R.id.left_back);
        left_back.setOnClickListener(listener);
        change_bank= (LinearLayout) findViewById(R.id.change_bank);
        change_bank.setOnClickListener(listener);
        bank_tx= (TextView) findViewById(R.id.bank_tx);
        bank_tx.setOnClickListener(listener);
        commit_tv= (TextView) findViewById(R.id.commit_tv);
        commit_tv.setOnClickListener(listener);
        bank_name= (TextView) findViewById(R.id.bank_name);
        bank_no= (TextView) findViewById(R.id.bank_no);
        bank_logo= (ImageView) findViewById(R.id.bank_logo);
        charge_tv= (TextView) findViewById(R.id.charge_tv);
        amount=findViewById(R.id.amount);
        amount.addTextChangedListener(new MoneyTextWatcher(amount){
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString().trim())){
                    double acount=Double.parseDouble(s.toString().trim());
                    charge_tv.setText("手续费：¥"+String.valueOf(acount*charge)+"元");
                }
            }
        }.setDigits(2));
        Gson gson=new Gson();
        String json= AppPrefs.getString(BaseApplication.getInstance(),
                ConfigKey.DEFAULT_BANK);
        bankInfo=gson.fromJson(json,BankCardInfo.class);
        if (bankInfo==null){
            bank_tx.setVisibility(View.VISIBLE);
            change_bank.setVisibility(View.GONE);
        }else {
            bank_tx.setVisibility(View.GONE);
            change_bank.setVisibility(View.VISIBLE);
            iniDataBank();
        }
    }
    private void iniDataBank(){
        bank_name.setText(bankInfo.getBankName());
        String bankNo=bankInfo.getBankNo().substring(bankInfo.getBankNo().length()-4,
                bankInfo.getBankNo().length());
        bank_no.setText("尾号"+bankNo);
        if(bankInfo.getBankLogo()!=null){
            if (bankInfo.getBankLogo().trim().length()>6){
                Glide.with(this)
                        .load(bankInfo.getBankLogo().trim())
                        .placeholder(R.drawable.yinlian)
                        .error(R.drawable.yinlian)
                        .dontAnimate()
                        .centerCrop()
                        .into(bank_logo);
                return;
            }
        }
        bank_logo.setImageResource(R.drawable.yinlian);
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
    public <T> ObservableTransformer<T, T> bindLifecycle() {
        return lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY);
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
                    String amounts=amount.getText().toString().trim();
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
                    bank_tx.setVisibility(View.VISIBLE);
                    change_bank.setVisibility(View.GONE);
                    return;
                }
                bank_tx.setVisibility(View.GONE);
                change_bank.setVisibility(View.VISIBLE);
                iniDataBank();
            }
        }
    }

    @Override
    public Object withdrawalsParam() {
        String amounts=amount.getText().toString().trim();
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
