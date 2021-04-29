package com.jelly.jellybase.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.GsyvideoActivityBinding;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

/**
 * 视频播放器gsyVideoPlayer
 * 文档地址 https://github.com/CarGuo/GSYVideoPlayer/wiki
 */
public class GSYVideoActivity extends BaseActivity<GsyvideoActivityBinding> {
    OrientationUtils orientationUtils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }
    private void init() {
        String source1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
        getViewBinding().videoPlayer.setUp(source1, true, "测试视频");
        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.ic_launcher);
        getViewBinding().videoPlayer.setThumbImageView(imageView);
        //增加title
        getViewBinding().videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        getViewBinding().videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
        orientationUtils = new OrientationUtils(this, getViewBinding().videoPlayer);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        getViewBinding().videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationUtils.resolveByClick();
            }
        });
        //是否可以滑动调整
        getViewBinding().videoPlayer.setIsTouchWiget(true);
        //设置返回按键功能
        getViewBinding().videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getViewBinding().videoPlayer.startPlayLogic();
    }


    @Override
    protected void onPause() {
        super.onPause();
        getViewBinding().videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getViewBinding().videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            getViewBinding().videoPlayer.getFullscreenButton().performClick();
            return;
        }
        //释放所有
        getViewBinding().videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }
}
