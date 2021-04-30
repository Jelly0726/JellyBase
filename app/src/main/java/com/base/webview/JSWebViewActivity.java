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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.log.LogUtils;
import com.jelly.baselibrary.token.GlobalToken;
import com.jelly.baselibrary.webview.DownloadCompleteReceiver;
import com.jelly.baselibrary.webview.WebConfig;
import com.jelly.baselibrary.webview.WebTools;
import com.jelly.baselibrary.webview.tbs.TBSClientCallBack;
import com.jelly.baselibrary.webview.tbs.WebViewJavaScriptFunction;
import com.jelly.baselibrary.webview.tbs.X5WebView;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.BaseTbsWebviewBinding;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;


/**
 * Created by Administrator on 2017/2/7.
 */

public class JSWebViewActivity extends BaseActivity<BaseTbsWebviewBinding>implements View.OnClickListener {
    private X5WebView mWebView;
    private WebTools webTools;
    private DownloadCompleteReceiver receiver;

    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> arg1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        try {
            if (Integer.parseInt(Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        webTools = (WebTools) getIntent().getParcelableExtra(WebConfig.CONTENT);
        if (webTools == null) {
            webTools = new WebTools();
        }
        // 文件下载成功广播接受者
        receiver = new DownloadCompleteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, intentFilter);
        getBinding().leftBack.setOnClickListener(this);
        //WebView
        init();
    }
    private void init() {
        //mRefreshLayout.setEnabled(false);//关闭滑动刷新
        getBinding().refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.setVisible(false);
                mWebView.reload();
            }
        }); // 刷新监听。
        // 设置子视图是否允许滚动到顶部
        getBinding().refreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override//返回true 就是子布局手势，false 就是自己使用
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                LogUtils.i("getScrollY()="+child.getScrollY());
                if (child.getScrollY() > 0)
                    return true;
                return false;
            }
        });
        mWebView = new X5WebView(this, null);
//        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);//开启硬件加速
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//滚动条在WebView内侧显示
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条在WebView外侧显示
        getBinding().webfilechooser.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));
        mWebView.setOnScrollChangedCallback(new X5WebView.OnScrollChangedCallback() {
            public void onScroll(int l, int t) {
                //LogUtils.d("We Scrolled etc..." + l + " t =" + t);
                if (t == 0) {//webView在顶部
                    getBinding().refreshLayout.setEnabled(true);
                } else {//webView不是顶部
                    getBinding().refreshLayout.setEnabled(false);
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
                LogUtils.i("data="+data);
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
            getBinding().titleTv.setText(webTools.title);
        }
        mWebView.setClientCallBack(new TBSClientCallBack(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                getBinding().topNavLayout.setVisibility(View.VISIBLE);
                LogUtils.i("onPageStarted  url="+url);
            }

            @Override
            public void onReceivedTitle(WebView arg0, String arg1) {
                LogUtils.i("onReceivedTitle  arg1="+arg1);
                if (!arg1.contains("Page Error")
                        &&!arg1.contains("about:blank")){
                    getBinding().topNavLayout.setVisibility(View.GONE);
                }else {
                    getBinding().topNavLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress>=100){
                    mWebView.setVisible(true);
                    getBinding().refreshLayout.setRefreshing(false);
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
   @Override
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
            getBinding().webfilechooser.removeAllViews();
            mWebView.stopLoading();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        unregisterReceiver(receiver);
        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == X5WebView.FILE_CHOOSER) {
            if (null == uploadFile && null == arg1) return;
            if (data!=null){
                LogUtils.i("文件路径"+data.getData());
            }
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (arg1 != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadFile != null) {
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

        arg1.onReceiveValue(results);
        arg1 = null;
    }
}
