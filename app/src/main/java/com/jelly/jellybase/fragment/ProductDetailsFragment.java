package com.jelly.jellybase.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.base.eventBus.NetEvent;
import com.base.view.BaseFragment;
import com.base.xrefreshview.XRefreshView;
import com.base.xrefreshview.XScrollView;
import com.jelly.jellybase.R;
import com.jelly.jellybase.datamodel.CurrentItem;

import butterknife.BindView;
import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * Created by Administrator on 2017/9/18.
 */

public class ProductDetailsFragment extends BaseFragment {
    @BindView(R.id.product_parameter)
    TextView product_parameter;
    @BindView(R.id.custom_view)
    XRefreshView xRefreshView;
    @BindView(R.id.xscrollview)
    XScrollView scrollView;

    @Override
    protected int getLayoutResource() {
        return R.layout.product_details_fragment;
    }

    @Override
    protected void initView() {
        product_parameter.setOnClickListener(listener);
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

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {

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
