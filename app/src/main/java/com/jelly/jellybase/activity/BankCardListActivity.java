package com.jelly.jellybase.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.bankcard.BankCardInfo;
import com.base.httpmvp.contact.BankCartListContact;
import com.base.httpmvp.presenter.BankListPresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.multiClick.AntiShake;
import com.base.toast.ToastUtils;
import com.base.xrefreshview.XRefreshView;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.view.SimpleItemDecoration;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.BankCardListAdapter;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
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

public class BankCardListActivity extends BaseActivityImpl<BankCartListContact.View,BankCartListContact.Presenter>
        implements BankCartListContact.View {
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.top_right)
    LinearLayout top_right;

    @BindView(R.id.recycler_view_test_rv)
    SwipeRecyclerView recyclerView;
    @BindView(R.id.xrefreshview)
    XRefreshView xRefreshView;
    private LinearLayoutManager layoutManager;
    private BankCardListAdapter adapter;
    private List<BankCardInfo> mList =new ArrayList<>();
    private int startRownumber=0;
    private int pageSize=10;
    private BankCardInfo bankCardInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
        iniXRefreshView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.bankcardlist_activity;
    }
    private void iniView(){
    }
    @Override
    public void onBackPressed() {
        closeProgress();
        super.onBackPressed();
    }
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        startRownumber=0;
        presenter.bankList(true);
    }

    @Override
    public BankCartListContact.Presenter initPresenter() {
        return new BankListPresenter();
    }
    @Override
    public BankCartListContact.View initIBView() {
        return this;
    }

    @Override
    public <T> ObservableTransformer<T, T> bindLifecycle() {
        return lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY);
    }
    private void iniXRefreshView(){
        adapter=new BankCardListAdapter(this,mList);
        //xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);
        xRefreshView.setPullLoadEnable(true);
        //recyclerView = (RecyclerView) findViewById(R.id.recycler_view_test_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLongPressDragEnabled(false); // 长按拖拽，默认关闭。
        recyclerView.setItemViewSwipeEnabled(false); // 滑动删除，默认关闭。
//        recyclerView.useDefaultLoadMore(); // 使用默认的加载更多的View。
//        recyclerView.setLoadMoreListener(mLoadMoreListener); // 加载更多的监听。
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
        recyclerView.setOnItemMenuClickListener(mMenuItemClickListener);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleItemDecoration(22,1, SimpleItemDecoration.NONE));
        recyclerView.setAdapter(adapter);
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
       // adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
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
            presenter.bankList(true);
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            startRownumber++;
            presenter.bankList(false);
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
    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.xswipe_dp_70);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加左侧的，如果不添加，则左侧不会出现菜单。
            {

            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(BankCardListActivity.this)
                        .setBackground(R.drawable.xswipe_selector_red)
                        .setImage(R.drawable.xswipe_action_delete)
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge,int adapterPosition) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                if (adapterPosition<mList.size()){
                    bankCardInfo=mList.get(adapterPosition);
                    if (bankCardInfo!=null){
                      //  presenter.deleteBank(lifecycleProvider.bindUntilEvent(ActivityEvent.STOP));
                    }
                }
            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
            }
        }
    };
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
    public void bankListFailed(boolean isRefresh, String message) {
        if (isRefresh){
            xRefreshView.stopRefresh();
        }else {
            xRefreshView.stopLoadMore();
        }
        ToastUtils.showToast(this,message);
    }
}
