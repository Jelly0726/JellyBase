package com.jelly.jellybase.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.jelly.jellybase.R;

import java.util.ArrayList;
import java.util.List;

public class CrashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_activity);
        findViewById(R.id.crash_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> resulr = new ArrayList<String>();
                resulr.add("XXXXXXXX");
                String s = resulr.get(1);
            }
        });
    }
}
