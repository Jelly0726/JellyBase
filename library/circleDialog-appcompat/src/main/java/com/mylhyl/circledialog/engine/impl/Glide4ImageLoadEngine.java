package com.mylhyl.circledialog.engine.impl;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mylhyl.circledialog.engine.ImageLoadEngine;


/**
 * Created by hupei on 2019/1/14 15:28.
 */
@Deprecated
public final class Glide4ImageLoadEngine implements ImageLoadEngine {

    @Override
    public void loadImage(Context context, ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .skipMemoryCache(true)//不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存
                .dontAnimate()
                .into(imageView);
    }
}
