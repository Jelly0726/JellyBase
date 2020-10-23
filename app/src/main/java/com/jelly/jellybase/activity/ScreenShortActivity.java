package com.jelly.jellybase.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.base.dialog.QRDialogLogout;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;

/**
 * Created by JELLY on 2017/11/3.
 */

public class ScreenShortActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screenshort_activity);
        iniView();
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
