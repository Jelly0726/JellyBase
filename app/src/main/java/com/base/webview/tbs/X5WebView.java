package com.base.webview.tbs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.applicationUtil.MyApplication;
import com.base.mprogressdialog.MProgressUtil;
import com.maning.mndialoglibrary.MProgressDialog;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.Map;

//import com.tencent.smtt.sdk.WebBackForwardList;
//import com.tencent.smtt.sdk.WebHistoryItem;
//import com.tencent.smtt.sdk.WebStorage;
//import com.tencent.smtt.sdk.WebViewDatabase;

public class X5WebView extends WebView {
	public static final int FILE_CHOOSER = 0;
	private String resourceUrl = "";
	private WebView smallWebView;
	private static boolean isSmallWebViewDisplayed = false;
	private boolean isClampedY = false;
	private Map<String, Object> mJsBridges;
	private TextView tog;
	RelativeLayout.LayoutParams layoutParams;
	private RelativeLayout refreshRela;
	TextView title;
	private TBSClientCallBack tbsClientCallBack;
	private MProgressDialog progressDialog;
	private boolean isVisible=true;//视图是否可见
	private WebViewClient client = new WebViewClient() {
		/**
		 * 防止加载网页时调起系统浏览器
		 */
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			if (tbsClientCallBack!=null){
				tbsClientCallBack.shouldOverrideUrlLoading(view, url);
			}
			return false;
		}
		public void onReceivedHttpAuthRequest(WebView webview,
											  com.tencent.smtt.export.external.interfaces.HttpAuthHandler httpAuthHandlerhost,
											  String host,
											  String realm) {
			boolean flag = httpAuthHandlerhost.useHttpAuthUsernamePassword();
			if (tbsClientCallBack!=null){
				tbsClientCallBack.onReceivedHttpAuthRequest(webview, httpAuthHandlerhost,host,realm);
			}
		}
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view,
                                                          WebResourceRequest request) {
			// TODO Auto-generated method stub

			Log.e("should", "request.getUrl().toString() is " + request.getUrl().toString());
			if (tbsClientCallBack!=null){
				tbsClientCallBack.shouldInterceptRequest(view, request);
			}
			return super.shouldInterceptRequest(view, request);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (progressDialog != null&&isVisible) {
				progressDialog.show();
			}
			if (tbsClientCallBack!=null){
				tbsClientCallBack.onPageStarted(view, url, favicon);
			}
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (tbsClientCallBack!=null){
				tbsClientCallBack.onPageFinished(view, url);
			}

		}
		@Override
		public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse error) {
			Log.i("msg onReceivedHttpError", "request=" + request + " error=" + error);
			super.onReceivedHttpError(view, request, error);
			if (tbsClientCallBack!=null){
				tbsClientCallBack.onReceivedHttpError(view, request, error);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
			}

		}
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			//6.0以下执行
			//Log.i(TAG, "onReceivedError: ------->errorCode" + errorCode + ":" + description);
			//网络未连接
			showErrorPage();
			if (tbsClientCallBack!=null){
				tbsClientCallBack.onReceivedError(view, errorCode, description, failingUrl);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}

		//处理网页加载失败时
		@Override
		public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
			super.onReceivedError(view, request, error);
			//6.0以上执行
			//Log.i(TAG, "onReceivedError: ");
			showErrorPage();//显示错误页面
			if (tbsClientCallBack!=null){
				tbsClientCallBack.onReceivedError(view, request, error);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	};
	/**
	 * 显示自定义错误提示页面，用一个View覆盖在WebView
	 */
	public void showErrorPage() {
		loadUrl("file:///android_asset/webpage/404.html");
	}
	private WebChromeClient chromeClient = new WebChromeClient() {
		@Override
		public boolean onJsConfirm(WebView arg0, String arg1, String arg2, JsResult arg3) {
			if (tbsClientCallBack!=null){
				tbsClientCallBack.onJsConfirm(arg0, arg1, arg2, arg3);
			}
			return super.onJsConfirm(arg0, arg1, arg2, arg3);
		}

		View myVideoView;
		View myNormalView;
		IX5WebChromeClient.CustomViewCallback callback;

		///////////////////////////////////////////////////////////
		//
		/**
		 * 全屏播放配置
		 */
		@Override
		public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
			if (tbsClientCallBack!=null){
				tbsClientCallBack.onShowCustomView(view, customViewCallback);
			}
		}
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			Log.i("ss", "BridgeTBSWebViewClient onProgressChanged:----------->" + newProgress);
			if (newProgress == 100) {
				//loadingLayout.setVisibility(View.GONE);
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
			}
			if (tbsClientCallBack!=null){
				tbsClientCallBack.onProgressChanged(view, newProgress);
			}
		}
		@Override
		public void onHideCustomView() {
			if (callback != null) {
				callback.onCustomViewHidden();
				callback = null;
			}
			if (myVideoView != null) {
				ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
				viewGroup.removeView(myVideoView);
				viewGroup.addView(myNormalView);
			}
		}

		@Override
		public boolean onShowFileChooser(WebView arg0,
                                         ValueCallback<Uri[]> arg1, FileChooserParams arg2) {
			// TODO Auto-generated method stub
			Log.e("app", "onShowFileChooser");
			if (tbsClientCallBack!=null){
				tbsClientCallBack.onShowFileChooser(arg0, arg1, arg2);
			}
			return super.onShowFileChooser(arg0, arg1, arg2);
		}

		@Override
		public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String captureType) {
			if (tbsClientCallBack!=null){
				tbsClientCallBack.openFileChooser(uploadFile,acceptType,  captureType);
			}
		}


		@Override
		public boolean onJsAlert(WebView arg0, String arg1, String arg2, JsResult arg3) {
			/**
			 * 这里写入你自定义的window alert
			 */
			// AlertDialog.Builder builder = new Builder(getContext());
			// builder.setTitle("X5内核");
			// builder.setPositiveButton("确定", new
			// DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// // TODO Auto-generated method stub
			// dialog.dismiss();
			// }
			// });
			// builder.show();
			// arg3.confirm();
			// return true;
			Log.i("yuanhaizhou", "setX5webview = null");
			if (tbsClientCallBack!=null){
				tbsClientCallBack.onJsAlert(null, "www.baidu.com", "aa", arg3);
			}
			return super.onJsAlert(null, "www.baidu.com", "aa", arg3);
		}

		/**
		 * 对应js 的通知弹框 ，可以用来实现js 和 android之间的通信
		 */


		@Override
		public void onReceivedTitle(WebView arg0, final String arg1) {
			super.onReceivedTitle(arg0, arg1);
			Log.i("yuanhaizhou", "webpage title is " + arg1);
			if (tbsClientCallBack!=null){
				tbsClientCallBack.onReceivedTitle(arg0,arg1);
			}
			if (arg1.contains("404")){
				showErrorPage();
			}
		}
	};
	/**
	 * onAttachedToWindow在初始化视频播放（既创建view）之前调用，
	 */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}
	/**
	 *
	 onDetachedFromWindow在退出视频播放，销毁资源（既销毁view）之后调用。
	 */
	@Override
	protected void onDetachedFromWindow() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		super.onDetachedFromWindow();
	}
	@SuppressLint("SetJavaScriptEnabled")
	public X5WebView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
		this.setWebViewClientExtension(new X5WebViewEventHandler(this));// 配置X5webview的事件处理
		this.setWebViewClient(client);
		this.setWebChromeClient(chromeClient);
		//WebStorage webStorage = WebStorage.getInstance();
		initWebViewSettings();
		this.getView().setClickable(true);
		this.getView().setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		progressDialog = MProgressUtil.getInstance().getMProgressDialog(arg0);
	}

	private void initWebViewSettings() {
		//android:scrollbars="none"   隐藏滚动条
		this.setHorizontalScrollBarEnabled(false);//水平不显示
		this.setVerticalScrollBarEnabled(false); //垂直不显示
		this.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//滚动条在WebView内侧显示
		this.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条在WebView外侧显示
		WebSettings webSetting = this.getSettings();
		webSetting.setJavaScriptEnabled(true);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setAllowFileAccess(true);
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webSetting.setSupportZoom(true);
		webSetting.setBuiltInZoomControls(true);
		webSetting.setUseWideViewPort(true);
		webSetting.setSupportMultipleWindows(true);
		//webSetting.setLoadWithOverviewMode(true);
		webSetting.setAppCacheEnabled(true);
		//webSetting.setDatabaseEnabled(true);
		webSetting.setDomStorageEnabled(true);
		webSetting.setGeolocationEnabled(true);
		webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
		// webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
		webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
		//webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
		webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSetting.setAppCachePath(MyApplication.getMyApp().getDir("appcache", 0).getPath());
		webSetting.setDatabasePath(MyApplication.getMyApp().getDir("databases", 0).getPath());
		webSetting.setGeolocationDatabasePath(MyApplication.getMyApp().getDir("geolocation", 0)
				.getPath());
		// this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
		// settings 的设计
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		boolean ret = super.drawChild(canvas, child, drawingTime);
		return ret;
	}
	/**
	 * 设置视图是否可见
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.isVisible=visible;
	}
	public X5WebView(Context arg0) {
		super(arg0);
		setBackgroundColor(85621);
	}

	public static void setSmallWebViewEnabled(boolean enabled) {
		isSmallWebViewDisplayed = enabled;
	}

	/**
	 * 当webchromeClient收到 web的prompt请求后进行拦截判断，用于调起本地android方法
	 *
	 * @param methodName
	 *            方法名称
	 * @param blockName
	 *            区块名称
	 * @return true ：调用成功 ； false ：调用失败
	 */
	private boolean onJsPrompt(String methodName, String blockName) {

		if (this.mJsBridges != null ) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判定当前的prompt消息是否为用于调用native方法的消息
	 *
	 * @param msg
	 *            消息名称
	 * @return true 属于prompt消息方法的调用
	 */
	private boolean isMsgPrompt(String msg) {
		if (msg != null ) {
			return true;
		} else {
			return false;
		}
	}

	// TBS: Do not use @Override to avoid false calls
	public boolean tbs_dispatchTouchEvent(MotionEvent ev, View view) {
		boolean r = super.super_dispatchTouchEvent(ev);
		Log.d("Bran", "dispatchTouchEvent " + ev.getAction() + " " + r);
		return r;
	}

	// TBS: Do not use @Override to avoid false calls
	public boolean tbs_onInterceptTouchEvent(MotionEvent ev, View view) {
		boolean r = super.super_onInterceptTouchEvent(ev);
		return r;
	}

	protected void tbs_onScrollChanged(int l, int t, int oldl, int oldt, View view) {
		super_onScrollChanged(l, t, oldl, oldt);
	}

	protected void tbs_onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY, View view) {
		super_onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}

	protected void tbs_computeScroll(View view) {
		super_computeScroll();
	}

	protected boolean tbs_overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
									   int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent, View view) {
		return super_overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
				maxOverScrollY, isTouchEvent);
	}

	public void setTitle(TextView title) {
		this.title = title;
	}

	protected boolean tbs_onTouchEvent(MotionEvent event, View view) {
		return super_onTouchEvent(event);
	}

	public void setClientCallBack(TBSClientCallBack tbsClientCallBack) {
		this.tbsClientCallBack = tbsClientCallBack;
	}
}
