package com.base.webview.tbs;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.base.mprogressdialog.MProgressUtil;
import com.base.webview.jsbridge.Message;
import com.maning.mndialoglibrary.MProgressDialog;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
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
    private MProgressDialog progressDialog;
    private TBSClientCallBack webViewClientCallBack;

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    private boolean isVisible=true;//视图是否可见
    public BridgeTBSWebViewClient(BridgeTBSWebView webView, Context context) {
        this.webView = webView;
        progressDialog = MProgressUtil.getInstance().getMProgressDialog(context);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //Log.i("msg","url="+url);
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (webViewClientCallBack!=null){
            webViewClientCallBack.shouldOverrideUrlLoading(view,url);
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
        if (progressDialog != null&&isVisible) {
            progressDialog.show();
        }
        if (webViewClientCallBack != null) {
            webViewClientCallBack.onPageStarted(view, url, favicon);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
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
        if (webViewClientCallBack != null) {
            webViewClientCallBack.onPageFinished(view, url);
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
            //webView.clearHistory();//清除历史记录
            //Log.i("msg","list="+list.getSize());
        }
        if (webViewClientCallBack!=null){
            webViewClientCallBack.doUpdateVisitedHistory(view, url, isReload);
        }
    }
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse error) {
        Log.i("msg onReceivedHttpError", "request=" + request + " error=" + error);
        super.onReceivedHttpError(view, request, error);
        if (webViewClientCallBack!=null){
            webViewClientCallBack.onReceivedHttpError(view, request, error);
        }
        progressDialogDismiss();

    }
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        //6.0以下执行
        //Log.i(TAG, "onReceivedError: ------->errorCode" + errorCode + ":" + description);
        //网络未连接
        webView.showErrorPage();
        if (webViewClientCallBack!=null){
            webViewClientCallBack.onReceivedError(view, errorCode, description, failingUrl);
        }
        progressDialogDismiss();
    }

    //处理网页加载失败时
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        //6.0以上执行
        //Log.i(TAG, "onReceivedError: ");
        webView.showErrorPage();//显示错误页面
        if (webViewClientCallBack!=null){
            webViewClientCallBack.onReceivedError(view, request, error);
        }
        progressDialogDismiss();
    }

    public void progressDialogDismiss() {
        if (progressDialog != null) {
                progressDialog.dismiss();
        }
    }

    public void setClientCallBack(TBSClientCallBack webViewClientCallBack) {
        this.webViewClientCallBack = webViewClientCallBack;
    }
}