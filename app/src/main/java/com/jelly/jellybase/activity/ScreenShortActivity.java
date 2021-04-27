package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.jelly.baselibrary.dialog.QRDialogLogout;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.jellybase.R;

/**
 * Created by JELLY on 2017/11/3.
 */

public class ScreenShortActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.screenshort_activity;
    }
    private void iniView(){
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
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
