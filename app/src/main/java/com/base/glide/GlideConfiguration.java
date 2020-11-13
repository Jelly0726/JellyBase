package com.base.glide;
import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by zhaoyong on 2016/1/26.
 * 增加图片清晰度
 * 解决Glide由于过度压缩导致了图片变绿部分机型未能解决需要加上.diskCacheStrategy(DiskCacheStrategy.SOURCE) //查有道翻译了一下，大概意思是将图片完整缓存
 *  例如：Glide.with(this).load("你的图片地址").diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mivTestImage);
 */
public class GlideConfiguration implements GlideModule{
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
//        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {

    }
}
