package com.jelly.jellybase.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.base.config.IntentAction;
import com.base.view.MyActivity;
import com.jelly.jellybase.R;

/**
 * Created by Administrator on 2017/9/18.
 */

public class LoginActivity extends MyActivity {
    private TextView login_tv;
    private TextView forget_pwd;
    private TextView register_account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        iniView();
    }
    private void iniView (){
        login_tv= (TextView) findViewById(R.id.login_tv);
        login_tv.setOnClickListener(listener);

        forget_pwd= (TextView) findViewById(R.id.forget_pwd);
        forget_pwd.setOnClickListener(listener);

        register_account= (TextView) findViewById(R.id.register_account);
        register_account.setOnClickListener(listener);
    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()){
                case R.id.login_tv:
                    intent=new Intent();
                    intent.setAction(IntentAction.ACTION_MAIN);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.forget_pwd:
                    intent=new Intent(LoginActivity.this, ForgetActivity.class);
                    startActivity(intent);
                    break;
                case R.id.register_account:
                    intent=new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}
