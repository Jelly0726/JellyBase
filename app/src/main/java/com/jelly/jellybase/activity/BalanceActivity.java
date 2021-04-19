package com.jelly.jellybase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jelly.baselibrary.multiClick.AntiShake;
import com.base.BaseActivity;
import com.jelly.jellybase.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/9/26.
 */

public class BalanceActivity extends BaseActivity {
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.recharge_layout)
    LinearLayout recharge_layout;
    @BindView(R.id.withdraw_layout)
    LinearLayout withdraw_layout;
    @BindView(R.id.account_details)
    LinearLayout account_details;
    @BindView(R.id.balnace_tv)
    TextView balnace_tv;
    private String balance;
    private double charge=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        charge=getIntent().getDoubleExtra("charge",0);
        ButterKnife.bind(this);
        iniView ();
        balance=getIntent().getStringExtra("Balance");
        iniData();
    }
    @Override
    public int getLayoutId(){
        return R.layout.balance_activity;
    }
    private void iniView (){
    }
    private void iniData(){
        balnace_tv.setText(balance);
    }
    @OnClick({R.id.left_back,R.id.recharge_layout,R.id.withdraw_layout,R.id.account_details})
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
