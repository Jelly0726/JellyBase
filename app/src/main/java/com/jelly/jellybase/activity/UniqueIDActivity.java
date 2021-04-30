package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.applicationUtil.PhoneUtil;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.UniqueidActivityBinding;

public class UniqueIDActivity extends BaseActivity<UniqueidActivityBinding> implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBinding().leftBack.setOnClickListener(this);
        getBinding().button1.setOnClickListener(this);
        getBinding().button2.setOnClickListener(this);
        getBinding().button3.setOnClickListener(this);
        getBinding().button4.setOnClickListener(this);
        getBinding().button5.setOnClickListener(this);
        getBinding().button6.setOnClickListener(this);
    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.button1:
                getBinding().idTv.setText(PhoneUtil.getUniqueID(getApplicationContext()));
                break;
            case R.id.button2:
                getBinding().id1Tv.setText(PhoneUtil.getDeviceId(getApplicationContext()));
                break;
            case R.id.button3:
                getBinding().id2Tv.setText(PhoneUtil.getPseudoUniqueID());
                break;
            case R.id.button4:
                getBinding().id3Tv.setText(PhoneUtil.getAndroidId(getApplicationContext()));
                break;
            case R.id.button5:
                getBinding().id4Tv.setText(PhoneUtil.getSimSerialNumber(getApplicationContext()));
                break;
            case R.id.button6:
                getBinding().id5Tv.setText(PhoneUtil.getSerialNumber(getApplicationContext()));
                break;
        }
    }
}
