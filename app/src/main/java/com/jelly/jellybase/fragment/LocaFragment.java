package com.jelly.jellybase.fragment;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.base.model.AccountDetail;
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
import systemdb.PositionEntity;

/**
 * Created by Administrator on 2018/1/11.
 */

public class LocaFragment extends BaseFragment {
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
    public int getLayoutId() {
        return R.layout.loca_fragment;
    }
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
