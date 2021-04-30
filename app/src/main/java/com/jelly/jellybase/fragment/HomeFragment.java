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
            getBinding().webView.setVisible(isVisible);
            getBinding().webView.loadUrl("https://www.baidu.com");
        }
    }

    @Override
    public void onFragmentFirstVisible() {
        getBinding().webView.setVisible(true);
        getBinding().webView.loadUrl("https://www.baidu.com");
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
        if (getBinding().webView != null && getBinding().webView.canGoBack()) {
            getBinding().webView.goBack();
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
        getBinding().webView.setVisible(getUserVisibleHint());
        getBinding().webView.loadUrl("https://www.baidu.com");
        getBinding().webView.setClientCallBack(new TBSClientCallBack(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress>=100&&getActivity()!=null){
                    getBinding().webView.setVisible(true);
                    getBinding().customView.stopRefresh();
                }
            }
        });
        getBinding().customView.setPullLoadEnable(false);
        getBinding().customView.setAutoRefresh(false);
        getBinding().customView.setAutoLoadMore(false);
        getBinding().customView.setPinnedTime(1000);
        getBinding().customView.setMoveForHorizontal(true);
        getBinding().customView.setOnTopRefreshTime(new OnTopRefreshTime() {

            @Override
            public boolean isTop() {
                if (getBinding().webView.getWebScrollY() == 0) {
                    View firstVisibleChild = getBinding().webView.getChildAt(0);
                    return firstVisibleChild.getTop() >= 0;
                }
                return false;
            }
        });
        getBinding().customView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh(boolean isPullDown) {
                getBinding().webView.setVisible(false);
                getBinding().webView.reload();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
            }
        });
    }
}
