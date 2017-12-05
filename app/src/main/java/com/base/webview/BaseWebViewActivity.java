package com.base.webview;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.jelly.jellybase.R;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by Administrator on 2017/11/3.
 */

public class BaseWebViewActivity extends BaseActivityImpl {
    private WebTools webTools;
    private WebView Web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_webview_activity);
        webTools = getIntent().getParcelableExtra(WebConfig.CONTENT);
        if (webTools == null) {
            webTools = new WebTools();
        }
        iniWebView();
    }

    @Override
    public IBasePresenter initPresenter() {
        return null;
    }

    private void iniWebView() {
        //WebView
        Web = (WebView) findViewById(R.id.forum_context);
        //设置可自由缩放网页
        //servicePriceWeb.getSettings().setSupportZoom(true);
        //servicePriceWeb.getSettings().setBuiltInZoomControls(true);
        //设置WebView属性，能够执行JavaScript脚本
        Web.getSettings().setJavaScriptEnabled(true);
        Web.getSettings().setAllowFileAccess(true); // 允许访问文件
        Web.getSettings().setAppCacheEnabled(false);//缓存
        Web.getSettings().setLoadsImagesAutomatically(true);//自动加载图片
        /**自适应屏幕
         * 方法一
         * 	设置加载进来的页面自适应手机屏幕
         */
        //第一个方法设置webview推荐使用的窗口，设置为true
        Web.getSettings().setUseWideViewPort(true);
        //第二个方法是设置webview加载的页面的模式，也设置为true。
        Web.getSettings().setLoadWithOverviewMode(true);
        /*** 方法二
         *  LayoutAlgorithm是一个枚举用来控制页面的布局，有三个类型：
         1.NARROW_COLUMNS：可能的话使所有列的宽度不超过屏幕宽度
         2.NORMAL：正常显示不做任何渲染
         3.SINGLE_COLUMN：把所有内容放大webview等宽的一列中
         */
        //Web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        /*** 方法三
         * 主要用于平板，针对特定屏幕代码调整分辨率
         */
        /*DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int mDensity = metrics.densityDpi;
		//Log.d("maomao", "densityDpi = " + mDensity);
		if (mDensity == 240) {
			Web.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		} else if (mDensity == 160) {
			Web.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		} else if(mDensity == 120) {
			Web.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
		}else if(mDensity == DisplayMetrics.DENSITY_XHIGH){
			Web.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		}else if (mDensity == DisplayMetrics.DENSITY_TV){
			Web.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		}else{
			Web.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		}*/
        Web.setEnabled(true);
        // 如果页面中链接，如果希望点击链接继续在当前servicePriceWeb中响应，
        // 而不是新开Android的系统servicePriceWeb中响应该链接，必须覆盖webview的WebViewClient对象
        Web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
        Web.getSettings().setJavaScriptEnabled(true);
        Web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsHidePrompt() {
                super.onGeolocationPermissionsHidePrompt();
                Log.i("test", "onGeolocationPermissionsHidePrompt");
            }

            //网页权限弹窗
            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin,
                                                           final GeolocationPermissionsCallback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                Log.i("test", "onGeolocationPermissionsShowPrompt");
            }
        });
        if (!TextUtils.isEmpty(webTools.url)) {
            Web.loadUrl(webTools.url);
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
