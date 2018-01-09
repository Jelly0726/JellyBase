package com.jelly.jellybase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.applicationUtil.MyApplication;
import com.base.multiClick.AntiShake;
import com.base.view.BaseFragment;
import com.base.xrefreshview.XNestedScrollView;
import com.base.xrefreshview.XRefreshView;
import com.jelly.jellybase.R;
import com.jelly.jellybase.activity.BalanceActivity;
import com.jelly.jellybase.activity.BankCardListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.balance_layout)
    LinearLayout balance_layout;
    @BindView(R.id.balance_tv)
    TextView balance_tv;
    @BindView(R.id.bankcard_layout)
    LinearLayout bankcard_layout;
    @BindView(R.id.banksqy_tv)
    TextView banksqy_tv;
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
    @OnClick({R.id.right_icon,R.id.balance_layout,R.id.bankcard_layout})
    public void onClick(View view){
        if (AntiShake.check(view.getId()))return;
        Intent intent;
        switch (view.getId()){
            case R.id.right_icon:
                break;
            case R.id.balance_layout:
                intent=new Intent(MyApplication.getMyApp(), BalanceActivity.class);
                intent.putExtra("Balance",balance_tv.getText().toString().trim());
                startActivity(intent);
                break;
            case R.id.bankcard_layout:
                intent=new Intent(MyApplication.getMyApp(), BankCardListActivity.class);
                startActivity(intent);
                break;
        }
    }
}
