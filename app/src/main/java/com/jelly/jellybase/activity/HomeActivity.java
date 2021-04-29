package com.jelly.jellybase.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XScrollView;
import com.base.BaseApplication;
import com.base.MapUtil.DestinationActivity;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.eventBus.NetEvent;
import com.jelly.baselibrary.log.LogUtils;
import com.jelly.baselibrary.model.ScanResult;
import com.jelly.baselibrary.mypopupmenu.BaseItem;
import com.jelly.baselibrary.mypopupmenu.TopMiddlePopup;
import com.jelly.baselibrary.mypopupmenu.Util;
import com.jelly.baselibrary.recyclerViewUtil.SimpleItemDecoration;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.HomeAdapter;
import com.jelly.jellybase.databinding.HomeFragmentActivityBinding;
import com.jelly.jellybase.datamodel.CurrentItem;
import com.jelly.jellybase.datamodel.Product;
import com.jelly.jellybase.seach.SearchActivity;
import com.yanzhenjie.album.impl.OnItemClickListener;
import com.zxingx.library.ScanQRcodeActivity;
import com.zxingx.library.activity.CodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import systemdb.PositionEntity;


/**
 * Created by Administrator on 2017/9/18.
 */

public class HomeActivity extends BaseActivity<HomeFragmentActivityBinding> {
    private final int zxingRequestCode=1;
    private final int addressRequestCode=2;
    private PositionEntity positionEntity;

    private int stickyY=0;
    private boolean isSticky=false;
    private GridLayoutManager layoutManager;
    private HomeAdapter adapter;
    private List<Product> mList =new ArrayList<>();
    private int startRownumber=0;
    private int pageSize=10;

    private TopMiddlePopup topMiddlePopup;

