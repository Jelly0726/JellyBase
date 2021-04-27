package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jelly.baselibrary.Utils.AntiEmulator;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.jellybase.R;

import org.jetbrains.annotations.Nullable;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 判断是否真机
 */
public class EmulatorOrAuthenticActivity extends BaseActivity {
    @BindView(R.id.result)
    TextView result;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public int getLayoutId(){
        return R.layout.emulator_or_authentic_activity;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @OnClick({R.id.back_layout,R.id.start,R.id.startJniNorm,R.id.startJni})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back_layout:
                finish();
                break;
            case R.id.start:
                result.setText("Java模式验证结果当前运行环境为："+(AntiEmulator.verify(this)? "模拟器":"真机"));
                break;
            case R.id.startJniNorm://标准模式
                result.setText("标准模式验证结果当前运行环境为："+(diff.strazzere.anti.AntiEmulator.check(this)?"模拟器":"真机"));
                break;
            case R.id.startJni://安全模式
                result.setText("安全模式验证结果当前运行环境为："+(diff.strazzere.anti.AntiEmulator.checkSafely(this)?"模拟器":"真机"));
                break;
        }
    }
}
