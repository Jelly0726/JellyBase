package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jelly.baselibrary.idcart.IDCard;
import com.jelly.baselibrary.idcart.IDCardUtils;
import com.jelly.baselibrary.idcart.IDEntity;
import com.jelly.baselibrary.toast.ToastUtils;
import com.base.BaseActivity;
import com.jelly.jellybase.R;

import butterknife.BindView;
import butterknife.OnClick;

public class IDCartActivity extends BaseActivity{
    @BindView(R.id.IDCard_edit)
    EditText IDCard_edit;
    @BindView(R.id.IDCard_btn)
    Button IDCard_btn;
    @BindView(R.id.IDCardUtils_btn)
    Button IDCardUtils_btn;
    @BindView(R.id.IDEntity_btn)
    Button IDEntity_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IDCard_edit.setKeyListener(new NumberKeyListener() {
            @Override
            public int getInputType() {
                return android.text.InputType.TYPE_CLASS_PHONE;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] numberChars = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'X','x' };
                return numberChars;
            }
        });

    }
    @Override
    public int getLayoutId(){
        return R.layout.idcart_activity;
    }
    @OnClick({R.id.left_back,R.id.IDCard_btn,R.id.IDCardUtils_btn,R.id.IDEntity_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.IDCard_btn:
                String IDCar=IDCard_edit.getText().toString().toUpperCase();
                if (!IDCard.IDCardValidate(IDCar)){
                    ToastUtils.showShort(this, "无效");
                }
                break;
            case R.id.IDCardUtils_btn:
                String IDCardUtil=IDCard_edit.getText().toString();
                if (!IDCardUtils.validateCard(IDCardUtil)){
                    ToastUtils.showShort(this, "无效");
                }
                break;
            case R.id.IDEntity_btn:
                String IDEntit=IDCard_edit.getText().toString();
                if (!IDEntity.checkIDCard(IDEntit)){
                    ToastUtils.showShort(this, "无效");
                }
                break;
        }
    }
}
