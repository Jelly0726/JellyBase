package com.jelly.jellybase.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.view.BaseFragment;
import com.base.webview.tbs.TBSClientCallBack;
import com.base.webview.tbs.X5WebView;
import com.base.xrefreshview.XRefreshView;
import com.base.xrefreshview.listener.OnTopRefreshTime;
import com.jelly.jellybase.R;
import com.tencent.smtt.sdk.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/1/6.
 */

public class HomeFragment extends BaseFragment {
    private Unbinder mUnbinder;
    @BindView(R.id.webView)
    X5WebView webView;
    @BindView(R.id.custom_view)
    XRefreshView custom_view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null)
            rootView = inflater.inflate(R.layout.home_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            webView.setVisible(isVisible);
            webView.loadUrl("https://www.baidu.com");
        }
    }

    @Override
    public void onFragmentFirstVisible() {
        webView.setVisible(true);
        webView.loadUrl("https://www.baidu.com");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
    @Override
    public void setData(String json) {

    }
    @Override
    public boolean onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
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
        webView.setVisible(getUserVisibleHint());
        webView.loadUrl("https://www.baidu.com");
        webView.setClientCallBack(new TBSClientCallBack(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress>=100&&getActivity()!=null){
                    webView.setVisible(true);
                    custom_view.stopRefresh();
                }
            }
        });
        custom_view.setPullLoadEnable(false);
        custom_view.setAutoRefresh(false);
        custom_view.setAutoLoadMore(false);
        custom_view.setPinnedTime(1000);
        custom_view.setMoveForHorizontal(true);
        custom_view.setOnTopRefreshTime(new OnTopRefreshTime() {

            @Override
            public boolean isTop() {
                if (webView.getWebScrollY() == 0) {
                    View firstVisibleChild = webView.getChildAt(0);
                    return firstVisibleChild.getTop() >= 0;
                }
                return false;
            }
        });
        custom_view.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh(boolean isPullDown) {
                webView.setVisible(false);
                webView.reload();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
            }
        });
    }
}
