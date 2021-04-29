package com.jelly.jellybase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;

import com.andview.refreshview.XRefreshView;
import com.base.BaseApplication;
import com.jelly.baselibrary.BaseFragment;
import com.jelly.baselibrary.applicationUtil.AppPrefs;
import com.jelly.baselibrary.config.ConfigKey;
import com.jelly.baselibrary.model.MyInfo;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.jellybase.R;
import com.jelly.jellybase.activity.BalanceActivity;
import com.jelly.jellybase.activity.BankCardListActivity;
import com.jelly.jellybase.activity.MessageActivity;
import com.jelly.jellybase.databinding.MeFragmentBinding;
/**
 * Created by Administrator on 2018/1/6.
 */

public class MeFragment extends BaseFragment<MeFragmentBinding> implements View.OnClickListener {
    private MyInfo myInfo;
    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getViewBinding().balanceLayout.setOnClickListener(this);
        getViewBinding().bankcardLayout.setOnClickListener(this);
        getViewBinding().messageLayout.setOnClickListener(this);
        iniXRefreshView();
        if (getActivity()!=null){
            if (BaseApplication.getInstance().isLogin()){
                if (myInfo==null){
                    //presenter.getMyInfo(lifecycleProvider.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
                }
            }else {
                getViewBinding().nameTv.setText("未登录");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity()!=null){
            if (BaseApplication.getInstance().isLogin()){
                if (myInfo==null){
                    //presenter.getMyInfo(lifecycleProvider.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
                }
            }else {
                getViewBinding().nameTv.setText("未登录");
            }
            if (AppPrefs.getBoolean(BaseApplication.getInstance(), ConfigKey.NEWMESSAGE)){
                getViewBinding().tvPoint.setVisibility(View.GONE);
            }else {
                getViewBinding().tvPoint.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void setData(String json) {

    }
    @Override
    public boolean onBackPressed() {
        return false;
    }
    private void iniXRefreshView(){
        getViewBinding().xrefreshview.setAutoRefresh(false);
        getViewBinding().xrefreshview.setPullLoadEnable(true);
        getViewBinding().xrefreshview.setPullRefreshEnable(true);
        getViewBinding().xrefreshview.setPinnedTime(1000);
        getViewBinding().xrefreshview.setMoveForHorizontal(true);
        getViewBinding().xrefreshview.setAutoLoadMore(false);
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
                    //xRefreshView.setLoadComplete(true);
                    // 刷新完成必须调用此方法停止加载
                    getViewBinding().xrefreshview.stopLoadMore();
                }
            }, 1000);
        }
    };
    public void onClick(View view){
        if (AntiShake.check(view.getId()))return;
        Intent intent;
        switch (view.getId()){
            case R.id.balance_layout:
                intent=new Intent(BaseApplication.getInstance(), BalanceActivity.class);
                intent.putExtra("Balance",getViewBinding().balanceTv.getText().toString().trim());
                startActivity(intent);
                break;
            case R.id.bankcard_layout:
                intent=new Intent(BaseApplication.getInstance(), BankCardListActivity.class);
                startActivity(intent);
                break;
            case R.id.message_layout:
                intent=new Intent(BaseApplication.getInstance(), MessageActivity.class);
                startActivity(intent);
                break;
        }
    }
}
