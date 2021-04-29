package com.jelly.jellybase.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.base.appbadge.BadgeUtils;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.jellybase.databinding.BadgeMainActivityBinding;
import com.jelly.jellybase.server.BadgeIntentService;

import me.leolin.shortcutbadger.ShortcutBadger;

public class BadgeMainActivity extends BaseActivity<BadgeMainActivityBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewBinding().btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int count = Integer.parseInt(getViewBinding().etCount.getText().toString());
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
       getViewBinding().btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int count = Integer.parseInt(getViewBinding().etCount.getText().toString());
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
        getViewBinding().btnSetBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int badgeCount = 0;
                try {
                    badgeCount = Integer.parseInt(getViewBinding().etCount.getText().toString());
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

        getViewBinding().btnSetBadgeByNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int badgeCount = 0;
                try {
                    badgeCount = Integer.parseInt(getViewBinding().etCount.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Error input", Toast.LENGTH_SHORT).show();
                }

                finish();
                startService(
                        new Intent(BadgeMainActivity.this, BadgeIntentService.class).putExtra("badgeCount", badgeCount)
                );
            }
        });

        getViewBinding().btnRemoveBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = ShortcutBadger.removeCount(BadgeMainActivity.this);

                Toast.makeText(getApplicationContext(), "success=" + success, Toast.LENGTH_SHORT).show();
            }
        });
    }
}