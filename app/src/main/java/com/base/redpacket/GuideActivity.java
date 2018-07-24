package com.base.redpacket;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jelly.jellybase.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * 自动抢红包使用说明
 */
public class GuideActivity extends AppCompatActivity {

    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private String[] imgs = new String[]{"guide_1.png", "guide_2.png", "guide_3.png", "guide_4.png", "guide_5.png"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_packet_guide_activity);
        ButterKnife.bind(this);
        viewPager.setOffscreenPageLimit(imgs.length);
        viewPager.setAdapter(new ImageViewPagerAdapter(this));
    }
    @OnClick({R.id.left_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_back:
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
