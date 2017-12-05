package com.jelly.jellybase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.base.applicationUtil.ToastUtils;
import com.base.bankcard.BankCardInfo;
import com.base.httpmvp.presenter.BankListPresenter;
import com.base.httpmvp.view.IBankListView;
import com.base.mprogressdialog.MProgressUtil;
import com.base.multiClick.AntiShake;
import com.base.view.MyActivity;
import com.base.xrefreshview.XRefreshView;
import com.base.xrefreshview.XRefreshViewFooter;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.view.SimpleItemDecoration;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.BankCardListAdapter;
import com.maning.mndialoglibrary.MProgressDialog;
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

public class BankCardListActivity extends MyActivity implements IBankListView {
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.top_right)
    LinearLayout top_right;

    @BindView(R.id.recycler_view_test_rv)
    RecyclerView recyclerView;
    @BindView(R.id.xrefreshview)
    XRefreshView xRefreshView;
    private LinearLayoutManager layoutManager;
    private BankCardListAdapter adapter;
    private List<BankCardInfo> mList =new ArrayList<>();
    private int startRownumber=0;
    private int pageSize=10;

    private BankListPresenter bankListPresenter;
    private MProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bankcardlist_activity);
        ButterKnife.bind(this);
        iniView();
        iniXRefreshView();
        iniProgress();
        bankListPresenter=new BankListPresenter(this);
    }
    private void iniView(){
    }
    @Override
    public void onBackPressed() {
        closeProgress();
        super.onBackPressed();
    }
    private void iniProgress(){
        progressDialog= MProgressUtil.getInstance().getMProgressDialog(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog=null;
    }
    @Override
    protected void onResume() {
        super.onResume();
        startRownumber=0;
        bankListPresenter.bankList(true,lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
    }

    private void iniXRefreshView(){
        adapter=new BankCardListAdapter(this,mList);
        //xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);
        xRefreshView.setPullLoadEnable(true);
        //recyclerView = (RecyclerView) findViewById(R.id.recycler_view_test_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleItemDecoration(22,1, SimpleItemDecoration.NONE));
        recyclerView.setAdapter(adapter);
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        adapter.setOnItemClickListener(onItemClickListener);
        xRefreshView.setXRefreshViewListener(simpleXRefreshListener);
    }
    @OnClick({R.id.left_back,R.id.top_right})
    public void onClick(View v) {
        if (AntiShake.check(v.getId())) {    //判断是否多次点击
            return;
        }
        Intent intent;
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.top_right:
                intent=new Intent(BankCardListActivity.this,AddBankCardActivity.class);
                startActivity(intent);
                break;

        }
    }
    /**
     * 滑动刷新
     */
    private XRefreshView.SimpleXRefreshListener simpleXRefreshListener =new XRefreshView.SimpleXRefreshListener() {

        @Override
        public void onRefresh(boolean isPullDown) {
            startRownumber=0;
            bankListPresenter.bankList(true,lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            startRownumber++;
            bankListPresenter.bankList(false,lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
        }
    };
    private OnItemClickListener onItemClickListener=new OnItemClickListener(){

        @Override
        public void onItemClick(View view,int position) {

            if (getIntent().getIntExtra("requestCode",-1)==-1)return;
            BankCardInfo bankCardInfo=mList.get(position);
            Intent intent=new Intent();
            intent.putExtra("data",bankCardInfo);
            setResult(getIntent().getIntExtra("requestCode",-1),intent);
            finish();
//            Intent intent=new Intent(MyApplication.getMyApp(), OrderDetailsActivity.class);
//            startActivity(intent);
        }
    };


    @Override
    public void showProgress() {
        if (progressDialog!=null){
            progressDialog.show();
        }
    }

    @Override
    public void closeProgress() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public Object getBankListParam() {
        Map map=new TreeMap();
        map.put("startRownumber",startRownumber*pageSize+1);
        map.put("pageSize",pageSize);
        return map;
    }

    @Override
    public void bankListSuccess(boolean isRefresh, Object mCallBackVo) {
        List list= (List) mCallBackVo;
        if (isRefresh){
            mList.clear();
            xRefreshView.stopRefresh();
        }else {
            if (list.size()<pageSize){
                xRefreshView.setLoadComplete(true);
            }else
                xRefreshView.stopLoadMore();
        }
        mList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void bankListFailed(boolean isRefresh, String message) {
        if (isRefresh){
            xRefreshView.stopRefresh();
        }else {
            xRefreshView.stopLoadMore();
        }
        ToastUtils.showToast(this,message);
    }
}
