package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.dialog.QRDialogLogout;
import com.jelly.jellybase.databinding.ScreenshortActivityBinding;

/**
 * Created by JELLY on 2017/11/3.
 */

public class ScreenShortActivity extends BaseActivity<ScreenshortActivityBinding> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    private void iniView(){
        getBinding().button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRDialogLogout qrDialogLogout=QRDialogLogout.getInstance();
                qrDialogLogout.setRebuildBtnVisibility(View.GONE);
                qrDialogLogout.setSaveBtnVisibility(View.VISIBLE);
                qrDialogLogout.show(getSupportFragmentManager(), "QRDialogLogout");
            }
        });
    }
}
