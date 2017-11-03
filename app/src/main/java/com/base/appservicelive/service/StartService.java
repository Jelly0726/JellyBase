package com.base.appservicelive.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.base.applicationUtil.AppPrefs;
import com.base.applicationUtil.MyApplication;
import com.base.appservicelive.toolsUtil.CommonStaticUtil;
import com.base.config.ConfigKey;


/**
 * Created by Administrator on 2017/4/10.
 */

public class StartService extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public StartService() {
        super(StartService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Class cla= (Class) intent.getSerializableExtra("class");
        if(!CommonStaticUtil.isServiceRunning(this
                ,cla.getName())) {
            AppPrefs
                    .putBoolean(MyApplication.getMyApp(), ConfigKey.ISRUN, true);
            Intent stateGuardService = new Intent(MyApplication.getMyApp(),cla);
            startService(stateGuardService);
        }
    }
}
