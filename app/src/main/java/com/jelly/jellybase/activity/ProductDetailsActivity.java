package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.jelly.baselibrary.BackInterface;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.BaseFragment;
import com.jelly.baselibrary.FragmentAdapter;
import com.jelly.baselibrary.Utils.MyDate;
import com.jelly.baselibrary.dialog.AddCartDialog;
import com.jelly.baselibrary.eventBus.NetEvent;
import com.jelly.baselibrary.middleBar.MiddleBarItem;
import com.jelly.baselibrary.middleBar.MiddleBarLayout;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.ProductDetailsActivityBinding;
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

import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * Created by Administrator on 2017/9/21.
 */

public class ProductDetailsActivity extends BaseActivity<ProductDetailsActivityBinding> implements BackInterface, View.OnClickListener {
    private FragmentAdapter myAdapter;

    private List<Fragment> mFragmentList = new ArrayList<>();
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
    private void iniView(){
        getViewBinding().productdetailsBack.setOnClickListener(this);
        getViewBinding().airlinesTv.setOnClickListener(this);
        getViewBinding().addcartTv.setOnClickListener(this);
        getViewBinding().buyImmediately.setOnClickListener(this);
        getViewBinding().banner.setDelegate(new BGABanner.Delegate<ImageView, String>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, String model, int position) {
                Toast.makeText(banner.getContext(), "点击了第" + (position + 1) + "页", Toast.LENGTH_SHORT).show();
            }
        });
        getViewBinding().banner.setAdapter(new BGABanner.Adapter<ImageView, String>() {
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
            getViewBinding().shangouLayout.setVisibility(View.VISIBLE);
            getViewBinding().shangouImg.setVisibility(View.VISIBLE);
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
                                getViewBinding().timeTv.setText("距离结束：00:00:00");
                            }
                        });
                    }
                    time=time-1;
                    //实时更新进度
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getViewBinding().timeTv.setText("距离结束："+ MyDate.secToTimeEN(time));
                        }
                    });
                }
            }, 50, 1000);
        }

    }
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
        getViewBinding().banner.setData(imgs,null);
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
        getViewBinding().vpContent.setAdapter(myAdapter);
        getViewBinding().bbl.setViewPager(getViewBinding().vpContent);
        getViewBinding().bbl.setOnItemSelectedListener(new MiddleBarLayout.OnItemSelectedListener() {
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
        getViewBinding().banner.startAutoPlay();
        // 如果开发者的引导页主题是透明的，需要在界面可见时给背景 Banner 设置一个白色背景，避免滑动过程中两个 Banner 都设置透明度后能看到 Launcher
        getViewBinding().banner.setBackgroundResource(android.R.color.white);
    }
    @Override
    protected void onPause() {
        super.onPause();
        getViewBinding().banner.stopAutoPlay();
    }

    @Override
    protected void onDestroy() {
        getViewBinding().banner.onDestroy();
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
            getViewBinding().bbl.setCurrentItem(currentItem.getItemIndex());
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
