package com.jelly.jellybase.fragment;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.google.gson.Gson;
import com.jelly.baselibrary.BaseFragment;
import com.jelly.baselibrary.model.AccountDetail;
import com.jelly.baselibrary.recyclerViewUtil.SimpleItemDecoration;
import com.jelly.jellybase.adpater.LocaFragmentAdapter;
import com.jelly.jellybase.databinding.LocaFragmentBinding;

import java.util.ArrayList;
import java.util.List;

import systemdb.PositionEntity;


/**
 * Created by Administrator on 2018/1/11.
 */

public class LocaFragment extends BaseFragment<LocaFragmentBinding> {
    private PositionEntity entity;

    private LinearLayoutManager layoutManager;
    private LocaFragmentAdapter adapter;
    private List<AccountDetail> mList =new ArrayList<>();
    private int startRownumber=0;
    private int pageSize=10;
    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {

    }

    @Override
    public void onDestroyView() {
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
        getViewBinding().xrefreshview.setPullLoadEnable(true);
        getViewBinding().recyclerViewTestRv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        getViewBinding().recyclerViewTestRv.setLayoutManager(layoutManager);
        getViewBinding().recyclerViewTestRv.addItemDecoration(new SimpleItemDecoration(1,1, SimpleItemDecoration.NONE));
        getViewBinding().recyclerViewTestRv.setAdapter(adapter);
        getViewBinding().xrefreshview.setPinnedTime(1000);
        getViewBinding().xrefreshview.setMoveForHorizontal(true);

        adapter.setCustomLoadMoreView(new XRefreshViewFooter(getActivity()));

        getViewBinding().xrefreshview.setXRefreshViewListener(simpleXRefreshListener);
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
                    //getViewBinding().xrefreshview.setLoadComplete(true);
                    // 刷新完成必须调用此方法停止加载
                    getViewBinding().xrefreshview.stopLoadMore();
                }
            }, 1000);
        }
    };
}
