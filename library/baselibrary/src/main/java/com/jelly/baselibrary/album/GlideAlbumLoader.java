/*
 * Copyright © Yan Zhenjie. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jelly.baselibrary.album;

import android.os.Build;
import android.os.Environment;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.jelly.baselibrary.log.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;

import java.io.File;

/**
 * Created by Yan Zhenjie on 2017/3/31.
 */
public class GlideAlbumLoader implements AlbumLoader {
    @Override
    public void load(ImageView imageView, AlbumFile albumFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !Environment.isExternalStorageLegacy() &&
                albumFile.getUri() != null) {//大于等于29并且应用以分区存储特性运行
            Glide.with(imageView.getContext())
                    .load(albumFile.getUri())
                    .skipMemoryCache(true)//不使用内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存
                    .dontAnimate()
                    .into(imageView);
        } else {
            load(imageView, albumFile.getPath());
        }
    }

    @Override
    public void load(ImageView imageView, String imagePath) {
        LogUtils.i("imagePath=" + imagePath);
        if (URLUtil.isNetworkUrl(imagePath)) {
            Glide.with(imageView.getContext())
                    .load(imagePath)
                    .skipMemoryCache(true)//不使用内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存
                    .dontAnimate()
                    .into(imageView);
        } else {
            Glide.with(imageView.getContext())
                    .load(new File(imagePath))
                    .skipMemoryCache(true)//不使用内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存
                    .dontAnimate()
                    .into(imageView);
        }
    }
//    @Override
//    public void load(ImageView imageView, AlbumFile albumFile) {
//        load(imageView, albumFile.getPath());
//    }
//
//    @Override
//    public void load(ImageView imageView, String url) {
//        if (URLUtil.isNetworkUrl(url)) {
//            Glide.with(imageView.getContext())
//                    .load(url)
//                    .skipMemoryCache(true)//不使用内存缓存
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存
//                    .dontAnimate()
//                    .into(imageView);
//        } else {
//            Glide.with(imageView.getContext())
//                    .load(new File(url))
//                    .skipMemoryCache(true)//不使用内存缓存
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存
//                    .dontAnimate()
//                    .into(imageView);
//        }
//    }
}
