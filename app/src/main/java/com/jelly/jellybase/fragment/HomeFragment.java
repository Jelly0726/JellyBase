package com.jelly.jellybase.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.view.BaseFragment;
import com.base.webview.jsbridge.BridgeTBSWebView;
import com.jelly.jellybase.R;
import com.yanzhenjie.sofia.StatusView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/1/6.
 */

public class HomeFragment extends BaseFragment {
    private View mRootView;
    private Unbinder unbinder;
    @BindView(R.id.webView)
    BridgeTBSWebView webView;

    @BindView(R.id.status_view)
    StatusView mStatusView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.home_fragment, container, false);
        unbinder= ButterKnife.bind(this,mRootView);
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
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
        //↓↓↓↓↓自定义状态栏底色。↓↓↓↓↓
        mStatusView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gary));
        //↑↑↑↑↑自定义状态栏底色。↑↑↑↑↑
        iniData();
    }
    private void iniData(){
        webView.loadUrl("https://www.baidu.com");
    }
}
