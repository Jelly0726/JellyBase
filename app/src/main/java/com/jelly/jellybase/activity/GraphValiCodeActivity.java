package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.base.view.GraphValiCode;
import com.jelly.jellybase.MainActivity;
import com.jelly.jellybase.R;

/**
 * Created by Administrator on 2018/1/8.
 */

public class GraphValiCodeActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = MainActivity.class.getName();
    private ImageView iv_showCode;
    private EditText et_phoneCode;
    private EditText et_phoneNum;
    //产生的验证码
    private String realCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphvalicode_activity);

        et_phoneCode = (EditText) findViewById(R.id.et_phoneCodes);
        Button but_toSetCode = (Button) findViewById(R.id.but_forgetpass_toSetCodes);
        but_toSetCode.setOnClickListener(this);
        iv_showCode = (ImageView) findViewById(R.id.iv_showCode);
        //将验证码用图片的形式显示出来
        iv_showCode.setImageBitmap(GraphValiCode.getInstance().createBitmap());
        realCode = GraphValiCode.getInstance().getCode().toLowerCase();
        iv_showCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_showCode:
                iv_showCode.setImageBitmap(GraphValiCode.getInstance().createBitmap());
                realCode = GraphValiCode.getInstance().getCode().toLowerCase();
                Log.v(TAG,"realCode"+realCode);
                break;
            case R.id.but_forgetpass_toSetCodes:
                String phoneCode = et_phoneCode.getText().toString().toLowerCase();
                String msg = "生成的验证码："+realCode+"输入的验证码:"+phoneCode;
                Toast.makeText(this,msg,Toast.LENGTH_LONG).show();

                if (phoneCode.equals(realCode)) {
                    Toast.makeText(this, phoneCode + "验证码正确", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, phoneCode + "验证码错误", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}