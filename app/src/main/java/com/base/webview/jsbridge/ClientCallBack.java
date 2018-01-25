package com.base.webview.jsbridge;

import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

/**
 * Created by Administrator on 2018/1/25.
 */

public class ClientCallBack {
    public boolean shouldOverrideUrlLoading(WebView view, String url) { return false;}
    public void onPageStarted(WebView view, String url, Bitmap favicon) {}
    public void onPageFinished(WebView view, String url) {}
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {}
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse error) {}
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {}
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {}
    public void onProgressChanged(WebView view, int newProgress) {}
    public void onReceivedTitle(WebView arg0, final String arg1) {}
    public void onReceivedIcon(WebView view, Bitmap icon) {
    }

    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {

    }

    public void onGeolocationPermissionsHidePrompt() {
    }

    public void onPermissionRequest(PermissionRequest request) {
    }

    public void onPermissionRequestCanceled(PermissionRequest request) {
    }

    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        return false;
    }

    public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
    }
}
