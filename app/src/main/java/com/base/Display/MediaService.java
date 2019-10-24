package com.base.Display;

import android.app.Service;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.base.appManager.BaseApplication;
import com.base.appManager.ExecutorManager;
import com.base.eventBus.NetEvent;
import com.base.liveDataBus.LiveDataBus;
import com.base.log.DebugLog;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public class MediaService extends Service {
    private Observer observer;
    private static int position=-1;
    private MediaPlayer mMediaPlayer;
    private AudioManager audioMa;
    private SurfaceView video;
    private SHCallBack callBack;
    private Uri uri;

    private final static int MEDIA_PLAYER_NUM = 4;
    private ExecutorService mExecutorService = ExecutorManager.getInstance().getScheduledThread();
    private Queue<MediaPlayer> mMediaPlayerQueue = new ArrayDeque<>();
    private Queue<MediaPlayer> mRecycleQueue = new ArrayDeque<>();
    private final Object mAvailableLocker = new Object();
    @Override
    public void onCreate() {
        super.onCreate();
        observer=new Observer<NetEvent>() {
            @Override
            public void onChanged(@Nullable NetEvent netEvent) {
                if (!DisplayUtils.getInstance().isMultiScreen()){
                    stopSelf();
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(BaseApplication.getInstance())){
                        return;
                    }
                }
                switch (netEvent.getArg()){
                    case 0://纯视频
                        if (position!=0) {
                            if (mMediaPlayer!=null&&mMediaPlayer.isPlaying()) {
                                DisplayUtils.getInstance().setPosition(mMediaPlayer.getCurrentPosition());
                                mMediaPlayer.pause();
                                mMediaPlayer.setDisplay(null);
                            }
                            findAvailableMediaPlayer();
                            AdvertisingDislay mPresentation= (AdvertisingDislay) DisplayUtils.getInstance().showVedio(getApplicationContext());
                            if (video!=null)video.getHolder().removeCallback(callBack);
                            video = mPresentation.getVideo();
                            video.getHolder().addCallback(callBack);
                            play();
                            changeVideoSize();
                        }
                        position=0;
                        break;
                    case 1://收银
                        if (position!=1) {
                            if (mMediaPlayer!=null&&mMediaPlayer.isPlaying()) {
                                DisplayUtils.getInstance().setPosition(mMediaPlayer.getCurrentPosition());
                                mMediaPlayer.pause();
                                mMediaPlayer.setDisplay(null);
                            }
                            DisplayUtils.getInstance().show(getApplicationContext());
                        }
                        position=1;
                        break;
                    case 2://二维码
//                        if (position!=2)
                        if (mMediaPlayer!=null&&mMediaPlayer.isPlaying()) {
                            DisplayUtils.getInstance().setPosition(mMediaPlayer.getCurrentPosition());
                            mMediaPlayer.pause();
                        }
                        DisplayUtils.getInstance().showQR(getApplicationContext(),netEvent.getMsg()
                                ,netEvent.getArg0());
                        position=2;
                        break;
                }
            }
        };
        LiveDataBus.get("MediaService", NetEvent.class)
                .observeForever(observer);

        if (audioMa==null)
            audioMa = (AudioManager) BaseApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        if (callBack==null)
            callBack=new SHCallBack();
        //网络视频
        uri = Uri.parse("http://abc.dxfchs.com/video/0517/2.mp4");
        //音乐音量
        int current = audioMa.getStreamVolume( AudioManager.STREAM_MUSIC );
        //最大音量
        int MaxVolume = audioMa.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if(current!=(int)(MaxVolume * 0.5)) {
            //设置音量
            audioMa.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (MaxVolume * 0.5), AudioManager.FLAG_SHOW_UI);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void play() {
        try {
            //设置要播放的资源，可以是文件、文件路径、或者URL
            mMediaPlayer.setDataSource(BaseApplication.getInstance(),uri);
            //Sets the audio stream type for this MediaPlayer，设置流的类型，此为音乐流
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //Sets the SurfaceHolder to use for displaying the video portion of the media，设置播放的容器
//            mMediaPlayer.setDisplay(video.getHolder());
            //prepare同步的方式装载流媒体文件
            //异步的方式装载流媒体文件(推荐)
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void stop(MediaPlayer mMediaPlayer){
        if (mMediaPlayer!=null) {
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnVideoSizeChangedListener(null);
            try {
                if (mMediaPlayer.isPlaying()) {
                    DisplayUtils.getInstance().setPosition(mMediaPlayer.getCurrentPosition());
                    mMediaPlayer.stop();
                }
            } catch (IllegalStateException e) {
                // TODO 如果当前java状态和jni里面的状态不一致，
                //e.printStackTrace();
                mMediaPlayer = null;
                mMediaPlayer=new MediaPlayer();
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer.setDisplay(null);
        }
        mMediaPlayer=null;
    }
    /**
     * 修改预览View的大小,以用来适配屏幕
     */
    public void changeVideoSize() {
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();
        int deviceWidth = getResources().getDisplayMetrics().widthPixels;
        int deviceHeight = getResources().getDisplayMetrics().heightPixels;
        DebugLog.i("changeVideoSize: deviceHeight="+deviceHeight+"deviceWidth="+deviceWidth);
        float devicePercent = 0;
        //下面进行求屏幕比例,因为横竖屏会改变屏幕宽度值,所以为了保持更小的值除更大的值.
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) { //竖屏
            devicePercent = (float) deviceWidth / (float) deviceHeight; //竖屏状态下宽度小与高度,求比
        }else { //横屏
            devicePercent = (float) deviceHeight / (float) deviceWidth; //横屏状态下高度小与宽度,求比
        }

        if (videoWidth > videoHeight){ //判断视频的宽大于高,那么我们就优先满足视频的宽度铺满屏幕的宽度,然后在按比例求出合适比例的高度
            videoWidth = deviceWidth;//将视频宽度等于设备宽度,让视频的宽铺满屏幕
            videoHeight = (int)(deviceWidth*devicePercent);//设置了视频宽度后,在按比例算出视频高度

        }else {  //判断视频的高大于宽,那么我们就优先满足视频的高度铺满屏幕的高度,然后在按比例求出合适比例的宽度
            if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {//竖屏
                videoHeight = deviceHeight;
                /**
                 * 接受在宽度的轻微拉伸来满足视频铺满屏幕的优化
                 */
                float videoPercent = (float) videoWidth / (float) videoHeight;//求视频比例 注意是宽除高 与 上面的devicePercent 保持一致
                float differenceValue = Math.abs(videoPercent - devicePercent);//相减求绝对值
                if (differenceValue < 0.3){ //如果小于0.3比例,那么就放弃按比例计算宽度直接使用屏幕宽度
                    videoWidth = deviceWidth;
                }else {
                    videoWidth = (int)(videoWidth/devicePercent);//注意这里是用视频宽度来除
                }

            }else { //横屏
                videoHeight = deviceHeight;
                videoWidth = (int)(deviceHeight*devicePercent);

            }

        }
        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) video.getLayoutParams();
        layoutParams.width = videoWidth;
        layoutParams.height = videoHeight;
        video.setLayoutParams(layoutParams);

    }
    private class SHCallBack implements SurfaceHolder.Callback {
        /**
         * 在SurfaceHolder被创建的时候回调，
         * 在这里可以做一些初始化的操作
         *
         * @param holder
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //调用MediaPlayer.setDisplay(holder)设置surfaceHolder，
//            play();
            DebugLog.i("mMediaPlayer="+mMediaPlayer);
            mMediaPlayer.setDisplay(video.getHolder());
        }

        /**
         * 当SurfaceHolder的尺寸发生变化的时候被回调
         *
         * @param holder
         * @param format
         * @param width
         * @param height
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            DebugLog.i("width="+width+",height="+height);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i=0;i<mRecycleQueue.size();i++) {
            MediaPlayer mediaPlayer = mRecycleQueue.poll();
            stop(mediaPlayer);
        }
        for (int i=0;i<mMediaPlayerQueue.size();i++) {
            MediaPlayer mediaPlayer = mMediaPlayerQueue.poll();
            stop(mediaPlayer);
        }
        if (video!=null){
            video.getHolder().removeCallback(callBack);
        }
        video=null;
        callBack=null;
        DisplayUtils.getInstance().dismiss();
        if (observer!=null)
            LiveDataBus.get("MediaService").removeObserver(observer);
    }
    /**
     * create a new player and release current
     * mMediaPlayer.reset(); cost too much time
     * release() 在音频未加载完时，也是耗时操作
     * 但是用户快速点击切换播放音乐，有问题
     *
     * 没有到限制播放器数量，直接新建
     * 存在 可用队列 为空 的情况，即所有的 player 都在 reset ，此时直接新建
     *
     */
    private void findAvailableMediaPlayer() {
        try {
            if (currentPlayerNumLegal() || mMediaPlayerQueue.isEmpty()) {
                DebugLog.i("create a new media player available size: " + mMediaPlayerQueue.size() + " recycle size: " + mRecycleQueue.size());
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setLooping(true);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnPreparedListener(mPreparedListener);
                mediaPlayer.setOnCompletionListener(mCompletionListener);
                mediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
                queueAvailableMediaPlayer(mediaPlayer);
            }
            if (mMediaPlayer != null) {
                // 加入回收队列，待处理 释放
                synchronized (mAvailableLocker) {
                    mRecycleQueue.add(mMediaPlayer);
                }
            }
            // 此处 mMediaPlayer not null
            DebugLog.i("availble media player size: " + mMediaPlayerQueue.size());
            synchronized (mAvailableLocker) {
                mMediaPlayer = mMediaPlayerQueue.poll();
            }

            if(mRecycleQueue.size() > 0) {
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        MediaPlayer player;
                        synchronized (mAvailableLocker) {
                            player = mRecycleQueue.poll();
                        }
                        DebugLog.i("media player reset... ");
                        if (player.isPlaying())
                            player.stop();
                        player.setDisplay(null);
                        player.reset();
                        player.release();
                        DebugLog.i("media player reset done... ");
                        queueAvailableMediaPlayer(player);
                    }
                });
            }

        } catch (Exception e) {
            DebugLog.e("media player error: " + e.getMessage());
        }
    }

    private boolean currentPlayerNumLegal(){
        synchronized (mAvailableLocker) {
            return MEDIA_PLAYER_NUM > mMediaPlayerQueue.size() + mRecycleQueue.size();
        }
    }

    private void queueAvailableMediaPlayer(MediaPlayer player){
        synchronized (mAvailableLocker) {
            mMediaPlayerQueue.add(player);
            DebugLog.i("media player size: " + mMediaPlayerQueue.size());
        }
    }

    private MediaPlayer.OnPreparedListener mPreparedListener =
            new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    DebugLog.i("media player prepared ...");
                    mp.start();
                    mp.seekTo(DisplayUtils.getInstance().getPosition());
                }
            };

    private MediaPlayer.OnCompletionListener mCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    DebugLog.i("media player completed ...");
                    mp.start();
                }
            };
    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener
            =new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
            if (video!=null) {
                changeVideoSize();
            }
        }
    };
}