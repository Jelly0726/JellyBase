package com.base.Display;

import android.app.Presentation;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
            mediaPlayer.setDisplay(video.getHolder());
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

        } catch (Exception e) {

        }
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
