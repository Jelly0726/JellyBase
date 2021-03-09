package com.jelly.jellybase.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jelly.mvp.contact.AccountDetailContact;
import com.jelly.mvp.presenter.AccountDetailPresenter;
import com.base.httpmvp.retrofitapi.methods.ResultData;
import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.base.model.AccountDetail;
import com.base.multiClick.AntiShake;
import com.base.recyclerViewUtil.ItemDecoration;
import com.base.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.AccountDetailsAdapter;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableTransformer;


/**
 * Created by Administrator on 2017/9/27.
 */

public class AccountDetailsActivity extends BaseActivityImpl<AccountDetailContact.View,AccountDetailContact.Presenter>
        implements AccountDetailContact.View{
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.mRecyclerView)
    SwipeRecyclerView mRecyclerView;
    @BindView(R.id.mRefreshLayout)
    SwipeRefreshLayout mRefreshLayout;
    private LinearLayoutManager layoutManager;
    private AccountDetailsAdapter adapter;
    private List<AccountDetail> mList =new ArrayList<>();
    private long mMaxToal=10;//
    private int page=1;
    private int size=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
        iniXRefreshView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.account_details_activity;
    }
    private void iniView (){
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void iniXRefreshView(){
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        adapter=new AccountDetailsAdapter(this);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(createItemDecoration());
        mRecyclerView.useDefaultLoadMore(); // 使用默认的加载更多的View。
        mRecyclerView.setLoadMoreListener(mLoadMoreListener); // 加载更多的监听。
        mRecyclerView.setLongPressDragEnabled(false); // 长按拖拽，默认关闭。
        mRecyclerView.setItemViewSwipeEnabled(false); // 滑动删除，默认关闭。
        mRecyclerView.setAdapter(adapter);
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
        presenter.accountDetail(true);
    }
    protected RecyclerView.ItemDecoration createItemDecoration() {
        Rect rect=new Rect();
        rect.top=1;
        rect.bottom=1;
        rect.left=1;
        rect.right=1;
        return new ItemDecoration(rect,1,ItemDecoration.NONE, ContextCompat.getColor(this, R.color.main_bg));
    }
    @Override
    public AccountDetailContact.Presenter initPresenter() {
        return new AccountDetailPresenter();
    }

    @Override
    public AccountDetailContact.View initIBView() {
        return this;
    }

    @Override
    public <T> ObservableTransformer<T, T> bindLifecycle() {
        return lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY);
    }
    /**
     * 刷新。
     */
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            page=1;
            presenter.accountDetail(true);

        }
    };
    /**
     * 加载更多。
     */
    private SwipeRecyclerView.LoadMoreListener mLoadMoreListener = new SwipeRecyclerView.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (mMaxToal>(page*size)){
                page++;
                presenter.accountDetail(false);
            }
        }
    };
    @Override
    public void onBackPressed() {
        closeProgress();
        super.onBackPressed();
    }
    @Override
    public Object getAccountDetailParam() {
        Map map=new TreeMap<>();
        map.put("page",page);
        map.put("size",size);
        map.put("needPage", true);
        return map;
    }

    @Override
    public void accountDetailSuccess(boolean isRefresh, Object mCallBackVo) {
        ResultData<AccountDetail> resultData= (ResultData<AccountDetail>) mCallBackVo;
        if (isRefresh){
            mList.clear();
            mRefreshLayout.setRefreshing(false);
        }
        mMaxToal=resultData.getTotal();
        mList.addAll(resultData.getRows());
        adapter.notifyDataSetChanged(mList);
        // 数据完更多数据，一定要掉用这个方法。
        // 第一个参数：表示此次数据是否为空。
        // 第二个参数：表示是否还有更多数据。
        mRecyclerView.loadMoreFinish(resultData.getRows().size()==0, mMaxToal>(page*size));
    }

    @Override
    public void accountDetailFailed(boolean isRefresh, String message) {
        ToastUtils.showToast(this,message);
        if (isRefresh) {
            mList.clear();
            mRefreshLayout.setRefreshing(false);
        }else
            page--;
        adapter.notifyDataSetChanged(mList);
        // 数据完更多数据，一定要掉用这个方法。
        // 第一个参数：表示此次数据是否为空。
        // 第二个参数：表示是否还有更多数据。
        mRecyclerView.loadMoreFinish(true, mMaxToal>(page*size));
    }
}