    private BGABanner banner;
    private ScanResult scanResult;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
        iniXRefreshView();
        iniBanner();
        processLogic();
    }
    private void iniView (){
        getViewBinding().saomiaoImg.setOnClickListener(listener);
        getViewBinding().leftAddress.setOnClickListener(listener);
        getViewBinding().homeSearch.setOnClickListener(listener);

        iniSticky(getViewBinding().getRoot());

        getViewBinding().homeGride.unconfirmedLayout.setOnClickListener(listener);
        getViewBinding().homeGride.obligationLayout.setOnClickListener(listener);
        getViewBinding().homeGride.unsendoutLayout.setOnClickListener(listener);

        iniSticky1(getViewBinding().stickyview.getRoot());
    }
    private void iniSticky(View view){
        getViewBinding().homeSticky.priceLayout.setOnClickListener(listener);
        getViewBinding().homeSticky.classifyLayout.setOnClickListener(listener);
        getViewBinding().homeSticky.stateLayout.setOnClickListener(listener);
    }
    private void iniSticky1(View view){
        getViewBinding().stickyview.priceLayout.setOnClickListener(listener);
        getViewBinding().stickyview.classifyLayout.setOnClickListener(listener);
        getViewBinding().stickyview.stateLayout.setOnClickListener(listener);
    }
    private void iniXRefreshView(){
        for (int i=0;i<9;i++){
            mList.add(new Product());
        }
        adapter=new HomeAdapter(this,mList);
        adapter.setOnItemClickListener(onItemClickListener);
        getViewBinding().xscrollview.setOnScrollListener(new XScrollView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(ScrollView view, int scrollState, boolean arriveBottom) {
                int[] location = new int[2];
                getViewBinding().stickyview.getRoot().getLocationOnScreen(location);
                int y = location[1];
                stickyY = y;
            }

            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                if (getViewBinding().homeSticky.getRoot() == null) return;
                int[] location = new int[2];
                getViewBinding().homeSticky.getRoot().getLocationOnScreen(location);
                int y = location[1];
                int getTop = y;
                if (getTop <= stickyY) {
                    getViewBinding().stickyview.getRoot().setVisibility(View.VISIBLE);
                    isSticky=true;
                } else {
                    getViewBinding().stickyview.getRoot().setY(0);
                    getViewBinding().stickyview.getRoot().setVisibility(View.GONE);
                    isSticky=false;
                }
            }
        });
        getViewBinding().customView.setAutoRefresh(false);
        getViewBinding().customView.setPullLoadEnable(true);
        getViewBinding().customView.setPullRefreshEnable(true);
        getViewBinding().customView.setPinnedTime(1000);
        getViewBinding().customView.setMoveForHorizontal(true);
        getViewBinding().customView.setAutoLoadMore(false);
        getViewBinding().customView.setXRefreshViewListener(simpleXRefreshListener);

        getViewBinding().recyclerViewTestRv.setHasFixedSize(true);
        getViewBinding().recyclerViewTestRv.setNestedScrollingEnabled(false);
        layoutManager = new GridLayoutManager(this,2);
        getViewBinding().recyclerViewTestRv.setLayoutManager(layoutManager);
        getViewBinding().recyclerViewTestRv.addItemDecoration(new SimpleItemDecoration(22,2, SimpleItemDecoration.NONE));
        getViewBinding().recyclerViewTestRv.setAdapter(adapter);
    }
    /**
     * 设置弹窗
     *
     * @param type
     */
    private void setPopup(int type,View view) {
        int width=Util.getScreenWidth(BaseApplication.getInstance());
        int height=Util.getScreenHeight(BaseApplication.getInstance());
        LogUtils.i("width="+width+",height="+height);
        ArrayList<BaseItem> items = new ArrayList<BaseItem>();
        items.add(new BaseItem("集团客户",-1));
        items.add(new BaseItem("集团客户",-1));
        items.add(new BaseItem("集团客户",-1));
        topMiddlePopup = new TopMiddlePopup(this,
                width,
                height,
                onPopItem,items, type);
        topMiddlePopup.show(view);
    }
    private boolean oNcount=false;
    private void onChangeFiltrate(int type){
        Drawable img;
        switch (type){
            case 0:
                if(!oNcount){
                    getViewBinding().homeSticky.priceTv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                    getViewBinding().stickyview.priceTv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                    img = getResources().getDrawable(R.mipmap.price_down);
                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                    getViewBinding().homeSticky.priceTv.setCompoundDrawables(null, null, img, null);
                    getViewBinding().stickyview.priceTv.setCompoundDrawables(null, null, img, null);
                    oNcount=true;
                }else {
                    getViewBinding().homeSticky.priceTv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                    getViewBinding().stickyview.priceTv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                    img = getResources().getDrawable(R.mipmap.price_up);
                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                    getViewBinding().homeSticky.priceTv.setCompoundDrawables(null, null, img, null);
                    getViewBinding().stickyview.priceTv.setCompoundDrawables(null, null, img, null);
                    oNcount=false;
                }

                getViewBinding().homeSticky.classifyTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                getViewBinding().stickyview.classifyTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));

                getViewBinding().homeSticky.stateTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                getViewBinding().stickyview.stateTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                break;
            case 1:
                oNcount=false;
                getViewBinding().homeSticky.priceTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                getViewBinding().stickyview.priceTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                img = getResources().getDrawable(R.mipmap.price_down);
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                getViewBinding().homeSticky.priceTv.setCompoundDrawables(null, null, img, null);
                getViewBinding().stickyview.priceTv.setCompoundDrawables(null, null, img, null);

                getViewBinding().homeSticky.classifyTv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                getViewBinding().stickyview.classifyTv.setTextColor(getResources().getColor(R.color.home_filtrate_on));

                getViewBinding().homeSticky.stateTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                getViewBinding().stickyview.stateTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                break;
            case 2:
                oNcount=false;
                getViewBinding().homeSticky.priceTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                getViewBinding().stickyview.priceTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                img = getResources().getDrawable(R.mipmap.price_down);
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                getViewBinding().homeSticky.priceTv.setCompoundDrawables(null, null, img, null);
                getViewBinding().stickyview.priceTv.setCompoundDrawables(null, null, img, null);

                getViewBinding().homeSticky.classifyTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                getViewBinding().stickyview.classifyTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));

                getViewBinding().homeSticky.stateTv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                getViewBinding().stickyview.stateTv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                break;
        }


    }
    /**
     * 弹窗点击事件
     */
    private AdapterView.OnItemClickListener onPopItem = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            topMiddlePopup.dismiss();
        }
    };
    /**
     * 滑动刷新
     */
    private XRefreshView.SimpleXRefreshListener simpleXRefreshListener =new XRefreshView.SimpleXRefreshListener() {

        @Override
        public void onRefresh(boolean isPullDown) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getViewBinding().customView.stopRefresh();
                }
            }, 2000);
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //xRefreshView.setLoadComplete(true);
                    // 刷新完成必须调用此方法停止加载
                    getViewBinding().customView.stopLoadMore();
                }
            }, 1000);
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            return;
        }
        //扫描
        if(requestCode==zxingRequestCode && resultCode== CodeUtils.RESULT_SUCCESS){
            String result=data.getStringExtra(CodeUtils.RESULT_STRING);
            Log.i("ss","result="+result);
            scanResult=new Gson().fromJson(result,ScanResult.class);
            if (!BaseApplication.getInstance().isLogin()){
                ToastUtils.showToast(this,"请先登录!");
                return;
            }
        }
        //地址
        if(requestCode==addressRequestCode && resultCode== addressRequestCode){
            //Log.i("ss","data="+data.getStringExtra("result"));
            positionEntity= (PositionEntity) data.getSerializableExtra("search");
            getViewBinding().addressTv.setText(positionEntity.address);
        }
    }

    private View.OnClickListener listener=new View.OnClickListener() {
        Intent intent;
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.saomiao_img:
                    intent=new Intent(BaseApplication.getInstance(), ScanQRcodeActivity.class);
                    startActivityForResult(intent,zxingRequestCode);
                    break;
                case R.id.left_address:
                    intent=new Intent(BaseApplication.getInstance(), DestinationActivity.class);
                    startActivityForResult(intent,addressRequestCode);
                    break;
                case R.id.home_search:
                    intent=new Intent(BaseApplication.getInstance(), SearchActivity.class);
                    //startActivityForResult(intent,addressRequestCode);
                    startActivity(intent);
                    break;
                case R.id.price_layout:
                    onChangeFiltrate(0);
                    break;
                case R.id.classify_layout:
                    onChangeFiltrate(1);
                    if(isSticky){
                        setPopup(Util.Anim_TopMiddle,getViewBinding().stickyview.classifyLayout);
                    }else {
                        setPopup(Util.Anim_TopMiddle,getViewBinding().homeSticky.classifyLayout);
                    }
                    break;
                case R.id.state_layout:
                    onChangeFiltrate(2);
                    break;
                case R.id.obligation_layout:
                    NetEvent netEvent0=new NetEvent();
                    JSONObject jsonObject0=new JSONObject();
                    try {
                        jsonObject0.put("selectIndex",1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    netEvent0.setEvent(new CurrentItem(2,jsonObject0.toString(),0));
                    EventBus.getDefault().post(netEvent0);
                    break;
                case R.id.unconfirmed_layout:
                    NetEvent netEvent=new NetEvent();
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("selectIndex",2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    netEvent.setEvent(new CurrentItem(2,jsonObject.toString(),0));
                    EventBus.getDefault().post(netEvent);
                    break;
                case R.id.unsendout_layout:
                    NetEvent netEvent1=new NetEvent();
                    JSONObject jsonObject1=new JSONObject();
                    try {
                        jsonObject1.put("selectIndex",3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    netEvent1.setEvent(new CurrentItem(2,jsonObject1.toString(),0));
                    EventBus.getDefault().post(netEvent1);
                    break;
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        startRownumber=0;
        if (banner!=null)
        banner.startAutoPlay();
    }

    private OnItemClickListener onItemClickListener=new OnItemClickListener(){

        @Override
        public void onItemClick(View view,int position) {
        }
    };
    private void iniBanner(){
        banner= (BGABanner) findViewById(R.id.banner);
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
                Glide.with(HomeActivity.this)
                        .load(model)
                        .placeholder(R.drawable.bga_banner_holder)
                        .error(R.drawable.bga_banner_holder)
                        .fallback(R.drawable.bga_banner_holder)//load为null
                        .dontAnimate()
                        .centerCrop()
                        .into(itemView);
            }
        });
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

    @Override
    public void onPause() {
        super.onPause();
        if (banner!=null)
        banner.stopAutoPlay();
    }

}
