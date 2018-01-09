package com.jelly.jellybase.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.view.BaseFragment;
import com.base.xrefreshview.XNestedScrollView;
import com.base.xrefreshview.XRefreshView;
import com.jelly.jellybase.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/1/6.
 */

public class MeFragment extends BaseFragment {
    private View mRootView;
    private Unbinder unbinder;

    @BindView(R.id.xrefreshview)
    XRefreshView xRefreshView;
    @BindView(R.id.nestedScroll_view)
    XNestedScrollView scrollView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.me_fragment, container, false);
        unbinder= ButterKnife.bind(this,mRootView);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniXRefreshView();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
    @Override
    public void setData(String json) {

    }
    @Override
    public boolean onBackPressed() {
        return false;
    }
    private void iniXRefreshView(){
        xRefreshView.setAutoRefresh(false);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setPullRefreshEnable(true);
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setAutoLoadMore(false);
        xRefreshView.setXRefreshViewListener(simpleXRefreshListener);

        scrollView.setOnScrollListener(new XNestedScrollView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(NestedScrollView view, int scrollState, boolean arriveBottom) {

            }

            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {

            }
        });

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
