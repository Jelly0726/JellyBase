package com.jelly.baselibrary.redpacket;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.R;
import com.jelly.baselibrary.databinding.RedPacketGuideActivityBinding;

import java.io.IOException;
/**
 * 自动抢红包使用说明
 */
public class GuideActivity extends BaseActivity<RedPacketGuideActivityBinding> implements View.OnClickListener {
    private String[] imgs = new String[]{"guide_1.jpg", "guide_2.jpg", "guide_3.jpg", "guide_4.jpg", "guide_5.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_packet_guide_activity);
        getBinding().viewPager.setOffscreenPageLimit(imgs.length);
        getBinding().viewPager.setAdapter(new ImageViewPagerAdapter(this));
        getBinding().leftBack.setOnClickListener(this);
    }
    public void onClick(View view){
        if (view.getId() == R.id.left_back) {
            finish();
        }
    }
    public class ImageViewPagerAdapter extends PagerAdapter {

        private Context context;

        public ImageViewPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return imgs.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
        @Override
        public Object instantiateItem(final ViewGroup container, int position) { // 这个方法用来实例化页卡
            final ImageView imageView;
            if (container.getChildAt(position) == null) {
                imageView = new ImageView(this.context);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(params);
                try {
                    BitmapDrawable d = new BitmapDrawable(this.context.getAssets()
                            .open(imgs[position]));
                    imageView.setImageDrawable(d);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                container.addView(imageView);
            } else
                imageView = (ImageView) container.getChildAt(position);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
