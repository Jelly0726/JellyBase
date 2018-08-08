package com.jelly.jellybase.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.base.MapUtil.DestinationActivity;
import com.base.appManager.MyApplication;
import com.base.toast.ToastUtils;
import com.base.bgabanner.BGABanner;
import com.base.eventBus.NetEvent;
import com.base.httpmvp.databean.ScanResult;
import com.base.view.BaseActivity;
import com.base.xrefreshview.XRefreshView;
import com.base.xrefreshview.XScrollView;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.view.SimpleItemDecoration;
import com.base.zxing.ScanerCodeActivity;
import com.base.zxing.decoding.ZXingUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.HomeAdapter;
import com.jelly.jellybase.datamodel.CurrentItem;
import com.jelly.jellybase.datamodel.Product;
import com.jelly.jellybase.mypopupmenu.BaseItem;
import com.jelly.jellybase.mypopupmenu.TopMiddlePopup;
import com.jelly.jellybase.mypopupmenu.Util;
import com.jelly.jellybase.seach.SearchActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import systemdb.PositionEntity;
import xiaofei.library.hermeseventbus.HermesEventBus;


/**
 * Created by Administrator on 2017/9/18.
 */

public class HomeActivity extends BaseActivity {
    private View mRootView;
    private final int zxingRequestCode=1;
    private final int addressRequestCode=2;
    private ImageView saomiao_img;
    private LinearLayout left_address;
    private TextView address_tv;
    private PositionEntity positionEntity;
    private TextView home_search;

    private LinearLayout price_layout;
    private TextView price_tv;
    private LinearLayout classify_layout;
    private TextView classify_tv;
    private LinearLayout state_layout;
    private TextView state_tv;
    private LinearLayout price_layout1;
    private TextView price_tv1;
    private LinearLayout classify_layout1;
    private TextView classify_tv1;
    private LinearLayout state_layout1;
    private TextView state_tv1;

    private LinearLayout unconfirmed_layout;
    private LinearLayout obligation_layout;
    private LinearLayout unsendout_layout;

    private View stickyview;
    private View stickyview_layout;
    private int stickyY=0;
    private boolean isSticky=false;
    private RecyclerView recyclerView;
    private XRefreshView xRefreshView;
    private XScrollView scrollView;
    private GridLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HomeAdapter adapter;
    private TextView textView;
    private List<Product> mList =new ArrayList<>();
    private int startRownumber=0;
    private int pageSize=10;

    private TopMiddlePopup topMiddlePopup;

