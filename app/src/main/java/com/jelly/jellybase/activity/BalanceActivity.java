package com.jelly.jellybase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.BalanceActivityBinding;

/**
 * Created by Administrator on 2017/9/26.
 */

public class BalanceActivity extends BaseActivity<BalanceActivityBinding> implements View.OnClickListener {
    private String balance;
    private double charge=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        charge=getIntent().getDoubleExtra("charge",0);
        iniView ();
        balance=getIntent().getStringExtra("Balance");
        iniData();
    }
    private void iniView (){
        getViewBinding().leftBack.setOnClickListener(this);
        getViewBinding().rechargeLayout.setOnClickListener(this);
        getViewBinding().withdrawLayout.setOnClickListener(this);
        getViewBinding().accountDetails.setOnClickListener(this);
    }
    private void iniData(){
        getViewBinding().balnaceTv.setText(balance);
    }
    public void onClick(View v) {
        if (AntiShake.check(v.getId())) {    //判断是否多次点击
            return;
        }
        Intent intent;
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.recharge_layout:
                intent=new Intent(BalanceActivity.this,RechargeActivity.class);
                startActivity(intent);
                break;
            case R.id.withdraw_layout:
                intent=new Intent(BalanceActivity.this,WithdrawActivity.class);
                intent.putExtra("charge",charge);
                startActivity(intent);
                break;
            case R.id.account_details:
                intent=new Intent(BalanceActivity.this,AccountDetailsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
