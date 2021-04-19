package com.jelly.baselibrary.redpacket;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.jelly.baselibrary.R;
import com.jelly.baselibrary.R2;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * 自动抢红包使用说明
 */
public class GuideActivity extends AppCompatActivity {

    @BindView(R2.id.left_back)
    LinearLayout left_back;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;

    private String[] imgs = new String[]{"guide_1.jpg", "guide_2.jpg", "guide_3.jpg", "guide_4.jpg", "guide_5.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_packet_guide_activity);
        ButterKnife.bind(this);
        viewPager.setOffscreenPageLimit(imgs.length);
        viewPager.setAdapter(new ImageViewPagerAdapter(this));
    }
    @OnClick({R2.id.left_back})
    public void onClick(View view){
        switch (view.getId()){
            case R2.id.left_back:
                finish();
                break;
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
