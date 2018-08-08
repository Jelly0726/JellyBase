package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.base.toast.ToastUtils;
import com.base.httpmvp.contact.AccountDetailContact;
import com.base.httpmvp.databean.AccountDetail;
import com.base.httpmvp.presenter.AccountDetailPresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.multiClick.AntiShake;
import com.base.xrefreshview.XRefreshView;
import com.base.xrefreshview.XRefreshViewFooter;
import com.base.xrefreshview.view.SimpleItemDecoration;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.AccountDetailsAdapter;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/9/27.
 */

public class AccountDetailsActivity extends BaseActivityImpl<AccountDetailContact.Presenter>
        implements AccountDetailContact.View{
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.recycler_view_test_rv)
    RecyclerView recyclerView;
    @BindView(R.id.xrefreshview)
    XRefreshView xRefreshView;
    private LinearLayoutManager layoutManager;
    private AccountDetailsAdapter adapter;
    private List<AccountDetail> mList =new ArrayList<>();
    private int startRownumber=0;
    private int pageSize=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_details_activity);
        ButterKnife.bind(this);
        iniView();
        iniXRefreshView();
    }
    private void iniView (){
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void iniXRefreshView(){
        adapter=new AccountDetailsAdapter(this,mList);
        xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);
        xRefreshView.setPullLoadEnable(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_test_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleItemDecoration(1,1, SimpleItemDecoration.NONE));
        recyclerView.setAdapter(adapter);
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);

        adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));

        xRefreshView.setXRefreshViewListener(simpleXRefreshListener);
    }
    @OnClick({R.id.left_back})
    public void onClick(View v) {
        if (AntiShake.check(v.getId())) {    //判断是否多次点击
            return;
        }
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.accountDetail(true,lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
    }

    @Override
    public AccountDetailContact.Presenter initPresenter() {
        return new AccountDetailPresenter(this);
    }

    /**
     * 滑动刷新
     */
    private XRefreshView.SimpleXRefreshListener simpleXRefreshListener =new XRefreshView.SimpleXRefreshListener() {

        @Override
        public void onRefresh(boolean isPullDown) {
            startRownumber=0;
            presenter.accountDetail(true,lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            startRownumber++;
            presenter.accountDetail(false,lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
        }
    };
    @Override
    public void onBackPressed() {
        closeProgress();
        super.onBackPressed();
    }
    @Override
    public Object getAccountDetailParam() {
        Map<String,String> map=new TreeMap<>();
        map.put("startRownumber",(startRownumber*pageSize+1)+"");
        map.put("pageSize",pageSize+"");
        return map;
    }

    @Override
    public void accountDetailSuccess(boolean isRefresh, Object mCallBackVo) {
        List list= (List) mCallBackVo;
        if (isRefresh){
            mList.clear();
            if (xRefreshView!=null) {
                xRefreshView.stopRefresh();
                if (list.size() < pageSize) {
                    xRefreshView.setLoadComplete(true);
                } else {
                    xRefreshView.setLoadComplete(false);
                }
            }
        }else {
            if (xRefreshView!=null) {
                if (list.size() == 0) {
                    xRefreshView.setLoadComplete(true);
                } else
                    xRefreshView.stopLoadMore();
            }
        }
        mList.addAll((List)mCallBackVo);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void accountDetailFailed(boolean isRefresh, String message) {
        if (xRefreshView!=null) {
            if (isRefresh) {
                xRefreshView.stopRefresh();
            } else {
                xRefreshView.stopLoadMore();
            }
        }
        ToastUtils.showToast(this,message);
    }
}
