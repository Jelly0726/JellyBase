package com.jelly.jellybase.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.view.BaseFragment;
import com.base.webview.jsbridge.tbs.BridgeTBSWebView;
import com.jelly.jellybase.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/1/6.
 */

public class HomeFragment extends BaseFragment {
    private Unbinder mUnbinder;
    @BindView(R.id.webView)
    BridgeTBSWebView webView;
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
    }
}
