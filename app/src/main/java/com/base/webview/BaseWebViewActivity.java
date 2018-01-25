package com.base.webview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.view.BaseActivity;
import com.base.webview.jsbridge.tbs.BridgeTBSWebView;
import com.base.webview.jsbridge.tbs.TBSClientCallBack;
import com.jelly.jellybase.R;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/3.
 */

public class BaseWebViewActivity extends BaseActivity {
    private WebTools webTools;
    @BindView(R.id.forum_context)
    BridgeTBSWebView Web;
    @BindView(R.id.topNav_layout)
    LinearLayout topNav_layout;
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_webview_activity);
        ButterKnife.bind(this);
        webTools = getIntent().getParcelableExtra(WebConfig.CONTENT);
        if (webTools == null) {
            webTools = new WebTools();
        }
        iniWebView();
    }

    private void iniWebView() {
        //WebView
        Web.setVisible(true);
        if (!TextUtils.isEmpty(webTools.url)) {
            Web.loadUrl(webTools.url);
        }
        title_tv.setText(webTools.title);
        Web.setClientCallBack(new TBSClientCallBack(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                topNav_layout.setVisibility(View.GONE);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                topNav_layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                topNav_layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse error) {
                topNav_layout.setVisibility(View.VISIBLE);
            }
        });
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
    public void onBackPressed() {
        if (Web != null && Web.canGoBack()) {
            Web.goBack();
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
                if (Web != null && Web.canGoBack()) {
                    Web.goBack();
                    return true;
                } else
                    return super.onKeyDown(keyCode, event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onKeyDown(keyCode, event);
    }
}
