package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.jelly.baselibrary.model.Message;
import com.jelly.baselibrary.recyclerViewUtil.SimpleItemDecoration;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.MessageAdapter;
import com.jelly.jellybase.databinding.MessageActivityBinding;
import com.jelly.mvp.contact.MessageContact;
import com.jelly.mvp.presenter.MessagePresenter;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.ObservableTransformer;

/**
 * 消息通知
 */
public class MessageActivity extends BaseActivityImpl<MessageContact.View
        ,MessageContact.Presenter, MessageActivityBinding>
        implements MessageContact.View , View.OnClickListener {
    private LinearLayoutManager layoutManager;
    private MessageAdapter adapter;
    private List<Message> mList =new ArrayList<>();
    private int startRownumber=0;
    private int pageSize=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView ();
        iniXRefreshView();
    }
    @Override
    protected void onResume() {
        super.onResume();
        startRownumber=0;
        getViewBinding().xrefreshview.setLoadComplete(false);
        presenter.getMessage(true);
    }
    @Override
    public MessageContact.Presenter initPresenter() {
        return new MessagePresenter() ;
    }
    @Override
    public MessageContact.View initIBView() {
        return this;
    }

    @Override
    public <T> ObservableTransformer<T, T> bindLifecycle() {
        return lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY);
    }
    private void iniView (){
        getViewBinding().leftBack.setOnClickListener(this);
    }
    private void iniXRefreshView(){
        adapter=new MessageAdapter(this,mList);
        getViewBinding().xrefreshview.setPullLoadEnable(true);
        getViewBinding().xrefreshview.setPullRefreshEnable(true);
        getViewBinding().recyclerViewTestRv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        getViewBinding().recyclerViewTestRv.setLayoutManager(layoutManager);
        getViewBinding().recyclerViewTestRv.addItemDecoration(new SimpleItemDecoration(1,1, SimpleItemDecoration.NONE));
        getViewBinding().recyclerViewTestRv.setAdapter(adapter);
        getViewBinding().xrefreshview.setPinnedTime(1000);
        getViewBinding().xrefreshview.setMoveForHorizontal(true);
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        getViewBinding().xrefreshview.setXRefreshViewListener(simpleXRefreshListener);
    }
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_back:
                finish();
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
            getViewBinding().xrefreshview.setLoadComplete(false);
            presenter.getMessage(true);
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            startRownumber++;
            presenter.getMessage(false);
        }
    };
    @Override
    public Object getMessageParam() {
        Map map=new TreeMap();
        map.put("startrow",startRownumber*pageSize+1);
        map.put("pagesize",pageSize);
        return map;
    }

    @Override
    public void getMessageSuccess(boolean isRefresh, Object mCallBackVo) {
        List<Message> list= (List<Message>) mCallBackVo;
        if (isRefresh){
            mList.clear();
            getViewBinding().xrefreshview.stopRefresh();
            if (list.size()<pageSize){
                getViewBinding().xrefreshview.setLoadComplete(true);
            }else {
                getViewBinding().xrefreshview.setLoadComplete(false);
            }
        }else {
            if (list.size()==0){
                getViewBinding().xrefreshview.setLoadComplete(true);
            }else
                getViewBinding().xrefreshview.stopLoadMore();
        }
        mList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getMessageFailed(boolean isRefresh, String message) {
        if (isRefresh){
            getViewBinding().xrefreshview.stopRefresh();
        }else {
            getViewBinding().xrefreshview.stopLoadMore();
        }
        ToastUtils.showToast(this,message);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void closeProgress() {

    }
}
