package com.base.Display;

import android.app.Presentation;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.base.appManager.BaseApplication;
import com.jelly.jellybase.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AdvertisingDislay extends Presentation {
    private Unbinder unbinder;
    @BindView(R.id.video)
    SurfaceView video;
    private MediaPlayer mediaPlayer;
    public AdvertisingDislay(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public AdvertisingDislay(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertising_dislay);
        unbinder= ButterKnife.bind(this);
        mediaPlayer=new MediaPlayer();
        video.getHolder().addCallback(new SHCallBack());
    }

    @Override
    public void show() {
        super.show();
        iniView();
    }

    private void iniView(){
    }

    @Override
    public void dismiss() {
        DisplayUtils.getInstance().setPosition(mediaPlayer.getCurrentPosition());
        if (unbinder!=null)
            unbinder.unbind();
        super.dismiss();

    }

    private void play() {
        //网络视频
        Uri uri = Uri.parse("http://abc.dxfchs.com/video/0517/2.mp4");
        try {
            //设置要播放的资源，可以是文件、文件路径、或者URL
            mediaPlayer.setDataSource(BaseApplication.getInstance(),uri);
            //Sets the audio stream type for this MediaPlayer，设置流的类型，此为音乐流
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //Sets the SurfaceHolder to use for displaying the video portion of the media，设置播放的容器
            mediaPlayer.setDisplay(video.getHolder());
            mediaPlayer.setVolume(10f,10f);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //调用MediaPlayer.start()来播放视频
                    mediaPlayer.start();
                    mediaPlayer.seekTo(DisplayUtils.getInstance().getPosition());
                    mediaPlayer.setLooping(true);
                }
            });
            //调用MediaPlayer.prepare()来准备
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();

                }
            });
            mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                    changeVideoSize();
                }
            });

        } catch (Exception e) {

        }
    }
    //设置自适应
    public void changeVideoSize() {
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();

        //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
        float max;
        if (getResources().getConfiguration().orientation== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //竖屏模式下按视频宽度计算放大倍数值
            max = Math.max((float) videoWidth / (float) video.getWidth(),(float) videoHeight / (float) video.getHeight());
        } else{
            //横屏模式下按视频高度计算放大倍数值
            max = Math.max(((float) videoWidth/(float) video.getWidth()),(float) videoHeight/(float) video.getHeight());
        }

        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
        videoWidth = (int) Math.ceil((float) videoWidth / max);
        videoHeight = (int) Math.ceil((float) videoHeight / max);

        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
        video.setLayoutParams(new RelativeLayout.LayoutParams(videoWidth, videoHeight));
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
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
            // surfaceHolder可以通过surfaceview的getHolder()方法获得
            play();
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

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    }
}
