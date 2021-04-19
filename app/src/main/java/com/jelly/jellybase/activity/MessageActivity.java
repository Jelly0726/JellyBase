package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jelly.mvp.contact.MessageContact;
import com.jelly.mvp.presenter.MessagePresenter;
import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.jelly.baselibrary.model.Message;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.baselibrary.xrefreshview.XRefreshView;
import com.jelly.baselibrary.xrefreshview.XRefreshViewFooter;
import com.jelly.baselibrary.xrefreshview.view.SimpleItemDecoration;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.MessageAdapter;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableTransformer;

/**
 * 消息通知
 */
public class MessageActivity extends BaseActivityImpl<MessageContact.View,MessageContact.Presenter>
        implements MessageContact.View {
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.recycler_view_test_rv)
    RecyclerView recyclerView;
    @BindView(R.id.xrefreshview)
    XRefreshView xRefreshView;
    @BindView(R.id.title_tv)
    TextView title_tv;
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
    public int getLayoutId(){
        return R.layout.message_activity;
    }
    @Override
    protected void onResume() {
        super.onResume();
        startRownumber=0;
        xRefreshView.setLoadComplete(false);
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
    }
    private void iniXRefreshView(){
        adapter=new MessageAdapter(this,mList);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setPullRefreshEnable(true);
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
            xRefreshView.setLoadComplete(false);
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
            xRefreshView.stopRefresh();
            if (list.size()<pageSize){
                xRefreshView.setLoadComplete(true);
            }else {
                xRefreshView.setLoadComplete(false);
            }
        }else {
            if (list.size()==0){
                xRefreshView.setLoadComplete(true);
            }else
                xRefreshView.stopLoadMore();
        }
        mList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getMessageFailed(boolean isRefresh, String message) {
        if (isRefresh){
            xRefreshView.stopRefresh();
        }else {
            xRefreshView.stopLoadMore();
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
