package com.base.appservicelive.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.base.applicationUtil.AppPrefs;
import com.base.applicationUtil.MyApplication;
import com.base.appservicelive.receiver.ScreenBroadcastListener;
import com.base.appservicelive.toolsUtil.CommonStaticUtil;
import com.base.config.BaseConfig;
import com.base.config.ConfigKey;
import com.base.config.IntentAction;
import com.coolerfall.daemon.Daemon;
import com.jelly.jellybase.R;
import com.jelly.jellybase.server.JobSchedulerService;
import com.jelly.jellybase.server.WokeService;


/**后台不死服务
 * Created by Administrator on 2016/9/6.
 */
public class LiveService extends Service {
    private static final String TAG = LiveService.class.getName();
    private BroadcastReceiver mBR;
    private IntentFilter mIF;
    private long mPeriodTime = 60*1000;             // jobSchedulerService 运行周期
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Daemon.run(this,
                LiveService.class, Daemon.INTERVAL_ONE_MINUTE);
        mBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                Intent a = new Intent(LiveService.this, GuardService.class);
                startService(a);
            }
        };
        mIF = new IntentFilter();
        mIF.addAction("GuardService");
        registerReceiver(mBR, mIF);

        if (Build.VERSION.SDK_INT < 18) {
            //API < 18 ，此方法能有效隐藏Notification上的图标
            startForeground(BaseConfig.SERVICE_ID, new Notification());
        } else {
            Intent innerIntent = new Intent(this, DaemonInnerService.class);
            startService(innerIntent);
            startForeground(BaseConfig.SERVICE_ID, new Notification());
        }

        CommonStaticUtil.startReceiver(this);
        //Log.i("msg","LiveService创建");
        jobSchedulerServiceStart();
        //↓↓↓屏幕关闭的时候启动一个1像素的Activity，开屏的时候关闭Activity↓↓↓
        ScreenBroadcastListener listener = new ScreenBroadcastListener(this);
        listener.registerListener(new ScreenBroadcastListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                Intent intent=new Intent(LiveService.this,StartLiveActivity.class);
                intent.putExtra("Screen",1);
                startService(intent);
            }
            @Override
            public void onScreenOff() {
                Intent intent=new Intent(LiveService.this,StartLiveActivity.class);
                intent.putExtra("Screen",0);
                startService(intent);
            }
        });
        //↑↑↑↑屏幕关闭的时候启动一个1像素的Activity，开屏的时候关闭Activity↑↑↑↑

        if(!CommonStaticUtil.isServiceRunning(this
                , NotificationService.class.getName())){
            //Log.i(TAG, "Service 没有开启，开启NotificationService服务........>");
            Intent c = new Intent(this, NotificationService.class);
            this.startService(c);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isHome= AppPrefs.getBoolean(MyApplication.getMyApp(), ConfigKey.ISHOME,false);
        if(isHome) {//是否按了home键
            //======================启动前台服务========================//
            Intent notificationIntent = new Intent();
            notificationIntent.setAction(IntentAction.JPUSH_CLICK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification noti = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.app_name))
                    //.setSmallIcon(R.mipmap.icon_logo)
                    .setContentIntent(pendingIntent)
                    .build();
            noti.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
            startForeground(BaseConfig.SERVICE_ID, noti);
            //======================启动前台服务========================//
        }else {
            stopForeground(true);
        }
        if(!CommonStaticUtil.isServiceRunning(this
                , WokeService.class.getName())){
            //Log.i(TAG, "WokeService 没有开启，开启WokeService服务........>");
//            Intent c = new Intent(this, WokeService.class);
//            c.setAction(BaseBroadcast.SET_ALARM);
//            this.startService(c);
        }
        return Service.START_STICKY;
    }
    @Override
    public void onDestroy() {

        Intent intent = new Intent();
        intent.setAction("LiveService");
        sendBroadcast(intent);

        unregisterReceiver(mBR);
        stopForeground(true);
        //Log.i("msg","LiveService关闭");
        super.onDestroy();
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class DaemonInnerService extends Service {
        static final String TAG = DaemonInnerService.class.getName();

        @Override
        public void onCreate() {
            Log.d(TAG, "InnerService -> onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d(TAG, "InnerService -> onStartCommand");
            startForeground(BaseConfig.SERVICE_ID, new Notification());
            //stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, "InnerService -> onDestroy");
            super.onDestroy();
        }
    }

	/***
     * JobScheduler 周期性检测服务是否被关闭
     */
    private void jobSchedulerServiceStart(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(getPackageName(), JobSchedulerService.class.getName()));

            builder.setPeriodic(mPeriodTime);
            builder.setRequiresCharging(true);
            builder.setPersisted(true);                 //设置设备重启后，是否重新执行任务
            builder.setRequiresDeviceIdle(true);

            if (jobScheduler.schedule(builder.build()) <= 0){
                // if something goes wrong
                Toast.makeText(this, "JobScheduler start faild", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
