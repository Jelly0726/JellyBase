package com.jelly.jellybase.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.listener.OnTopRefreshTime;
import com.jelly.baselibrary.BaseFragment;
import com.jelly.baselibrary.webview.tbs.TBSClientCallBack;
import com.jelly.jellybase.databinding.HomeFragmentBinding;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by Administrator on 2018/1/6.
 */

public class HomeFragment extends BaseFragment<HomeFragmentBinding> {
    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            getViewBinding().webView.setVisible(isVisible);
            getViewBinding().webView.loadUrl("https://www.baidu.com");
        }
    }

    @Override
    public void onFragmentFirstVisible() {
        getViewBinding().webView.setVisible(true);
        getViewBinding().webView.loadUrl("https://www.baidu.com");
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
        if (getViewBinding().webView != null && getViewBinding().webView.canGoBack()) {
            getViewBinding().webView.goBack();
            return true;
        }
        return false;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniData();
    }
    private void iniData(){
        getViewBinding().webView.setVisible(getUserVisibleHint());
        getViewBinding().webView.loadUrl("https://www.baidu.com");
        getViewBinding().webView.setClientCallBack(new TBSClientCallBack(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress>=100&&getActivity()!=null){
                    getViewBinding().webView.setVisible(true);
                    getViewBinding().customView.stopRefresh();
                }
            }
        });
        getViewBinding().customView.setPullLoadEnable(false);
        getViewBinding().customView.setAutoRefresh(false);
        getViewBinding().customView.setAutoLoadMore(false);
        getViewBinding().customView.setPinnedTime(1000);
        getViewBinding().customView.setMoveForHorizontal(true);
        getViewBinding().customView.setOnTopRefreshTime(new OnTopRefreshTime() {

            @Override
            public boolean isTop() {
                if (getViewBinding().webView.getWebScrollY() == 0) {
                    View firstVisibleChild = getViewBinding().webView.getChildAt(0);
                    return firstVisibleChild.getTop() >= 0;
                }
                return false;
            }
        });
        getViewBinding().customView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh(boolean isPullDown) {
                getViewBinding().webView.setVisible(false);
                getViewBinding().webView.reload();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
            }
        });
    }
}
