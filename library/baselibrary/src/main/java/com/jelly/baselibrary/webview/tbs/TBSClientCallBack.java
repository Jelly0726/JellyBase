package com.jelly.baselibrary.webview.tbs;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by Administrator on 2018/1/25.
 */

public class TBSClientCallBack {
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
       return false;
    }
    public void onReceivedHttpAuthRequest(WebView webview,
                                          com.tencent.smtt.export.external.interfaces.HttpAuthHandler httpAuthHandlerhost, String host,
                                          String realm) {

    }
    public WebResourceResponse shouldInterceptRequest(WebView view,
                                                      WebResourceRequest request) {
        return null;
    }
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
    }

    public void onPageFinished(WebView view, String url) {
    }
    /**
     * 添加历史记录
     *
     * @param view
     * @param url
     * @param isReload
     */
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
    }
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse error) {

    }
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    }
    //处理网页加载失败时
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
    }
    public boolean onJsConfirm(WebView arg0, String arg1, String arg2, JsResult arg3) {
        return true;
    }
    /**
     * 全屏播放配置
     */
    public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
    }

    public void onHideCustomView() {
    }

    public boolean onShowFileChooser(WebView arg0,
                                     ValueCallback<Uri[]> arg1, WebChromeClient.FileChooserParams arg2) {
        // TODO Auto-generated method stub
        return true;
    }
    public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String captureType) {
    }

    /**
     * 对应js 的通知弹框 ，可以用来实现js 和 android之间的通信
     */
    public boolean onJsAlert(WebView arg0, String arg1, String arg2, JsResult arg3) {
        return true;
    }


    public void onReceivedTitle(WebView arg0, final String arg1) {

    }

    public void onGeolocationPermissionsHidePrompt() {
    }

    public void onGeolocationPermissionsShowPrompt(String s,
                                                   GeolocationPermissionsCallback geolocationPermissionsCallback) {
    }


    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
    }
    public void onProgressChanged(WebView view, int newProgress) {
    }
}
