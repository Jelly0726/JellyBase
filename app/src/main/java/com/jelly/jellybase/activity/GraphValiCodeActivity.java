package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.valicode.GraphValiCode;
import com.jelly.jellybase.MainActivity;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.GraphvalicodeActivityBinding;

/**
 * Created by Administrator on 2018/1/8.
 */

public class GraphValiCodeActivity extends BaseActivity<GraphvalicodeActivityBinding> implements View.OnClickListener{
    public static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewBinding().butForgetpassToSetCodes.setOnClickListener(this);
        getViewBinding().ivShowCode.setOnClickListener(this);
    }
    @Override
    protected void onDestroy() {
        GraphValiCode.getInstance().onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //将验证码用图片的形式显示出来
        getViewBinding().ivShowCode.setImageBitmap(GraphValiCode.getInstance().createBitmap());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_showCode:
                getViewBinding().ivShowCode.setImageBitmap(GraphValiCode.getInstance().createBitmap());
                break;
            case R.id.but_forgetpass_toSetCodes:
                String phoneCode = getViewBinding().etPhoneCodes.getText().toString().toLowerCase();
                String msg = "生成的验证码："+GraphValiCode.getInstance().getCode()+"输入的验证码:"+phoneCode;
                Toast.makeText(this,msg,Toast.LENGTH_LONG).show();

                if (GraphValiCode.getInstance().verify(phoneCode)) {
                    Toast.makeText(this, phoneCode + "验证码正确", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, phoneCode + "验证码错误", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}