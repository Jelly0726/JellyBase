package com.base.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.log.LogUtils;
import com.jelly.baselibrary.webview.WebConfig;
import com.jelly.baselibrary.webview.WebTools;
import com.jelly.baselibrary.webview.tbs.TBSClientCallBack;
import com.jelly.baselibrary.webview.tbs.X5WebView;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.BaseWebviewActivityBinding;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;


/**
 * Created by Administrator on 2017/11/3.
 */

public class BaseWebViewActivity extends BaseActivity<BaseWebviewActivityBinding> implements View.OnClickListener {
    private WebTools webTools;
    private X5WebView mWebView;

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
        webTools = getIntent().getParcelableExtra(WebConfig.CONTENT);
        if (webTools == null) {
            webTools = new WebTools();
        }
        iniWebView();
    }
    private void iniWebView() {
        getViewBinding().leftBack.setOnClickListener(this);
        //WebView
        mWebView = new X5WebView(this, null);
//        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);//开启硬件加速
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//滚动条在WebView内侧显示
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条在WebView外侧显示
        getViewBinding().webfilechooser.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));
        mWebView.setVisible(true);
        if (!TextUtils.isEmpty(webTools.url)) {
            mWebView.loadUrl(webTools.url);
        }
        getViewBinding().titleTv.setText(webTools.title);
        mWebView.setClientCallBack(new TBSClientCallBack(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                getViewBinding().topNavLayout.setVisibility(View.VISIBLE);
                LogUtils.i("onPageStarted  url="+url);
            }

            @Override
            public void onReceivedTitle(WebView arg0, String arg1) {
                LogUtils.i("onReceivedTitle  arg1="+arg1);
                if (!arg1.contains("Page Error")
                        &&!arg1.contains("about:blank")){
                    getViewBinding().topNavLayout.setVisibility(View.GONE);
                }else {
                    getViewBinding().topNavLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            }
            @Override
            public boolean onShowFileChooser(WebView arg0, ValueCallback<Uri[]> arg1, WebChromeClient.FileChooserParams arg2) {
                BaseWebViewActivity.this.arg1=arg1;
                return false;
            }

            @Override
            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String captureType) {
                super.openFileChooser(uploadFile, acceptType, captureType);
                BaseWebViewActivity.this.uploadFile=uploadFile;
            }
        });
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
    protected void onPause() {
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
            getViewBinding().webfilechooser.removeAllViews();
            mWebView.stopLoading();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
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
