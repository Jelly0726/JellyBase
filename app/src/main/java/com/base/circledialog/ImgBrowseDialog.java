package com.base.circledialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.base.log.DebugLog;
import com.bumptech.glide.Glide;
import com.jelly.jellybase.R;

/**
 * 浏览图片
 */
public class ImgBrowseDialog extends BaseCircleDialog {
    private ImageView imageView;
    private String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1602328710595&di=8bb7732f56834ef513a65c4949ad4557&imgtype=0&src=http%3A%2F%2Fa1.att.hudong.com%2F05%2F00%2F01300000194285122188000535877.jpg";//图片地址

    public static ImgBrowseDialog getInstance() {
        ImgBrowseDialog dialogFragment = new ImgBrowseDialog();
        dialogFragment.setCanceledOnTouchOutside(true);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(1f);
        dialogFragment.setPadding(0, 0, 0, 0);
        return dialogFragment;
    }

    @SuppressLint("ResourceType")
    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        //最外层布局
//        LinearLayout rootLayout = new LinearLayout(context);
//        rootLayout.setOrientation(LinearLayout.VERTICAL);
//        rootLayout.setGravity(Gravity.CENTER);
////        //里层布局
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setPadding(0, 0, 0, 0);
//        linearLayout.setBackground(shapeSolid(context));
        //图片
        imageView = new ImageView(context);
        imageView.setId(0);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setPadding(0, 0, 0, 0);
//        imageView.setBackground(shapeSolid(context));
        //解决Imageview 左右充满 但上下出现间距的问题
        imageView.setAdjustViewBounds(true);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(dp2px(context, 15f)
                , dp2px(context, 0f)
                , dp2px(context, 15f)
                , dp2px(context, 0f));
//        imageView.setLayoutParams(textParams);
        linearLayout.addView(imageView, textParams);
//
//        rootLayout.addView(linearLayout, layoutParams);
        return linearLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniView();
    }

    public void setUrl(String url) {
//        this.url = url;
        DebugLog.i("url="+url);
        if (getActivity() != null) {
            iniView();
        }
    }

    private void iniView() {
        Glide.with(getActivity())
                .load(url)
                .placeholder(R.drawable.ic_placeholder_figure)
                .error(R.drawable.ic_placeholder_figure)
                .fallback(R.drawable.ic_placeholder_figure) //load为null
                .dontAnimate()
                .into(imageView);
    }

    /**
     * 设置圆角的背景
     *
     * @param context 上下文
     */
    private GradientDrawable shapeSolid(Context context) {
        GradientDrawable gd = new GradientDrawable();
        int strokeWidth = 1; // 1dp 边框宽度
        int roundRadius = 5; // 8dp 圆角半径
        int strokeColor = 0xffffffff;//边框颜色
        int fillColor = 0xffffffff;//内部填充颜色
        gd.setColor(fillColor);
        gd.setCornerRadius(dp2px(context, roundRadius));
        gd.setStroke(dp2px(context, strokeWidth), strokeColor);
        return gd;
    }

    /**
     * 根据手机的分辨率dp 转成px(像素)
     */
    private int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
