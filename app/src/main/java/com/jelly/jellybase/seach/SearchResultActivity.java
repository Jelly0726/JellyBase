package com.jelly.jellybase.seach;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.applicationUtil.MyApplication;
import com.base.view.MyActivity;
import com.base.xrefreshview.XRefreshView;
import com.base.xrefreshview.XRefreshViewFooter;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.view.ItemDecoration;
import com.jelly.jellybase.R;
import com.jelly.jellybase.datamodel.Product;
import com.jelly.jellybase.mypopupmenu.BaseItem;
import com.jelly.jellybase.mypopupmenu.TopMiddlePopup;
import com.jelly.jellybase.mypopupmenu.Util;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/9/28.
 */

public class SearchResultActivity extends MyActivity {
    private LinearLayout left_back;
    private TextView back_search;
    private LinearLayout price_layout;
    private TextView price_tv;
    private LinearLayout classify_layout;
    private TextView classify_tv;
    private LinearLayout state_layout;
    private TextView state_tv;
    private String search;

    private RecyclerView recyclerView;
    private XRefreshView xRefreshView;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchResultAdapter adapter;
    private TextView textView;
    private List<Product> mList =new ArrayList<>();
    private int startRownumber=0;
    private int pageSize=10;

    private TopMiddlePopup topMiddlePopup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_activity);
        search=getIntent().getStringExtra("search");
        iniView();
        iniXRefreshView();
    }
    private void iniView(){
        left_back= (LinearLayout) findViewById(R.id.left_back);
        left_back.setOnClickListener(listener);

        back_search= (TextView) findViewById(R.id.back_search);
        back_search.setText(search);
        back_search.setOnClickListener(listener);


        price_layout= (LinearLayout) findViewById(R.id.price_layout);
        price_tv= (TextView) findViewById(R.id.price_tv);
        price_layout.setOnClickListener(listener);
        classify_layout= (LinearLayout) findViewById(R.id.classify_layout);
        classify_tv= (TextView) findViewById(R.id.classify_tv);
        classify_layout.setOnClickListener(listener);
        state_layout= (LinearLayout) findViewById(R.id.state_layout);
        state_tv= (TextView) findViewById(R.id.state_tv);
        state_layout.setOnClickListener(listener);
    }
    private void iniXRefreshView(){
        for (int i=0;i<9;i++){
            mList.add(new Product());
        }
        adapter=new SearchResultAdapter(this,mList);
        adapter.setOnItemClickListener(onItemClickListener);
        xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);
        xRefreshView.setPullLoadEnable(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_test_rv);
        recyclerView.setHasFixedSize(true);
        // 设置静默加载模式
//		xRefreshView1.setSilenceLoadMore();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Rect rect=new Rect();
        rect.bottom=0;
        rect.top=0;
        rect.left=0;
        rect.right=0;
        recyclerView.addItemDecoration(new ItemDecoration(rect,0, ItemDecoration.NONE));
        // 静默加载模式不能设置footerview
        recyclerView.setAdapter(adapter);
//        xRefreshView1.setAutoLoadMore(false);
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);

        //当需要使用数据不满一屏时不显示点击加载更多的效果时，解注释下面的三行代码
        //并注释掉第四行代码
//        CustomerFooter customerFooter = new CustomerFooter(this);
//        customerFooter.setRecyclerView(recyclerView);
//        recyclerviewAdapter.setCustomLoadMoreView(customerFooter);
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        //xRefreshView.setCustomFooterView(new XRefreshViewFooter(this.getActivity()));
//        recyclerviewAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
//		xRefreshView1.setPullLoadEnable(false);
        //设置静默加载时提前加载的item个数
//		xRefreshView1.setPreLoadCount(2);

        xRefreshView.setXRefreshViewListener(simpleXRefreshListener);
//		// 实现Recyclerview的滚动监听，在这里可以自己处理到达底部加载更多的操作，可以不实现onLoadMore方法，更加自由
//		xRefreshView1.setOnRecyclerViewScrollListener(new OnScrollListener() {
//			@Override
//			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//				super.onScrolled(recyclerView, dx, dy);
//				lastVisibleItem = layoutManager.findLastVisibleItemPosition();
//			}
//
//			public void onScrollStateChanged(RecyclerView recyclerView,
//											 int newState) {
//				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//					isBottom = recyclerviewAdapter.getItemCount() - 1 == lastVisibleItem;
//				}
//			}
//		});
    }
    /**
     * 设置弹窗
     *
     * @param type
     */
    private void setPopup(int type) {
        ArrayList<BaseItem> items = new ArrayList<BaseItem>();
        items.add(new BaseItem("集团客户"));
        items.add(new BaseItem("集团客户"));
        items.add(new BaseItem("集团客户"));
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
                    img = getResources().getDrawable(R.mipmap.price_down);
                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                    price_tv.setCompoundDrawables(null, null, img, null);
                    oNcount=true;
                }else {
                    price_tv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                    img = getResources().getDrawable(R.mipmap.price_up);
                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                    price_tv.setCompoundDrawables(null, null, img, null);
                    oNcount=false;
                }

                classify_tv.setTextColor(getResources().getColor(R.color.home_filtrate_un));

                state_tv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                break;
            case 1:
                oNcount=false;
                price_tv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                img = getResources().getDrawable(R.mipmap.price_down);
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                price_tv.setCompoundDrawables(null, null, img, null);

                classify_tv.setTextColor(getResources().getColor(R.color.home_filtrate_on));

                state_tv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                break;
            case 2:
                oNcount=false;
                price_tv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                img = getResources().getDrawable(R.mipmap.price_down);
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                price_tv.setCompoundDrawables(null, null, img, null);

                classify_tv.setTextColor(getResources().getColor(R.color.home_filtrate_un));

                state_tv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
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
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_back:
                    finish();
                    break;
                case R.id.back_search:
                    Intent intent=new Intent(MyApplication.getMyApp(),SearchActivity.class);
                    intent.putExtra("search",search);
                    //setResult(getIntent().getIntExtra("requestCode",-1),intent);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.price_layout:
                    onChangeFiltrate(0);
                    break;
                case R.id.classify_layout:
                    onChangeFiltrate(1);
                    setPopup(Util.Anim_TopMiddle);
                    topMiddlePopup.show(classify_layout);
                    break;
                case R.id.state_layout:
                    onChangeFiltrate(2);
                    break;
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        startRownumber=0;
    }

    private OnItemClickListener onItemClickListener=new OnItemClickListener(){

        @Override
        public void onItemClick(View view,int position) {
        }
    };
}
