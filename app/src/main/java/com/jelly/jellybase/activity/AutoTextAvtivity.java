package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.jelly.baselibrary.multiClick.AntiShake;
import com.base.BaseActivity;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.AutoTextAdapter;

import butterknife.BindView;
import butterknife.OnClick;

public class AutoTextAvtivity extends BaseActivity{
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.autoText_ed)
    AutoCompleteTextView autoText_ed;
    @BindView(R.id.autoText_eds)
    AutoCompleteTextView autoText_eds;

    private AutoTextAdapter adapter;
    private ArrayAdapter<String> adapters;
    //        初始化数据源
    private String[] res = {"mukewang ","jikexueyuan","51cto"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.autotext_activity;
    }
    private void iniView(){
        adapter=new AutoTextAdapter(this);
        autoText_ed.setAdapter(adapter);

//        适配器 适配下拉列表的数据
        adapters = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, res);
//        给适配器加载数据源
        autoText_eds.setAdapter(adapters);
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
