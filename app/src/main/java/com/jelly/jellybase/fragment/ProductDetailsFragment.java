package com.jelly.jellybase.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.base.eventBus.NetEvent;
import com.base.view.BaseFragment;
import com.base.xrefreshview.XRefreshView;
import com.base.xrefreshview.XScrollView;
import com.jelly.jellybase.R;
import com.jelly.jellybase.datamodel.CurrentItem;

import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * Created by Administrator on 2017/9/18.
 */

public class ProductDetailsFragment extends BaseFragment {
    private View mRootView;
    private TextView product_parameter;

    private XRefreshView xRefreshView;
    private XScrollView scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.product_details_fragment, container, false);
        return mRootView;
    }
    @Override
    public void setData(String json) {


    }
    public View getRootView() {
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        product_parameter= (TextView) mRootView.findViewById(R.id.product_parameter);
        product_parameter.setOnClickListener(listener);

        xRefreshView = (XRefreshView) mRootView.findViewById(R.id.custom_view);
        scrollView = (XScrollView) mRootView.findViewById(R.id.xscrollview);
        scrollView.setOnScrollListener(new XScrollView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(ScrollView view, int scrollState, boolean arriveBottom) {
            }

            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
            }
        });
        xRefreshView.setAutoRefresh(false);
        xRefreshView.setPullLoadEnable(false);
        xRefreshView.setPullRefreshEnable(false);
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setAutoLoadMore(false);
//		outView.setSilenceLoadMore();
        xRefreshView.setXRefreshViewListener(simpleXRefreshListener);
        //xRefreshView.setCustomFooterView(new CustomerFooter(this.getActivity()));

    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.product_parameter:
                    NetEvent netEvent0=new NetEvent();
                    netEvent0.setEvent(new CurrentItem(1,"",0));
                    HermesEventBus.getDefault().post(netEvent0);
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
}
