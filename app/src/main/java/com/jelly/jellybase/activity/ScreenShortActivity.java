package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.base.circledialog.QRDialogLogout;
import com.jelly.jellybase.R;

/**
 * Created by JELLY on 2017/11/3.
 */

public class ScreenShortActivity extends AppCompatActivity{
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