    private BGABanner banner;
    private ScanResult scanResult;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView=getLayoutInflater().inflate(R.layout.home_fragment_activity,null);
        setContentView(mRootView);
        iniView();
        iniXRefreshView();
        iniBanner();
        processLogic();
    }
    private void iniView (){
        saomiao_img= (ImageView) findViewById(R.id.saomiao_img);
        saomiao_img.setOnClickListener(listener);
        left_address= (LinearLayout) findViewById(R.id.left_address);
        left_address.setOnClickListener(listener);
        address_tv= (TextView) findViewById(R.id.address_tv);
        home_search= (TextView) findViewById(R.id.home_search);
        home_search.setOnClickListener(listener);

        stickyview_layout=findViewById(R.id.home_sticky_layout);
        iniSticky(mRootView);

        unconfirmed_layout= (LinearLayout) findViewById(R.id.unconfirmed_layout);
        unconfirmed_layout.setOnClickListener(listener);
        obligation_layout= (LinearLayout) findViewById(R.id.obligation_layout);
        obligation_layout.setOnClickListener(listener);
        unsendout_layout= (LinearLayout) findViewById(R.id.unsendout_layout);
        unsendout_layout.setOnClickListener(listener);

        stickyview=findViewById(R.id.stickyview);
        iniSticky1(stickyview);
    }
    private void iniSticky(View view){
        price_layout= (LinearLayout) view.findViewById(R.id.price_layout);
        price_tv= (TextView) view.findViewById(R.id.price_tv);
        price_layout.setOnClickListener(listener);
        classify_layout= (LinearLayout) view.findViewById(R.id.classify_layout);
        classify_tv= (TextView) view.findViewById(R.id.classify_tv);
        classify_layout.setOnClickListener(listener);
        state_layout= (LinearLayout) view.findViewById(R.id.state_layout);
        state_tv= (TextView) view.findViewById(R.id.state_tv);
        state_layout.setOnClickListener(listener);
    }
    private void iniSticky1(View view){
        price_layout1= (LinearLayout) view.findViewById(R.id.price_layout);
        price_tv1= (TextView) view.findViewById(R.id.price_tv);
        price_layout1.setOnClickListener(listener);
        classify_layout1= (LinearLayout) view.findViewById(R.id.classify_layout);
        classify_tv1= (TextView) view.findViewById(R.id.classify_tv);
        classify_layout1.setOnClickListener(listener);
        state_layout1= (LinearLayout) view.findViewById(R.id.state_layout);
        state_tv1= (TextView) view.findViewById(R.id.state_tv);
        state_layout1.setOnClickListener(listener);
    }
    private void iniXRefreshView(){
        for (int i=0;i<9;i++){
            mList.add(new Product());
        }
        adapter=new HomeAdapter(this,mList);
        adapter.setOnItemClickListener(onItemClickListener);
        xRefreshView = (XRefreshView) findViewById(R.id.custom_view);
        scrollView = (XScrollView) findViewById(R.id.xscrollview);
        scrollView.setOnScrollListener(new XScrollView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(ScrollView view, int scrollState, boolean arriveBottom) {
                int[] location = new int[2];
                stickyview.getLocationOnScreen(location);
                int y = location[1];
                stickyY = y;
            }

            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                if (stickyview_layout == null) return;
                int[] location = new int[2];
                stickyview_layout.getLocationOnScreen(location);
                int y = location[1];
                int getTop = y;
                if (getTop <= stickyY) {
                    stickyview.setVisibility(View.VISIBLE);
                    isSticky=true;
                } else {
                    stickyview.setY(0);
                    stickyview.setVisibility(View.GONE);
                    isSticky=false;
                }
            }
        });
        xRefreshView.setAutoRefresh(false);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setPullRefreshEnable(true);
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setAutoLoadMore(false);
        xRefreshView.setXRefreshViewListener(simpleXRefreshListener);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_test_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleItemDecoration(22,2, SimpleItemDecoration.NONE));
        recyclerView.setAdapter(adapter);
    }
    /**
     * 设置弹窗
     *
     * @param type
     */
    private void setPopup(int type) {
        ArrayList<BaseItem> items = new ArrayList<BaseItem>();
        items.add(new BaseItem("集团客户",-1));
        items.add(new BaseItem("集团客户",-1));
        items.add(new BaseItem("集团客户",-1));
        topMiddlePopup = new TopMiddlePopup(this,
                Util.getScreenWidth(MyApplication.getMyApp()), Util.getScreenHeight(MyApplication.getMyApp()),
                onPopItem,items, type);
    }
    private boolean oNcount=false;
    private void onChangeFiltrate(int type){
        Drawable img;
        switch (type){
            case 0:
                if(!oNcount){
                    price_tv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                    price_tv1.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                    img = getResources().getDrawable(R.mipmap.price_down);
                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                    price_tv.setCompoundDrawables(null, null, img, null);
                    price_tv1.setCompoundDrawables(null, null, img, null);
                    oNcount=true;
                }else {
                    price_tv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                    price_tv1.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                    img = getResources().getDrawable(R.mipmap.price_up);
                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                    price_tv.setCompoundDrawables(null, null, img, null);
                    price_tv1.setCompoundDrawables(null, null, img, null);
                    oNcount=false;
                }

                classify_tv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                classify_tv1.setTextColor(getResources().getColor(R.color.home_filtrate_un));

                state_tv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                state_tv1.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                break;
            case 1:
                oNcount=false;
                price_tv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                price_tv1.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                img = getResources().getDrawable(R.mipmap.price_down);
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                price_tv.setCompoundDrawables(null, null, img, null);
                price_tv1.setCompoundDrawables(null, null, img, null);

                classify_tv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                classify_tv1.setTextColor(getResources().getColor(R.color.home_filtrate_on));

                state_tv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                state_tv1.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                break;
            case 2:
                oNcount=false;
                price_tv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                price_tv1.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                img = getResources().getDrawable(R.mipmap.price_down);
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                price_tv.setCompoundDrawables(null, null, img, null);
                price_tv1.setCompoundDrawables(null, null, img, null);

                classify_tv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                classify_tv1.setTextColor(getResources().getColor(R.color.home_filtrate_un));

                state_tv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                state_tv1.setTextColor(getResources().getColor(R.color.home_filtrate_on));
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
                    xRefreshView.stopRefresh();
                }
            }, 2000);
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //xRefreshView.setLoadComplete(true);
                    // 刷新完成必须调用此方法停止加载
                    xRefreshView.stopLoadMore();
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
        if(requestCode==zxingRequestCode && resultCode== ZXingUtils.resultCode){
            String result=data.getStringExtra(ZXingUtils.ScanResult);
            Log.i("ss","result="+result);
            scanResult=new Gson().fromJson(result,ScanResult.class);
            if (!MyApplication.getMyApp().isLogin()){
                ToastUtils.showToast(this,"请先登录!");
                return;
            }
        }
        //地址
        if(requestCode==addressRequestCode && resultCode== addressRequestCode){
            //Log.i("ss","data="+data.getStringExtra("result"));
            positionEntity= (PositionEntity) data.getSerializableExtra("search");
            address_tv.setText(positionEntity.address);
        }
    }

    private View.OnClickListener listener=new View.OnClickListener() {
        Intent intent;
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.saomiao_img:
                    intent=new Intent(MyApplication.getMyApp(), ScanerCodeActivity.class);
                    startActivityForResult(intent,zxingRequestCode);
                    break;
                case R.id.left_address:
                    intent=new Intent(MyApplication.getMyApp(), DestinationActivity.class);
                    startActivityForResult(intent,addressRequestCode);
                    break;
                case R.id.home_search:
                    intent=new Intent(MyApplication.getMyApp(), SearchActivity.class);
                    //startActivityForResult(intent,addressRequestCode);
                    startActivity(intent);
                    break;
                case R.id.price_layout:
                    onChangeFiltrate(0);
                    break;
                case R.id.classify_layout:
                    onChangeFiltrate(1);
                    setPopup(Util.Anim_TopMiddle);
                    if(isSticky){
                        topMiddlePopup.show(classify_layout1);
                    }else {
                        topMiddlePopup.show(classify_layout);
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
                    HermesEventBus.getDefault().post(netEvent0);
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
                    HermesEventBus.getDefault().post(netEvent);
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
                    HermesEventBus.getDefault().post(netEvent1);
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
