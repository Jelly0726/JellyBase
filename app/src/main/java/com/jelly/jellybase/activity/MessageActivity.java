package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        getBinding().xrefreshview.setLoadComplete(false);
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
    public LifecycleOwner bindLifecycle() {
        return this;
    }
    private void iniView (){
        getBinding().leftBack.setOnClickListener(this);
    }
    private void iniXRefreshView(){
        adapter=new MessageAdapter(this,mList);
        getBinding().xrefreshview.setPullLoadEnable(true);
        getBinding().xrefreshview.setPullRefreshEnable(true);
        getBinding().recyclerViewTestRv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        getBinding().recyclerViewTestRv.setLayoutManager(layoutManager);
        getBinding().recyclerViewTestRv.addItemDecoration(new SimpleItemDecoration(1,1, SimpleItemDecoration.NONE));
        getBinding().recyclerViewTestRv.setAdapter(adapter);
        getBinding().xrefreshview.setPinnedTime(1000);
        getBinding().xrefreshview.setMoveForHorizontal(true);
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        getBinding().xrefreshview.setXRefreshViewListener(simpleXRefreshListener);
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
            getBinding().xrefreshview.setLoadComplete(false);
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
            getBinding().xrefreshview.stopRefresh();
            if (list.size()<pageSize){
                getBinding().xrefreshview.setLoadComplete(true);
            }else {
                getBinding().xrefreshview.setLoadComplete(false);
            }
        }else {
            if (list.size()==0){
                getBinding().xrefreshview.setLoadComplete(true);
            }else
                getBinding().xrefreshview.stopLoadMore();
        }
        mList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getMessageFailed(boolean isRefresh, String message) {
        if (isRefresh){
            getBinding().xrefreshview.stopRefresh();
        }else {
            getBinding().xrefreshview.stopLoadMore();
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
