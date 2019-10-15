package com.base.Display;

import android.app.Presentation;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.base.appManager.BaseApplication;
import com.base.applicationUtil.AppUtils;
import com.base.eventBus.NetEvent;
import com.base.liveDataBus.LiveDataBus;
import com.base.recyclerViewUtil.ItemDecoration;
import com.jelly.jellybase.R;

import java.lang.reflect.Member;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 客显
 */
public class DifferentDislay extends Presentation{
    private Unbinder unbinder;
    @BindView(R.id.video)
    SurfaceView video;
    private MediaPlayer mediaPlayer;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView.ItemDecoration mItemDecoration;
    private Observer observer;
    private Member mMember;
    private double discountRate =10;//折扣
    private double deduction =0;//优惠金
    public DifferentDislay(Context outerContext, Display display) {
        super(outerContext,display);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.different_dislay);
        unbinder= ButterKnife.bind(this);
        iniView();
        iniRecyclerView();
        mediaPlayer=new MediaPlayer();
        video.getHolder().addCallback(new SHCallBack());
    }

    @Override
    public void show() {
        super.show();
        iniRegister();
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
            //当装载流媒体完毕的时候回调。
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //调用MediaPlayer.start()来播放视频
                    mediaPlayer.start();
                    mediaPlayer.seekTo(DisplayUtils.getInstance().getPosition());
                    mediaPlayer.setLooping(true);
                }
            });
            //prepare同步的方式装载流媒体文件
            //异步的方式装载流媒体文件(推荐)
            mediaPlayer.prepareAsync();
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
    @Override
    public void dismiss() {
        DisplayUtils.getInstance().setPosition(mediaPlayer.getCurrentPosition());
        if (unbinder!=null)
            unbinder.unbind();
        if (observer!=null)
            LiveDataBus.get("CashierDeskActivity").removeObserver(observer);
        super.dismiss();

    }

    private void iniView(){
    }
    private void iniRecyclerView(){
    }
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(this.getContext());
    }

    protected RecyclerView.ItemDecoration createItemDecoration() {
//        return new DefaultItemDecoration(ContextCompat.getColor(getActivity(), R.color.white),
//                AppUtils.dipTopx(getActivity(),0 )
//        ,AppUtils.dipTopx(getActivity(),0 ));
        Rect rect=new Rect();
        rect.top= AppUtils.dipTopx(this.getContext(),0 );
        rect.bottom= AppUtils.dipTopx(this.getContext(),0 );
        rect.left= AppUtils.dipTopx(this.getContext(),0 );
        rect.right= AppUtils.dipTopx(this.getContext(),0 );
        return new ItemDecoration(rect,0, ItemDecoration.HEAD, ContextCompat.getColor(this.getContext()
                .getApplicationContext(), R.color.transparent));
    }

    /**
     * 注册订阅
     */
    private void iniRegister(){
        observer=new Observer<NetEvent>() {
            @Override
            public void onChanged(@Nullable NetEvent netEvent) {

            }
        };
        LiveDataBus.get("CashierDeskActivity", NetEvent.class)
                .observeForever(observer);
    }
}