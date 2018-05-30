package com.base.appservicelive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.base.applicationUtil.AppPrefs;
import com.base.appservicelive.toolsUtil.ScreenManager;


/**
 * Created by Administrator on 2017/3/27.
 */

public class LiveActivity extends Activity {

    public static final String TAG = LiveActivity.class.getSimpleName();

    public static void actionToLiveActivity(Context pContext) {
        AppPrefs.putInt(pContext,"from",0);
        Intent intent = new Intent(pContext, LiveActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int from=AppPrefs.getInt(this,"from",0);
        if (from==1) {
            finish();
//            Intent intent = new Intent();
//            intent.setAction(IntentAction.JPUSH_CLICK);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
            return;
        }
        Window window = getWindow();
        //放在左上角
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams attributes = window.getAttributes();
        //宽高设计为1个像素
        attributes.width = 1;
        attributes.height = 1;
        //起始坐标
        attributes.x = 0;
        attributes.y = 0;
        window.setAttributes(attributes);
        ScreenManager.instance.setActivity(this);
    }
    @Override
    public void onBackPressed() {
        ScreenManager.instance.finishActivity();
        //super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        AppPrefs.putInt(this,"from",1);
        super.onDestroy();
    }
}