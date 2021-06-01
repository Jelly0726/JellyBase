package com.jelly.jellybase.imagePreview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jelly.jellybase.R;
import com.previewlibrary.loader.IZoomMediaLoader;
import com.previewlibrary.loader.MySimpleTarget;

import org.jetbrains.annotations.NotNull;

/**
 * Created by yangc on 2017/9/4.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class ImageLoader implements IZoomMediaLoader {


    @Override
    public void displayImage(@NonNull Fragment context, @NonNull String path, final ImageView imageView, @NonNull final MySimpleTarget simpleTarget) {
        RequestListener listener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                simpleTarget.onLoadFailed(null);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                simpleTarget.onResourceReady();
                return false;
            }
        };
        Glide.with(imageView.getContext())
                .asDrawable()
                .load(path)
                .apply(new RequestOptions()
                                .error(R.drawable.ic_placeholder_figure)
                                .fitCenter()
//                        .placeholder(R.drawable.image_placeholder)
                )
                .listener(listener)
                .into(imageView);
    }

    @Override
    public void displayImage(@NonNull @NotNull Fragment context, @NonNull Integer path, ImageView imageView, @NonNull @NotNull MySimpleTarget simpleTarget) {
        RequestListener listener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                simpleTarget.onLoadFailed(null);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                simpleTarget.onResourceReady();
                return false;
            }
        };
        Glide.with(imageView.getContext())
                .asDrawable()
                .load(path)
                .apply(new RequestOptions()
                                .error(R.drawable.ic_placeholder_figure)
                                .fitCenter()
//                        .placeholder(R.drawable.image_placeholder)
                )
                .listener(listener)
                .into(imageView);
    }

    @Override
    public void displayGifImage(@NonNull Fragment context, @NonNull String path, ImageView imageView, @NonNull final MySimpleTarget simpleTarget) {
        RequestListener listener = new RequestListener<String>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<String> target, boolean isFirstResource) {
                simpleTarget.onLoadFailed(null);
                return false;
            }

            @Override
            public boolean onResourceReady(String resource, Object model, Target<String> target, DataSource dataSource, boolean isFirstResource) {
                simpleTarget.onResourceReady();
                return false;
            }
        };
        Glide.with(imageView.getContext())
                .asGif()
                .load(path)
                .apply(new RequestOptions()
                        .dontAnimate() //去掉显示动画
                        //可以解决gif比较几种时 ，加载过慢  //DiskCacheStrategy.NONE
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .error(R.drawable.ic_placeholder_figure)
                        .fitCenter()
                        .placeholder(R.drawable.ic_placeholder_figure)
                )
                .listener(listener)
                .into(imageView);
    }

    @Override
    public void onStop(@NonNull Fragment context) {
        Glide.with(context.getContext()).onStop();

    }

    @Override
    public void clearMemory(@NonNull Context c) {
        Glide.get(c).clearMemory();
    }
}

