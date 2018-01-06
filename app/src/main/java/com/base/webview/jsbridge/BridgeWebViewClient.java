package com.base.webview.jsbridge;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.jelly.jellybase.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by bruce on 10/28/15.
 */
public class BridgeWebViewClient extends WebViewClient {
    private BridgeWebView webView;
    private static ProgressDialog progressDialog;
    private WebViewClientCallBack webViewClientCallBack;
    private View mErrorView;
    private Context context;
    public BridgeWebViewClient(BridgeWebView webView, Context context) {
        this.webView = webView;
        this.context = context;
        initErrorPage();//初始化自定义页面
        progressDialog = new ProgressDialog(context);
        //progressDialog.setTitle("加载提示");
        progressDialog.setMessage("正在加载.....");
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // Log.i(TAG, "onProgressChanged:----------->" + newProgress);
                if (newProgress == 100) {
                    //loadingLayout.setVisibility(View.GONE);
                }
            }


            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                // Log.i(TAG, "onReceivedTitle:title ------>" + title);
                if (title.contains("404")){
                    showErrorPage();
                }
            }
        });
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Log.i("msg","url="+url);
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) { // 如果是返回数据
            webView.handlerReturnData(url);
            return true;
        } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //
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
        if (BridgeWebView.toLoadJs != null) {
            BridgeUtil.webViewLoadLocalJs(view, BridgeWebView.toLoadJs);
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
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse error) {
        Log.i("msg onReceivedHttpError", "request=" + request + " error=" + error);
        super.onReceivedHttpError(view, request, error);

    }
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        //6.0以下执行
        //Log.i(TAG, "onReceivedError: ------->errorCode" + errorCode + ":" + description);
        //网络未连接
        showErrorPage();
    }

    //处理网页加载失败时
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        //6.0以上执行
        //Log.i(TAG, "onReceivedError: ");
        showErrorPage();//显示错误页面
    }
    /**
     * 显示自定义错误提示页面，用一个View覆盖在WebView
     */
    private void showErrorPage() {
        ((ViewGroup)webView.getParent()).removeAllViews(); //移除加载网页错误时，默认的提示信息
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ((ViewGroup)webView.getParent()).addView(mErrorView, 0, layoutParams); //添加自定义的错误提示的View
    }

    /***
     * 显示加载失败时自定义的网页
     */
    private void initErrorPage() {
        if (mErrorView == null) {
            mErrorView = View.inflate(context, R.layout.webview_load_error, null);
        }
    }
    public static void progressDialogDismiss() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    public void setWebViewClientCallBack(WebViewClientCallBack webViewClientCallBack) {
        this.webViewClientCallBack = webViewClientCallBack;
    }

    public interface WebViewClientCallBack {
        public boolean shouldOverrideUrlLoading(WebView view, String url);

        public void onPageStarted(WebView view, String url, Bitmap favicon);

        public void onPageFinished(WebView view, String url);
    }
}