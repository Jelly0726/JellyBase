package com.jelly.jellybase.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.base.appbadge.BadgeUtils;
import com.base.BaseActivity;
import com.jelly.jellybase.R;
import com.jelly.jellybase.server.BadgeIntentService;

import me.leolin.shortcutbadger.ShortcutBadger;

public class BadgeMainActivity extends BaseActivity {
    private EditText mCountEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCountEditText = findViewById(R.id.et_count);
        findViewById(R.id.btn_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int count = Integer.parseInt(mCountEditText.getText().toString());
                    if (BadgeUtils.setCount(count, BadgeMainActivity.this)) {
                        Toast.makeText(BadgeMainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BadgeMainActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(BadgeMainActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.btn_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int count = Integer.parseInt(mCountEditText.getText().toString());
                    if (BadgeUtils.setNotificationBadge(count, BadgeMainActivity.this)) {
                        Toast.makeText(BadgeMainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BadgeMainActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(BadgeMainActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button button = findViewById(R.id.btnSetBadge);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int badgeCount = 0;
                try {
                    badgeCount = Integer.parseInt(mCountEditText.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Error input", Toast.LENGTH_SHORT).show();
                }
                if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
                    finish();
                    startService(
                            new Intent(BadgeMainActivity.this, BadgeIntentService.class).putExtra("badgeCount", badgeCount)
                    );
                    return;
                }
                boolean success = ShortcutBadger.applyCount(BadgeMainActivity.this, badgeCount);
                Toast.makeText(getApplicationContext(), "Set count=" + badgeCount + ", success=" + success, Toast.LENGTH_SHORT).show();
            }
        });

        Button launchNotification = findViewById(R.id.btnSetBadgeByNotification);
        launchNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int badgeCount = 0;
                try {
                    badgeCount = Integer.parseInt(mCountEditText.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Error input", Toast.LENGTH_SHORT).show();
                }

                finish();
                startService(
                        new Intent(BadgeMainActivity.this, BadgeIntentService.class).putExtra("badgeCount", badgeCount)
                );
            }
        });

        Button removeBadgeBtn = findViewById(R.id.btnRemoveBadge);
        removeBadgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = ShortcutBadger.removeCount(BadgeMainActivity.this);

                Toast.makeText(getApplicationContext(), "success=" + success, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getLayoutId(){
        return R.layout.badge_main_activity;
    }
}