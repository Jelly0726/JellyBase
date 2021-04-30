package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.Utils.AntiEmulator;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.EmulatorOrAuthenticActivityBinding;

import org.jetbrains.annotations.Nullable;

/**
 * 判断是否真机
 */
public class EmulatorOrAuthenticActivity extends BaseActivity<EmulatorOrAuthenticActivityBinding> implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBinding().backLayout.setOnClickListener(this);
        getBinding().start.setOnClickListener(this);
        getBinding().startJni.setOnClickListener(this);
        getBinding().startJniNorm.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                finish();
                break;
            case R.id.start:
                getBinding().result.setText("Java模式验证结果当前运行环境为：" + (AntiEmulator.verify(this) ? "模拟器" : "真机"));
                break;
            case R.id.startJniNorm://标准模式
                getBinding().result.setText("标准模式验证结果当前运行环境为：" + (diff.strazzere.anti.AntiEmulator.check(this) ? "模拟器" : "真机"));
                break;
            case R.id.startJni://安全模式
                getBinding().result.setText("安全模式验证结果当前运行环境为：" + (diff.strazzere.anti.AntiEmulator.checkSafely(this) ? "模拟器" : "真机"));
                break;
        }
    }
}
