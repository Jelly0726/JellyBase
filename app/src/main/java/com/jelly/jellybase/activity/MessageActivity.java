package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.toast.ToastUtils;
import com.base.httpmvp.contact.MessageContact;
import com.base.model.Message;
import com.base.httpmvp.presenter.MessagePresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.xrefreshview.XRefreshView;
import com.base.xrefreshview.XRefreshViewFooter;
import com.base.xrefreshview.view.SimpleItemDecoration;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.MessageAdapter;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 消息通知
 */
public class MessageActivity extends BaseActivityImpl<MessageContact.Presenter>
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
        setContentView(R.layout.message_activity);
        ButterKnife.bind(this);
        iniView ();
        iniXRefreshView();
    }
    @Override
    protected void onResume() {
        super.onResume();
        startRownumber=0;
        xRefreshView.setLoadComplete(false);
        presenter.getMessage(true,lifecycleProvider.bindUntilEvent(ActivityEvent.STOP));
    }
    @Override
    public MessageContact.Presenter initPresenter() {
        return new MessagePresenter(this) ;
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
            presenter.getMessage(true,lifecycleProvider.bindUntilEvent(ActivityEvent.STOP));
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            startRownumber++;
            presenter.getMessage(false,lifecycleProvider.bindUntilEvent(ActivityEvent.STOP));
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
