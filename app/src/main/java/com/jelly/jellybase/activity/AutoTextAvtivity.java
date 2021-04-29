package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.AutoTextAdapter;
import com.jelly.jellybase.databinding.AutotextActivityBinding;

public class AutoTextAvtivity extends BaseActivity<AutotextActivityBinding> implements View.OnClickListener {
    private AutoTextAdapter adapter;
    private ArrayAdapter<String> adapters;
    //        初始化数据源
    private String[] res = {"mukewang ","jikexueyuan","51cto"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    private void iniView(){
        getViewBinding().leftBack.setOnClickListener(this);
        adapter=new AutoTextAdapter(this);
        getViewBinding().autoTextEd.setAdapter(adapter);

//        适配器 适配下拉列表的数据
        adapters = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, res);
//        给适配器加载数据源
        getViewBinding().autoTextEds.setAdapter(adapters);
    }
    public void onClick(View view){
        if (AntiShake.check(view.getId()))return;
        switch (view.getId()){
            case R.id.left_back:
                finish();
                break;
        }
    }
}
