package com.base.appservicelive.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.support.annotation.RequiresApi;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        // 当 API > 18 时，使用 extras 获取通知的详细信息
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!CommonStaticUtil.isServiceRunning(this
                    , NotificationService.class.getName())) {
                //Log.i(TAG, "Service 没有开启，开启NotificationService服务........>");
                CommonStaticUtil.starNotificationService(this);
            }
        }else {
            if (!CommonStaticUtil.isServiceRunning(this
                    , AccessibilityServices.class.getName())) {
                //Log.i(TAG, "Service 没有开启，开启NotificationService服务........>");
                CommonStaticUtil.starAccessibilityService(this);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isHome= AppPrefs.getBoolean(MyApplication.getMyApp(), ConfigKey.ISHOME,false);
        if(isHome) {//是否按了home键
            //======================启动前台服务========================//
            // 当 API > 18 时，使用context.startForegroundService()启动前台服务 需要在onCreate（）时调用startForeground（）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(BaseConfig.CHANNEL_ID, getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_HIGH);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);
                Notification notification = new Notification.Builder(getApplicationContext(), BaseConfig.CHANNEL_ID).build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
                startForeground(BaseConfig.SERVICE_ID, notification);
            }else {
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
            }
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
     *
     * 如果你的应用想停止某个任务，你可以调用JobScheduler对象的cancel(int jobId)来实现；
     * 如果你想取消所有的任务，你可以调用JobScheduler对象的cancelAll()来实现。
     */
    private void jobSchedulerServiceStart(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(getPackageName(), JobSchedulerService.class.getName()));
            //builder.setMinimumLatency(2000); // 设置任务允许最少延迟时间 这个方法也会与setPeriodic(long time)，同时调用这两个方法会引发异常。
            //builder.setOverrideDeadline(50000); // 设置deadline,若到期还没有到达规定的条件也会执行 这个方法也会与setPeriodic(long time)，同时调用这两个方法会引发异常。
            //builder.setRequireNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); //设置网络条件，非蜂窝数据的
            //builder.setRequiresCharging(true); // 指定要运行这个作业，需要插入设备充电 默认false
            builder.setPeriodic(mPeriodTime);  //确定该工作应在提供的时间间隔内重复，而不是超过一次
            builder.setPersisted(true);        //设置设备重启后，是否重新执行任务
            builder.setRequiresDeviceIdle(true);//这个方法告诉你的任务只有当用户没有在使用该设备且有一段时间 没有使用时才会启动该任务。

            /**
             * 如果schedule方法失败了，它会返回一个小于0的错误码
             * 否则它会我们在JobInfo.Builder中定义的标识id。
             */
            if (jobScheduler.schedule(builder.build()) <= 0){
                // if something goes wrong
                Toast.makeText(this, "JobScheduler start faild", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
