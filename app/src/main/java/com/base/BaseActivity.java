package com.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.base.bankcard.BandCardEditText;
import com.base.nodeprogress.NodeProgressDemo;
import com.base.zxing.ScanerCodeActivity;
import com.jelly.jellybase.R;
import com.zhy.autolayout.AutoLayoutActivity;

public class BaseActivity extends AutoLayoutActivity {
    private EditText bankNo;
    private Button saomiao;
    private Button kuaidi;
    private BandCardEditText bankCardEditText;
    private TextView tv_desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);
        bankNo=(EditText)findViewById(R.id.bankNo);

        tv_desc = (TextView) findViewById(R.id.tv_desc);
        bankCardEditText = (BandCardEditText) findViewById(R.id.bankCardEditText);
        bankCardEditText.setBankCardListener(new BandCardEditText.BankCardListener() {
            @Override
            public void success(String name) {
                tv_desc.setText(name);
            }

            @Override
            public void failure() {
                tv_desc.setText("没有查到所属银行");
            }
        });
        saomiao=(Button) findViewById(R.id.saomiao);
        saomiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(BaseActivity.this, ScanerCodeActivity.class);
                startActivityForResult(intent,1);
            }
        });
        kuaidi= (Button) findViewById(R.id.kuaidi);
        kuaidi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(BaseActivity.this, NodeProgressDemo.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==300){
            if(data==null){
                return;
            }else {
                String result_value = data.getStringExtra("result");
                bankNo.setText(result_value);
                bankNo.setSelection(result_value.length());
            }
        }
    }
}
