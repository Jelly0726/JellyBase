package com.jelly.jellybase.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ScrollView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XScrollView;
import com.jelly.baselibrary.BaseFragment;
import com.jelly.baselibrary.eventBus.NetEvent;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.ProductDetailsFragmentBinding;
import com.jelly.jellybase.datamodel.CurrentItem;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Administrator on 2017/9/18.
 */

public class ProductDetailsFragment extends BaseFragment<ProductDetailsFragmentBinding> {
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
        initView();

    }
    private void initView() {
        getBinding().productParameter.setOnClickListener(listener);
        getBinding().xscrollview.setOnScrollListener(new XScrollView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(ScrollView view, int scrollState, boolean arriveBottom) {
            }

            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
            }
        });
        getBinding().customView.setAutoRefresh(false);
        getBinding().customView.setPullLoadEnable(false);
        getBinding().customView.setPullRefreshEnable(false);
        getBinding().customView.setPinnedTime(1000);
        getBinding().customView.setAutoLoadMore(false);
//		outView.setSilenceLoadMore();
        getBinding().customView.setXRefreshViewListener(simpleXRefreshListener);
        //xRefreshView.setCustomFooterView(new CustomerFooter(this.getActivity()));
    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.product_parameter:
                    NetEvent netEvent0=new NetEvent();
                    netEvent0.setEvent(new CurrentItem(1,"",0));
                    EventBus.getDefault().post(netEvent0);
                    break;
            }
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
                    getBinding().customView.stopRefresh();
                }
            }, 2000);
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //xRefreshView.setLoadComplete(true);
                    // 刷新完成必须调用此方法停止加载
                    getBinding().customView.stopLoadMore();
                }
            }, 1000);
        }
    };
}
