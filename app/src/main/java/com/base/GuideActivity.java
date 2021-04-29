package com.base;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.jellybase.MainActivity;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.BaseActivityGuideBinding;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;


public class GuideActivity extends BaseActivity<BaseActivityGuideBinding> {
    private static final String TAG = GuideActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniBar();
        initView();
        setListener();
        processLogic();
    }
    private void iniBar(){
    }
    private void initView() {
        getViewBinding().bannerGuideForeground.setDelegate(new BGABanner.Delegate<ImageView, String>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, String model, int position) {
                Toast.makeText(banner.getContext(), "点击了第" + (position + 1) + "页", Toast.LENGTH_SHORT).show();
            }
        });
        getViewBinding().bannerGuideForeground.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
////                SimpleDraweeView simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.sdv_item_fresco_content);
////                simpleDraweeView.setImageURI(Uri.parse(model));
                Glide.with(GuideActivity.this)
                        .load(model)
                        .placeholder(R.drawable.bga_banner_holder)//加载前
                        .error(R.drawable.bga_banner_holder)//加载错误
                        .fallback(R.drawable.bga_banner_holder)//load为null
                        .dontAnimate()
                        .centerCrop()
                        .into(itemView);
            }
        });
    }

    private void setListener() {
        /**
         * 设置进入按钮和跳过按钮控件资源 id 及其点击事件
         * 如果进入按钮和跳过按钮有一个不存在的话就传 0
         * 在 BGABanner 里已经帮开发者处理了防止重复点击事件
         * 在 BGABanner 里已经帮开发者处理了「跳过按钮」和「进入按钮」的显示与隐藏
         */
        getViewBinding().bannerGuideForeground.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                finish();

            }
        });
    }

    private void processLogic() {
        // 设置数据源
//        getViewBinding().bannerGuideBackground.setData(R.drawable.uoko_guide_background_1, R.drawable.uoko_guide_background_2, R.drawable.uoko_guide_background_3);
//        getViewBinding().bannerGuideForeground.setData(R.drawable.uoko_guide_foreground_1, R.drawable.uoko_guide_foreground_2, R.drawable.uoko_guide_foreground_3);
        List<String> imgs=new ArrayList<>();
        imgs.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1505712555066&di=72523b179ae82854526ae5281277a93c&imgtype=jpg&src=http%3A%2F%2Fimg2.niutuku.com%2Fdesk%2F1208%2F2009%2Fntk-2009-18712.jpg");
        imgs.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1505712553329&di=3a87a3a0ce8efc04c874143938cdf8ce&imgtype=0&src=http%3A%2F%2Fimgstore.cdn.sogou.com%2Fapp%2Fa%2F100540002%2F455377.jpg");
        getViewBinding().bannerGuideForeground.setAutoPlayAble(imgs.size() > 1);
        getViewBinding().bannerGuideForeground.setData(imgs,null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 如果开发者的引导页主题是透明的，需要在界面可见时给背景 Banner 设置一个白色背景，避免滑动过程中两个 Banner 都设置透明度后能看到 Launcher
        getViewBinding().bannerGuideBackground.setBackgroundResource(android.R.color.white);
    }

    @Override
    protected void onDestroy() {
        getViewBinding().bannerGuideBackground.onDestroy();
        getViewBinding().bannerGuideForeground.onDestroy();
        super.onDestroy();
    }
}