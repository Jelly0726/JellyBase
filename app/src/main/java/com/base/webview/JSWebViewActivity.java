package com.base.webview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.view.BaseActivity;
import com.base.webview.tbs.TBSClientCallBack;
import com.base.webview.tbs.WebViewJavaScriptFunction;
import com.base.webview.tbs.X5WebView;
import com.jelly.jellybase.R;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/2/7.
 */

public class JSWebViewActivity extends BaseActivity {
    @BindView(R.id.topNav_layout)
    LinearLayout topNav_layout;
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.web_filechooser)
    X5WebView mWebView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    private WebTools webTools;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_tbs_webview);
        ButterKnife.bind(this);
        webTools = (WebTools) getIntent().getParcelableExtra(WebConfig.CONTENT);
        if (webTools == null) {
            webTools = new WebTools();
        }
        //WebView
        init();
    }

    private void init() {
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.setVisible(false);
                mWebView.reload();
            }
        }); // 刷新监听。
        mWebView.setOnScrollChangedCallback(new X5WebView.OnScrollChangedCallback() {
            public void onScroll(int l, int t) {
                //Log.d(TAG, "We Scrolled etc..." + l + " t =" + t);
                if (t == 0) {//webView在顶部
                    mRefreshLayout.setEnabled(true);
                } else {//webView不是顶部
                    mRefreshLayout.setEnabled(false);
                }
            }
        });
        //js交互
        mWebView.addJavascriptInterface(new WebViewJavaScriptFunction() {
            @Override
            public void onJsFunctionCalled(String tag) {
                // TODO Auto-generated method stub

            }
            @JavascriptInterface
            public void onSkipPage(String data){
                Log.i("SSSS","data="+data);
            }
            /**
             * js调用java的返回事件
             * 注意：WebView的方法不能在其他线程操作
             */
            @JavascriptInterface
            public void onBack(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                });

            }
            /**
             * java 调用 js方法 并且 传值
             * 步骤：1、调用 js函数  2、js回调一个android方法得到参数  3、js处理函数
             * @return
             */
            @JavascriptInterface
            public String getToken(){
                return GlobalToken.getToken().getToken();
            }
            /**
             * 关闭当前的窗口
             */
            @JavascriptInterface
            public void closeCurrentWindow(){
                //JavaToJsActivity.this.finish();
            }
        }, "Android");
        if (webTools != null) {
            if (!TextUtils.isEmpty(webTools.url)) {
                mWebView.setVisible(true);
                mWebView.loadUrl(webTools.url);
            }
            title_tv.setText(webTools.title);
        }
        mWebView.setClientCallBack(new TBSClientCallBack(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                topNav_layout.setVisibility(View.VISIBLE);
                Log.i("SSSS","onPageStarted  url="+url);
            }

            @Override
            public void onReceivedTitle(WebView arg0, String arg1) {
                Log.i("SSSS","onReceivedTitle  arg1="+arg1);
                if (!arg1.contains("Page Error")
                        &&!arg1.contains("about:blank")){
                    topNav_layout.setVisibility(View.GONE);
                }else {
                    topNav_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress>=100){
                    mWebView.setVisible(true);
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }
    @OnClick({R.id.left_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_back:
                finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //go back
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            // Check if the key event was the Back button and if there's history
            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                } else
                    return super.onKeyDown(keyCode, event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        //mWebView.loadUrl("about:blank");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
