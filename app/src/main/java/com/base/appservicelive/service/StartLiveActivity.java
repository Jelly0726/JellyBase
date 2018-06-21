package com.base.appservicelive.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;


/**
 * Created by Administrator on 2017/3/27.
 */

public class StartLiveActivity extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public StartLiveActivity() {
        super(StartLiveActivity.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int Screen=intent.getIntExtra("Screen",0);
        if(Screen==0){
            //ScreenManager.instance.startActivity(StartLiveActivity.this);
        }else {
            //ScreenManager.instance.finishActivity();
        }
    }
}
