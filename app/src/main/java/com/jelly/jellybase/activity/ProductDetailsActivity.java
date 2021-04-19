package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.jelly.baselibrary.Utils.MyDate;
import com.jelly.baselibrary.dialog.AddCartDialog;
import com.jelly.baselibrary.eventBus.NetEvent;
import com.jelly.baselibrary.middleBar.MiddleBarItem;
import com.jelly.baselibrary.middleBar.MiddleBarLayout;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.base.BackInterface;
import com.base.BaseActivity;
import com.base.BaseFragment;
import com.base.FragmentAdapter;
import com.bumptech.glide.Glide;
import com.jelly.jellybase.R;
import com.jelly.jellybase.datamodel.CurrentItem;
import com.jelly.jellybase.fragment.ProductDetailsFragment;
import com.jelly.jellybase.fragment.ProductEvaluateFragment;
import com.jelly.jellybase.fragment.ProductParameterFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * Created by Administrator on 2017/9/21.
 */

public class ProductDetailsActivity extends BaseActivity implements BackInterface {
    @BindView(R.id.banner)
    BGABanner banner;
    @BindView(R.id.productdetails_back)
    ImageView productdetails_back;
    @BindView(R.id.airlines_tv)
    TextView airlines_tv;
    @BindView(R.id.addcart_tv)
    TextView addcart_tv;
    @BindView(R.id.buy_immediately)
    TextView buy_immediately;
    @BindView(R.id.vp_content)
    ViewPager mVpContent;
    @BindView(R.id.bbl)
    MiddleBarLayout mBottomBarLayout;
    private FragmentAdapter myAdapter;

    private List<Fragment> mFragmentList = new ArrayList<>();
    @BindView(R.id.shangou_layout)
    LinearLayout shangou_layout;
    @BindView(R.id.shangou_img)
    ImageView shangou_img;
    @BindView(R.id.time_tv)
    TextView time_tv;
    private boolean isShanGou=false;//是否闪购
    private Timer timer;
    private static int time=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        isShanGou=getIntent().getBooleanExtra("isShanGou",false);
        iniView();
        processLogic();
        initViewPagerView();
        initViewPagerData();
        initViewPagerListener();
    }
    @Override
    public int getLayoutId(){
        return R.layout.product_details_activity;
    }
    private void iniView(){
        banner.setDelegate(new BGABanner.Delegate<ImageView, String>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, String model, int position) {
                Toast.makeText(banner.getContext(), "点击了第" + (position + 1) + "页", Toast.LENGTH_SHORT).show();
            }
        });
        banner.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
////                SimpleDraweeView simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.sdv_item_fresco_content);
////                simpleDraweeView.setImageURI(Uri.parse(model));
                Glide.with(ProductDetailsActivity.this)
                        .load(model)
                        .placeholder(R.drawable.bga_banner_holder)
                        .error(R.drawable.bga_banner_holder)
                        .fallback(R.drawable.bga_banner_holder)//load为null
                        .dontAnimate()
                        .centerCrop()
                        .into(itemView);
            }
        });
        if (isShanGou){
            shangou_layout.setVisibility(View.VISIBLE);
            shangou_img.setVisibility(View.VISIBLE);
            time=24*3600;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(time<=0){
                        timer.cancel();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                time_tv.setText("距离结束：00:00:00");
                            }
                        });
                    }
                    time=time-1;
                    //实时更新进度
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            time_tv.setText("距离结束："+ MyDate.secToTimeEN(time));
                        }
                    });
                }
            }, 50, 1000);
        }

    }
    @OnClick({R.id.productdetails_back,R.id.airlines_tv,R.id.addcart_tv,R.id.buy_immediately})
    public void onClick(View v) {
        if (AntiShake.check(v.getId()))return;
        switch (v.getId()){
            case R.id.productdetails_back:
                finish();
                break;
            case R.id.airlines_tv:
                break;
            case R.id.addcart_tv:
                AddCartDialog addCartDialog=AddCartDialog.getInstance();
                addCartDialog.show(getSupportFragmentManager(),"addCartDialog");
                break;
            case R.id.buy_immediately:
                AddCartDialog addCartDialog1=AddCartDialog.getInstance();
                addCartDialog1.show(getSupportFragmentManager(),"addCartDialog");
                break;
        }
    }
    private void processLogic() {
        // 设置数据源
//        mBackgroundBanner.setData(R.drawable.uoko_guide_background_1, R.drawable.uoko_guide_background_2, R.drawable.uoko_guide_background_3);
//        mForegroundBanner.setData(R.drawable.uoko_guide_foreground_1, R.drawable.uoko_guide_foreground_2, R.drawable.uoko_guide_foreground_3);
        List<String> imgs=new ArrayList<>();
        imgs.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1505712555066&di=72523b179ae82854526ae5281277a93c&imgtype=jpg&src=http%3A%2F%2Fimg2.niutuku.com%2Fdesk%2F1208%2F2009%2Fntk-2009-18712.jpg");
        imgs.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1505712553329&di=3a87a3a0ce8efc04c874143938cdf8ce&imgtype=0&src=http%3A%2F%2Fimgstore.cdn.sogou.com%2Fapp%2Fa%2F100540002%2F455377.jpg");
        banner.setData(imgs,null);
    }
    private void initViewPagerView() {
    }

    private void initViewPagerData() {
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        mFragmentList.add(productDetailsFragment);

        ProductParameterFragment productParameterFragment = new ProductParameterFragment();
        mFragmentList.add(productParameterFragment);

        ProductEvaluateFragment productEvaluateFragment = new ProductEvaluateFragment();
        mFragmentList.add(productEvaluateFragment);
    }

    private void initViewPagerListener() {
        myAdapter= new FragmentAdapter(getSupportFragmentManager(),mFragmentList);
        mVpContent.setAdapter(myAdapter);
        mBottomBarLayout.setViewPager(mVpContent);
        mBottomBarLayout.setOnItemSelectedListener(new MiddleBarLayout.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final MiddleBarItem bottomBarItem, int position) {
            }
        });

//        mBottomBarLayout.setUnread(0,20);//设置第一个页签的未读数为20
//        mBottomBarLayout.setUnread(1,101);//设置第二个页签的未读书
//        mBottomBarLayout.showNotify(2);//设置第三个页签显示提示的小红点
//        mBottomBarLayout.setMsg(3,"NEW");//设置第四个页签显示NEW提示文字
    }
    @Override
    protected void onResume() {
        super.onResume();
        banner.startAutoPlay();
        // 如果开发者的引导页主题是透明的，需要在界面可见时给背景 Banner 设置一个白色背景，避免滑动过程中两个 Banner 都设置透明度后能看到 Launcher
        banner.setBackgroundResource(android.R.color.white);
    }
    @Override
    protected void onPause() {
        super.onPause();
        banner.stopAutoPlay();
    }

    @Override
    protected void onDestroy() {
        banner.onDestroy();
        if(timer!=null){
            timer.cancel();
        }
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetEvent netEvent){
        if (netEvent.getEventType().equals(CurrentItem.class.getName())){
            CurrentItem currentItem= (CurrentItem) netEvent.getEvent();
            mBottomBarLayout.setCurrentItem(currentItem.getItemIndex());
        }
    }

    private BaseFragment mBaseFragment;
    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        this.mBaseFragment = selectedFragment;
    }

    @Override
    public void onBackPressed() {
        if(mBaseFragment == null || !mBaseFragment.onBackPressed()){
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                super.onBackPressed();
            }else{
                getSupportFragmentManager().popBackStack();
            }
        }
    }
}
