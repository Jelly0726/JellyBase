package com.jelly.jellybase.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.jelly.baselibrary.BaseFragment;
import com.jelly.baselibrary.recyclerViewUtil.ItemDecoration;
import com.jelly.jellybase.adpater.ProductEvaluateAdapter;
import com.jelly.jellybase.databinding.ProductEvaluateFragmentBinding;
import com.jelly.jellybase.datamodel.Product;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/9/21.
 */

public class ProductEvaluateFragment extends BaseFragment<ProductEvaluateFragmentBinding> {
    private LinearLayoutManager layoutManager;
    private ProductEvaluateAdapter adapter;
    private List<Product> mList =new ArrayList<>();
    private int startRownumber=0;
    private int pageSize=10;
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {

    }

    @Override
    public void setData(String json) {

    }
    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //HermesEventBus.getDefault().attach(this);

        iniXRefreshView();
    }
    private void iniXRefreshView(){
        mList.clear();
        for (int i=0;i<9;i++){
            mList.add(new Product());
        }
        adapter=new ProductEvaluateAdapter(this.getActivity(),mList);
        getViewBinding().xrefreshview.setPullLoadEnable(true);
        getViewBinding().recyclerViewTestRv.setHasFixedSize(true);
        // 设置静默加载模式
//		xRefreshView1.setSilenceLoadMore();
        layoutManager = new LinearLayoutManager(this.getActivity());
        getViewBinding().recyclerViewTestRv.setLayoutManager(layoutManager);
        Rect rect=new Rect();
        rect.bottom=0;
        rect.top= 1;
        rect.left=0;
        rect.right=0;
        getViewBinding().recyclerViewTestRv.addItemDecoration(new ItemDecoration(rect,0,ItemDecoration.NONE, ItemDecoration.NONE));
        // 静默加载模式不能设置footerview
        getViewBinding().recyclerViewTestRv.setAdapter(adapter);
//        xRefreshView1.setAutoLoadMore(false);
        getViewBinding().xrefreshview.setPinnedTime(1000);
        getViewBinding().xrefreshview.setMoveForHorizontal(true);

        //当需要使用数据不满一屏时不显示点击加载更多的效果时，解注释下面的三行代码
        //并注释掉第四行代码
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(this.getActivity()));
        //xRefreshView.setCustomFooterView(new XRefreshViewFooter(this.getActivity()));
//        recyclerviewAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
//		xRefreshView1.setPullLoadEnable(false);
        //设置静默加载时提前加载的item个数
//		xRefreshView1.setPreLoadCount(2);

        getViewBinding().xrefreshview.setXRefreshViewListener(simpleXRefreshListener);
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
    @Override
    public void onResume() {
        super.onResume();
        startRownumber=0;

    }
}
