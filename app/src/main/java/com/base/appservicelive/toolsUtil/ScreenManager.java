package com.base.appservicelive.toolsUtil;

import android.app.Activity;
import android.content.Context;

import com.base.appservicelive.LiveActivity;

import java.lang.ref.WeakReference;


/**
 * Created by Administrator on 2017/3/27.
 */
public enum ScreenManager {
    instance;
    ScreenManager() {

    }
    private WeakReference<Activity> mActivityWref;

    public void setActivity(Activity pActivity) {
        mActivityWref = new WeakReference<Activity>(pActivity);
    }

    public void startActivity(Context mContext) {
        LiveActivity.actionToLiveActivity(mContext);
    }

    public void finishActivity() {
        //结束掉LiveActivity
        if (mActivityWref != null) {
            Activity activity = mActivityWref.get();
            if (activity != null) {
                activity.finish();
            }
        }
    }
}