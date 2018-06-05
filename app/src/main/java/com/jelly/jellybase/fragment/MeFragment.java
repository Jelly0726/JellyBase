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

import com.base.applicationUtil.AppPrefs;
import com.base.applicationUtil.MyApplication;
import com.base.config.ConfigKey;
import com.base.httpmvp.databean.MyInfo;
import com.base.multiClick.AntiShake;
import com.base.view.BaseFragment;
import com.base.xrefreshview.XNestedScrollView;
import com.base.xrefreshview.XRefreshView;
import com.jelly.jellybase.R;
import com.jelly.jellybase.activity.BalanceActivity;
import com.jelly.jellybase.activity.BankCardListActivity;
import com.jelly.jellybase.activity.MessageActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/1/6.
 */

public class MeFragment extends BaseFragment {
    private Unbinder mUnbinder;
    @BindView(R.id.xrefreshview)
    XRefreshView xRefreshView;
    @BindView(R.id.nestedScroll_view)
    XNestedScrollView scrollView;
    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.balance_layout)
    LinearLayout balance_layout;
    @BindView(R.id.message_layout)
    LinearLayout message_layout;
    @BindView(R.id.tv_point)
    TextView tv_point;
    @BindView(R.id.balance_tv)
    TextView balance_tv;
    @BindView(R.id.bankcard_layout)
    LinearLayout bankcard_layout;
    @BindView(R.id.banksqy_tv)
    TextView banksqy_tv;
    private MyInfo myInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null)
            rootView = inflater.inflate(R.layout.me_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniXRefreshView();
        if (getActivity()!=null){
            if (MyApplication.getMyApp().isLogin()){
                if (myInfo==null){
                    //presenter.getMyInfo(lifecycleProvider.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
                }
            }else {
                name_tv.setText("未登录");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity()!=null){
            if (MyApplication.getMyApp().isLogin()){
                if (myInfo==null){
                    //presenter.getMyInfo(lifecycleProvider.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
                }
            }else {
                name_tv.setText("未登录");
            }
            if (AppPrefs.getBoolean(MyApplication.getMyApp(), ConfigKey.NEWMESSAGE)){
                tv_point.setVisibility(View.GONE);
            }else {
                tv_point.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
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
    @OnClick({R.id.balance_layout,R.id.bankcard_layout,R.id.message_layout})
    public void onClick(View view){
        if (AntiShake.check(view.getId()))return;
        Intent intent;
        switch (view.getId()){
            case R.id.balance_layout:
                intent=new Intent(MyApplication.getMyApp(), BalanceActivity.class);
                intent.putExtra("Balance",balance_tv.getText().toString().trim());
                startActivity(intent);
                break;
            case R.id.bankcard_layout:
                intent=new Intent(MyApplication.getMyApp(), BankCardListActivity.class);
                startActivity(intent);
                break;
            case R.id.message_layout:
                intent=new Intent(MyApplication.getMyApp(), MessageActivity.class);
                startActivity(intent);
                break;
        }
    }
}
