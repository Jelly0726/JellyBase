package com.base.webview.jsbridge;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebHistoryItem;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by bruce on 10/28/15.
 */
public class BridgeTBSWebViewClient extends WebViewClient {
    private BridgeTBSWebView webView;
    private static ProgressDialog progressDialog;
    private TBSWebViewClientCallBack webViewClientCallBack;

    public BridgeTBSWebViewClient(BridgeTBSWebView webView, Context context) {
        this.webView = webView;
        progressDialog = new ProgressDialog(context);
        //progressDialog.setTitle("加载提示");
        progressDialog.setMessage("正在加载.....");
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //Log.i("msg","url="+url);
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (url.startsWith(BridgeTBSUtil.YY_RETURN_DATA)) { // 如果是返回数据
            webView.handlerReturnData(url);
            return true;
        } else if (url.startsWith(BridgeTBSUtil.YY_OVERRIDE_SCHEMA)) { //
            webView.flushMessageQueue();
            return true;
        } else {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (progressDialog != null) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }
        if (webViewClientCallBack != null) {
            webViewClientCallBack.onPageStarted(view, url, favicon);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
        if (webViewClientCallBack != null) {
            webViewClientCallBack.onPageFinished(view, url);
        }
        if (BridgeTBSWebView.toLoadJs != null) {
            BridgeTBSUtil.webViewLoadLocalJs(view, BridgeTBSWebView.toLoadJs);
        }

        //
        if (webView.getStartupMessage() != null) {
            for (Message m : webView.getStartupMessage()) {
                webView.dispatchMessage(m);
            }
            webView.setStartupMessage(null);
        }
    }

    /**
     * 添加历史记录
     *
     * @param view
     * @param url
     * @param isReload
     */
    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);
        WebHistoryItem webHistoryItem = webView.copyBackForwardList().getItemAtIndex(0);
        String uu = webHistoryItem.getOriginalUrl();
        //Log.i("msg","url="+url+" isReload="+isReload+" uu="+uu);
        if (url.contains(uu)) {
            webView.clearHistory();//清除历史记录
            //Log.i("msg","list="+list.getSize());
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Log.i("msg onReceivedError", "errorCode=" + errorCode + " description=" + description + " failingUrl=" + failingUrl);
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse error) {
        Log.i("msg onReceivedHttpError", "request=" + request + " error=" + error);
        super.onReceivedHttpError(view, request, error);

    }

    public static void progressDialogDismiss() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    public void setWebViewClientCallBack(TBSWebViewClientCallBack webViewClientCallBack) {
        this.webViewClientCallBack = webViewClientCallBack;
    }

    public interface TBSWebViewClientCallBack {
        public boolean shouldOverrideUrlLoading(WebView view, String url);

        public void onPageStarted(WebView view, String url, Bitmap favicon);

        public void onPageFinished(WebView view, String url);
    }
}