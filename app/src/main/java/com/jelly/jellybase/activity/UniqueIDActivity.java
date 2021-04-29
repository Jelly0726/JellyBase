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
        getViewBinding().leftBack.setOnClickListener(this);
        getViewBinding().button1.setOnClickListener(this);
        getViewBinding().button2.setOnClickListener(this);
        getViewBinding().button3.setOnClickListener(this);
        getViewBinding().button4.setOnClickListener(this);
        getViewBinding().button5.setOnClickListener(this);
        getViewBinding().button6.setOnClickListener(this);
    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.button1:
                getViewBinding().idTv.setText(PhoneUtil.getUniqueID(getApplicationContext()));
                break;
            case R.id.button2:
                getViewBinding().id1Tv.setText(PhoneUtil.getDeviceId(getApplicationContext()));
                break;
            case R.id.button3:
                getViewBinding().id2Tv.setText(PhoneUtil.getPseudoUniqueID());
                break;
            case R.id.button4:
                getViewBinding().id3Tv.setText(PhoneUtil.getAndroidId(getApplicationContext()));
                break;
            case R.id.button5:
                getViewBinding().id4Tv.setText(PhoneUtil.getSimSerialNumber(getApplicationContext()));
                break;
            case R.id.button6:
                getViewBinding().id5Tv.setText(PhoneUtil.getSerialNumber(getApplicationContext()));
                break;
        }
    }
}
