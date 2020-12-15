package com.base.bgabanner;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.core.view.ViewCompat;

import com.base.applicationUtil.AppUtils;
import com.base.imageView.ImageViewPlus;

import java.util.List;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/5 上午11:34
 * 描述:
 */
public class BGABannerUtil {

    private BGABannerUtil() {
    }

    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    public static <VT extends View> VT getItemImageView(Context context, @DrawableRes int placeholderResId) {
        return getItemImageView(context, placeholderResId, ImageView.ScaleType.CENTER_CROP);
    }

    public static <VT extends View> VT getItemImageView(Context context, @DrawableRes int placeholderResId, ImageView.ScaleType scaleType) {
        ImageViewPlus imageView = new ImageViewPlus(context);
        imageView.setImageResource(placeholderResId);
        imageView.setClickable(true);
        imageView.setScaleType(scaleType);
        imageView.setType(ImageViewPlus.TYPE_ROUNDED);
        imageView.setRectRoundRadius(AppUtils.dipTopx(context,5));
        return (VT) imageView;
    }

    public static void resetPageTransformer(List<? extends View> views) {
        if (views == null) {
            return;
        }

        for (View view : views) {
            view.setVisibility(View.VISIBLE);
            ViewCompat.setAlpha(view, 1);
            ViewCompat.setPivotX(view, view.getMeasuredWidth() * 0.5f);
            ViewCompat.setPivotY(view, view.getMeasuredHeight() * 0.5f);
            ViewCompat.setTranslationX(view, 0);
            ViewCompat.setTranslationY(view, 0);
            ViewCompat.setScaleX(view, 1);
            ViewCompat.setScaleY(view, 1);
            ViewCompat.setRotationX(view, 0);
            ViewCompat.setRotationY(view, 0);
            ViewCompat.setRotation(view, 0);
        }
    }

}