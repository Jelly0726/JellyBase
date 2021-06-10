package com.jelly.multiClick;

import android.view.View;

/**
 * 防止过快点击造成多次事件执行（防止按钮重复点击）
 */
public abstract class OnMultiClickListener implements View.OnClickListener{

    public abstract void onMultiClick(View v);

    @Override
    public void onClick(View v) {
        if(!AntiShake.check(v)) {
            onMultiClick(v);
        }
    }
}