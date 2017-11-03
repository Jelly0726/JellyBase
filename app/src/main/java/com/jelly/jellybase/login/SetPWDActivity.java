package com.jelly.jellybase.login;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.base.view.MyActivity;
import com.jelly.jellybase.R;

/**
 * Created by Administrator on 2017/9/28.
 */

public class SetPWDActivity extends MyActivity {
    private LinearLayout left_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpwd);
        iniView();
    }
    private void iniView(){
        left_back= (LinearLayout) findViewById(R.id.left_back);
        left_back.setOnClickListener(listener);
    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           switch (v.getId()){
               case R.id.left_back:
                   finish();
                   break;
           }
        }
    };
}
