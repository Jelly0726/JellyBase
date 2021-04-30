package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.text.method.NumberKeyListener;
import android.view.View;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.idcart.IDCard;
import com.jelly.baselibrary.idcart.IDCardUtils;
import com.jelly.baselibrary.idcart.IDEntity;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.IdcartActivityBinding;

public class IDCartActivity extends BaseActivity<IdcartActivityBinding> implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBinding().leftBack.setOnClickListener(this);
        getBinding().IDCardBtn.setOnClickListener(this);
        getBinding().IDCardUtilsBtn.setOnClickListener(this);
        getBinding().IDEntityBtn.setOnClickListener(this);
        getBinding().IDCardEdit.setKeyListener(new NumberKeyListener() {
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
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.IDCard_btn:
                String IDCar= getBinding().IDCardEdit.getText().toString().toUpperCase();
                if (!IDCard.IDCardValidate(IDCar)){
                    ToastUtils.showShort(this, "无效");
                }
                break;
            case R.id.IDCardUtils_btn:
                String IDCardUtil= getBinding().IDCardEdit.getText().toString();
                if (!IDCardUtils.validateCard(IDCardUtil)){
                    ToastUtils.showShort(this, "无效");
                }
                break;
            case R.id.IDEntity_btn:
                String IDEntit= getBinding().IDCardEdit.getText().toString();
                if (!IDEntity.checkIDCard(IDEntit)){
                    ToastUtils.showShort(this, "无效");
                }
                break;
        }
    }
}
