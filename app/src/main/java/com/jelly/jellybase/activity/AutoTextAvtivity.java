package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.base.multiClick.AntiShake;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.AutoTextAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AutoTextAvtivity extends BaseActivity{
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.autoText_ed)
    AutoCompleteTextView autoText_ed;

    private AutoTextAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autotext_activity);
        ButterKnife.bind(this);
        iniView();
    }
    private void iniView(){
        adapter=new AutoTextAdapter(this);
        autoText_ed.setAdapter(adapter);
    }
    @OnClick({R.id.left_back})
    public void onClick(View view){
        if (AntiShake.check(view.getId()))return;
        switch (view.getId()){
            case R.id.left_back:
                finish();
                break;
        }
    }
}
