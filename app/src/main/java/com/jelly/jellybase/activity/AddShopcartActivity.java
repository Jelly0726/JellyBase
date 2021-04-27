package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.jelly.baselibrary.dialog.AddCartDialog;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.jellybase.R;

/**
 * Created by JELLY on 2017/11/3.
 */

public class AddShopcartActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.add_shopcart_activity;
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
