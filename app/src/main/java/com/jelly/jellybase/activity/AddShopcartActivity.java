package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.base.circledialog.AddCartDialog;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;

/**
 * Created by JELLY on 2017/11/3.
 */

public class AddShopcartActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_shopcart_activity);
        iniView();
    }
    private void iniView(){
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCartDialog addCartDialog1=AddCartDialog.getInstance();
                addCartDialog1.show(getSupportFragmentManager(),"addCartDialog");
            }
        });
    }
}
