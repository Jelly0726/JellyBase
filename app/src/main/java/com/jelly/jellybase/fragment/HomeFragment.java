package com.jelly.jellybase.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.base.view.BaseFragment;
import com.base.webview.jsbridge.BridgeTBSWebView;
import com.jelly.jellybase.R;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/1/6.
 */

public class HomeFragment extends BaseFragment {
    @BindView(R.id.webView)
    BridgeTBSWebView webView;

    @Override
    protected int getLayoutResource() {
        return R.layout.home_fragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {

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
        webView.loadUrl("https://www.baidu.com");
    }
}
