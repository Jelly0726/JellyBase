package com.jelly.jellybase.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.httpmvp.databean.AccountDetail;
import com.base.view.BaseFragment;
import com.base.xrefreshview.XRefreshView;
import com.base.xrefreshview.XRefreshViewFooter;
import com.base.xrefreshview.view.SimpleItemDecoration;
import com.google.gson.Gson;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.LocaFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import systemdb.PositionEntity;

/**
 * Created by Administrator on 2018/1/11.
 */

public class LocaFragment extends BaseFragment {
    private View mRootView;
    private Unbinder unbinder;
    private PositionEntity entity;

    @BindView(R.id.recycler_view_test_rv)
    RecyclerView recyclerView;
    @BindView(R.id.xrefreshview)
    XRefreshView xRefreshView;
    private LinearLayoutManager layoutManager;
    private LocaFragmentAdapter adapter;
    private List<AccountDetail> mList =new ArrayList<>();
    private int startRownumber=0;
    private int pageSize=10;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.loca_fragment, container, false);
        unbinder= ButterKnife.bind(this,mRootView);
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
    @Override
    public void setData(String json) {
        entity=new Gson().fromJson(json, PositionEntity.class);
        if (getActivity()!=null)
            iniData();
    }
    private void iniData(){

    }
    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniXRefreshView();
    }

    private void iniXRefreshView(){
        adapter=new LocaFragmentAdapter(getActivity(),mList);
        xRefreshView.setPullLoadEnable(true);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleItemDecoration(1,1, SimpleItemDecoration.NONE));
        recyclerView.setAdapter(adapter);
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);

        adapter.setCustomLoadMoreView(new XRefreshViewFooter(getActivity()));

        xRefreshView.setXRefreshViewListener(simpleXRefreshListener);
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
