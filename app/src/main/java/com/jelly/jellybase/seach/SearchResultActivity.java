package com.jelly.jellybase.seach;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.base.BaseApplication;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.mypopupmenu.BaseItem;
import com.jelly.baselibrary.mypopupmenu.TopMiddlePopup;
import com.jelly.baselibrary.mypopupmenu.Util;
import com.jelly.baselibrary.recyclerViewUtil.ItemDecoration;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.SearchResultActivityBinding;
import com.jelly.jellybase.datamodel.Product;
import com.yanzhenjie.album.impl.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/9/28.
 */

public class SearchResultActivity extends BaseActivity<SearchResultActivityBinding> {
    private String search;

    private LinearLayoutManager layoutManager;
    private SearchResultAdapter adapter;
    private List<Product> mList =new ArrayList<>();
    private int startRownumber=0;
    private int pageSize=10;

    private TopMiddlePopup topMiddlePopup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        search=getIntent().getStringExtra("search");
        iniView();
        iniXRefreshView();
    }
    private void iniView(){
        getViewBinding().leftBack.setOnClickListener(listener);

        getViewBinding().backSearch.setText(search);
        getViewBinding().backSearch.setOnClickListener(listener);


        getViewBinding().priceLayout.setOnClickListener(listener);
        getViewBinding().classifyLayout.setOnClickListener(listener);
        getViewBinding().stateLayout.setOnClickListener(listener);
    }
    private void iniXRefreshView(){
        for (int i=0;i<9;i++){
            mList.add(new Product());
        }
        adapter=new SearchResultAdapter(this,mList);
        adapter.setOnItemClickListener(onItemClickListener);
        getViewBinding().xrefreshview.setPullLoadEnable(true);
        getViewBinding().recyclerViewTestRv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        getViewBinding().recyclerViewTestRv.setLayoutManager(layoutManager);
        Rect rect=new Rect();
        rect.bottom=0;
        rect.top=0;
        rect.left=0;
        rect.right=0;
        getViewBinding().recyclerViewTestRv.addItemDecoration(new ItemDecoration(rect,0,-1, ItemDecoration.NONE));
        getViewBinding().recyclerViewTestRv.setAdapter(adapter);
        getViewBinding().xrefreshview.setPinnedTime(1000);
        getViewBinding().xrefreshview.setMoveForHorizontal(true);
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        getViewBinding().xrefreshview.setXRefreshViewListener(simpleXRefreshListener);
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
                Util.getScreenWidth(BaseApplication.getInstance()), Util.getScreenHeight(BaseApplication.getInstance()),
                onPopItem,items, type);
    }
    private boolean oNcount=false;
    private void onChangeFiltrate(int type){
        Drawable img;
        switch (type){
            case 0:
                if(!oNcount){
                    getViewBinding().priceTv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                    img = getResources().getDrawable(R.mipmap.price_down);
                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                    getViewBinding().priceTv.setCompoundDrawables(null, null, img, null);
                    oNcount=true;
                }else {
                    getViewBinding().priceTv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
                    img = getResources().getDrawable(R.mipmap.price_up);
                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                    getViewBinding().priceTv.setCompoundDrawables(null, null, img, null);
                    oNcount=false;
                }

                getViewBinding().classifyTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));

                getViewBinding().stateTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                break;
            case 1:
                oNcount=false;
                getViewBinding().priceTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                img = getResources().getDrawable(R.mipmap.price_down);
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                getViewBinding().priceTv.setCompoundDrawables(null, null, img, null);

                getViewBinding().classifyTv.setTextColor(getResources().getColor(R.color.home_filtrate_on));

                getViewBinding().stateTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                break;
            case 2:
                oNcount=false;
                getViewBinding().priceTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));
                img = getResources().getDrawable(R.mipmap.price_down);
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                getViewBinding().priceTv.setCompoundDrawables(null, null, img, null);

                getViewBinding().classifyTv.setTextColor(getResources().getColor(R.color.home_filtrate_un));

                getViewBinding().stateTv.setTextColor(getResources().getColor(R.color.home_filtrate_on));
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
                    getViewBinding().xrefreshview.stopRefresh();
                }
            }, 2000);
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //xRefreshView.setLoadComplete(true);
                    // 刷新完成必须调用此方法停止加载
                    getViewBinding().xrefreshview.stopLoadMore();
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
                    Intent intent=new Intent(BaseApplication.getInstance(),SearchActivity.class);
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
                    topMiddlePopup.show(getViewBinding().classifyLayout);
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
