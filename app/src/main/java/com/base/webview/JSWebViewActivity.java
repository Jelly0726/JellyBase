package com.base.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.log.DebugLog;
import com.base.view.BaseActivity;
import com.base.webview.tbs.TBSClientCallBack;
import com.base.webview.tbs.WebViewJavaScriptFunction;
import com.base.webview.tbs.X5WebView;
import com.jelly.jellybase.R;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
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
    private X5WebView mWebView;
    @BindView(R.id.webfilechooser)
    ViewGroup mViewParent;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    private WebTools webTools;
    private DownloadCompleteReceiver receiver;

    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> arg1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.base_tbs_webview);
        ButterKnife.bind(this);
        webTools = (WebTools) getIntent().getParcelableExtra(WebConfig.CONTENT);
        if (webTools == null) {
            webTools = new WebTools();
        }
        // 文件下载成功广播接受者
        receiver = new DownloadCompleteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, intentFilter);

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
        mWebView = new X5WebView(this, null);
//        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);//开启硬件加速
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//滚动条在WebView内侧显示
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条在WebView外侧显示
        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));
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
            @Override
            public boolean onShowFileChooser(WebView arg0, ValueCallback<Uri[]> arg1, WebChromeClient.FileChooserParams arg2) {
                JSWebViewActivity.this.arg1=arg1;
                return false;
            }

            @Override
            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String captureType) {
                super.openFileChooser(uploadFile, acceptType, captureType);
                JSWebViewActivity.this.uploadFile=uploadFile;
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
        // 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
        //vue 的方法要在 created() { window.onPause=this.onPause;}
        if (Build.VERSION.SDK_INT < 18) {
            mWebView.loadUrl("javascript:onPause()");
        } else {
            mWebView.evaluateJavascript("javascript:onPause()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mViewParent.removeAllViews();
            mWebView.stopLoading();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
            mViewParent = null;
        }
        unregisterReceiver(receiver);
        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == X5WebView.FILE_CHOOSER) {
            if (null == uploadFile && null == arg1) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (arg1 != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadFile != null) {
                DebugLog.i("文件路径"+data.getData().getPath());
                uploadFile.onReceiveValue(result);
                uploadFile = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != X5WebView.FILE_CHOOSER || arg1 == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        DebugLog.i("文件路径"+results[0].getPath());
        arg1.onReceiveValue(results);
        arg1 = null;
    }
}
