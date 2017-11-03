package com.jelly.jellybase.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.countdowntimerbtn.CountDownTimerButton;
import com.base.view.MyActivity;
import com.jelly.jellybase.R;

/**
 * Created by Administrator on 2017/9/28.
 */

public class RegisterActivity extends MyActivity {
    private LinearLayout left_back;
    private TextView next_tv;
    private TextView login_tv;
    private CountDownTimerButton get_ver_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        iniView();
        initCountDownBtn();
    }
    private void iniView(){
        left_back= (LinearLayout) findViewById(R.id.left_back);
        left_back.setOnClickListener(listener);

        login_tv= (TextView) findViewById(R.id.login_tv);
        login_tv.setOnClickListener(listener);

        next_tv= (TextView) findViewById(R.id.next_tv);
        next_tv.setOnClickListener(listener);
    }
    private void initCountDownBtn() {
        get_ver_btn= (CountDownTimerButton) findViewById(R.id.btn_get_ver);
        get_ver_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_ver_btn.setStartCountDownText("再次获取");//设置倒计时开始时按钮上的显示文字
                get_ver_btn.startCountDownTimer(60000,1000);//设置倒计时时间，间隔
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        get_ver_btn.onDestroy();
    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           switch (v.getId()){
               case R.id.left_back:
                   finish();
                   break;
               case R.id.login_tv:
                   finish();
                   break;
               case R.id.next_tv:
                   Intent intent=new Intent(RegisterActivity.this,SetPWDActivity.class);
                   startActivity(intent);
                   break;
           }
        }
    };
}
