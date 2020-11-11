package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.applicationUtil.PhoneUtil;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;

import butterknife.BindView;
import butterknife.OnClick;

public class UniqueIDActivity extends BaseActivity{
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.id_tv)
    TextView id_tv;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.id1_tv)
    TextView id1_tv;
    @BindView(R.id.button2)
    Button button2;

    @BindView(R.id.id2_tv)
    TextView id2_tv;
    @BindView(R.id.button3)
    Button button3;
    @BindView(R.id.id3_tv)
    TextView id3_tv;
    @BindView(R.id.button4)
    Button button4;
    @BindView(R.id.id4_tv)
    TextView id4_tv;
    @BindView(R.id.button5)
    Button button5;
    @BindView(R.id.id5_tv)
    TextView id5_tv;
    @BindView(R.id.button6)
    Button button6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public int getLayoutId(){
        return R.layout.uniqueid_activity;
    }
    @OnClick({R.id.left_back,R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5,R.id.button6})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.button1:
                id_tv.setText(PhoneUtil.getUniqueID(getApplicationContext()));
                break;
            case R.id.button2:
                id1_tv.setText(PhoneUtil.getDeviceId(getApplicationContext()));
                break;
            case R.id.button3:
                id2_tv.setText(PhoneUtil.getPseudoUniqueID());
                break;
            case R.id.button4:
                id3_tv.setText(PhoneUtil.getAndroidId(getApplicationContext()));
                break;
            case R.id.button5:
                id4_tv.setText(PhoneUtil.getSimSerialNumber(getApplicationContext()));
                break;
            case R.id.button6:
                id5_tv.setText(PhoneUtil.getSerialNumber(getApplicationContext()));
                break;
        }
    }
}
