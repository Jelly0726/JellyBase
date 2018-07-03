package com.base.webview.tbs;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.appManager.MyApplication;
import com.base.mprogressdialog.MProgressUtil;
import com.jelly.jellybase.R;
import com.maning.mndialoglibrary.MProgressDialog;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.IX5WebSettings;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebBackForwardList;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebHistoryItem;
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
	//webview的滑动回调
	private OnScrollChangedCallback mOnScrollChangedCallback;
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
			//加载使用默认缓存
			view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
			view.loadUrl(url);
			if (tbsClientCallBack!=null){
				tbsClientCallBack.shouldOverrideUrlLoading(view, url);
			}
			return false;
		}
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
			try {
				if (url.startsWith("http") || url.startsWith("https")) { //http和https协议开头的执行正常的流程
					return super.shouldInterceptRequest(view, url);
				} else { //其他的URL则会开启一个Acitity然后去调用原生APP
					Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					MyApplication.getMyApp().startActivity(in);
					return null;
				}
			}catch (ActivityNotFoundException e){
				return super.shouldInterceptRequest(view, url);
			}
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
		/**
		 * 更新历史记录
		 *
		 * @param view
		 * @param url
		 * @param isReload
		 */
		@Override
		public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
			super.doUpdateVisitedHistory(view, url, isReload);
			if (tbsClientCallBack!=null){
				tbsClientCallBack.doUpdateVisitedHistory(view, url, isReload);
			}
			WebHistoryItem webHistoryItem = copyBackForwardList().getItemAtIndex(0);
			String uu = webHistoryItem.getOriginalUrl();
			//Log.i("msg","url="+url+" isReload="+isReload+" uu="+uu);
			if (url.contains(uu)) {
				//webView.clearHistory();//清除历史记录
				//Log.i("msg","list="+list.getSize());
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
	 * 加载
	 */
	@Override
	public void loadUrl(String var1) {
		if (var1.contains("android_asset/webpage/404.html")){
			super.loadUrl(var1);
			return;
		}
		WebBackForwardList webBackForwardList=copyBackForwardList();
		if (webBackForwardList.getSize()==0){
			super.loadUrl(var1);
			return;
		}
		WebHistoryItem webHistoryItem = webBackForwardList.getItemAtIndex(webBackForwardList.getSize()-1);
		String uu = webHistoryItem.getOriginalUrl();
		if (uu.contains("android_asset/webpage/404.html")
				||!uu.equals(var1)) {
			if (uu.contains("android_asset/webpage/404.html")){
				clearHistory();
			}
			super.loadUrl(var1);
		}
	}
	/**
	 * 重新加载
	 */
	@Override
	public void reload() {
		//重新加载不使用缓存
		clearCache(true);
		getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		WebBackForwardList webBackForwardList=copyBackForwardList();
		if (webBackForwardList.getSize()<=1){
			super.reload();
			return;
		}
		WebHistoryItem webHistoryItem = webBackForwardList.getItemAtIndex(webBackForwardList.getSize()-1);
		String uu = webHistoryItem.getOriginalUrl();
		if (uu.contains("android_asset/webpage/404.html")) {
			uu=webBackForwardList.getItemAtIndex(0).getOriginalUrl();
			//goBack();
			clearHistory();
			loadUrl(uu);
		}else
			super.reload();
	}
	@Override
	public void goBack() {
		//返回使用缓存
		getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		super.goBack();
	}
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
		/**
		 * 全屏播放配置
		 */
		@Override
		public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
			FrameLayout normalView = (FrameLayout) findViewById(R.id.webfilechooser);
			ViewGroup viewGroup = (ViewGroup) normalView.getParent();
			viewGroup.removeView(normalView);
			viewGroup.addView(view);
			myVideoView = view;
			myNormalView = normalView;
			callback = customViewCallback;
			if (tbsClientCallBack!=null){
				tbsClientCallBack.onShowCustomView(view, customViewCallback);
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

		/**
		 * 告诉客户端显示一个文件选择器。获取Android本地文件
		 该方法的作用，告诉当前APP，打开一个文件选择器，比如：打开相册、启动拍照或打开本地文件管理器，
		 实际上更好的理解，WebView加载包含上传文件的表单按钮，HTML定义了input标签，同时input的type类型为file，
		 手指点击该按钮，回调onShowFileChooser这个方法，在这个重写的方法里面打开相册、启动照片或打开本地文件管理器，
		 甚至做其他任何的逻辑处理，点击一次回调一次的前提是请求被取消，而取消该请求回调的方法：
		 给ValueCallback接口的onReceiveValue抽象方法传入null，同时onShowFileChooser方法返回true；
		 ValueCallback的抽象方法被回调onShowFileChooser方法返回true；反之返回false；
		 * @param arg0  启动请求的WebView实例。
		 * @param arg1  结果回调，将选取的文件回传给HTML  如果用户按下“选择文件”按钮，调用此回调以提供要上载的文件路径列表，
		 *                    或取消则回调filePathCallback.onReceiveValue(null)传NULL。
		 *              onReceiveValue传入Uri对象数组
		 * @param arg2  描述要打开的文件选择器的模式，以及与之一起使用的选项。
		 * @return     返回true，如果filePathCallback将被调用，false使用默认处理。用户按下“选择文件”按钮。
		 */
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
		/**
		 * 获取Android本地文件
		 在所有发布的SDK版本中，openFileChooser是一个隐藏的方法，
		 使用onShowFileChooser代替，
		 但是最好同时重写showFileChooser和openFileChooser方法，
		 Android 4.4.X以上的系统回调onShowFileChooser方法，
		 低于或等于Android 4.4.X的系统回调openFileChooser方法，
		 只重写onShowFileChooser或openFileChooser造成在有的系统可以正常回调，在有的系统点击没有反应。
		 * @param uploadFile  结果回调，将选取的文件回传给HTML  onReceiveValue传入一个Uri对象
		 * @param acceptType
		 * @param captureType
		 * @return
		 */
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
			if (arg1.contains("网页无法打开")){
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
		this.setHorizontalScrollBarEnabled(false);//水平不显示
		this.setVerticalScrollBarEnabled(false); //垂直不显示
		//this.setLayerType(View.LAYER_TYPE_HARDWARE,null);//开启硬件加速
		this.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//滚动条在WebView内侧显示
		this.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条在WebView外侧显示
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
		WebSettings webSetting = this.getSettings();
		//* 是否支持缩放，配合方法setBuiltInZoomControls使用，默认true
		webSetting.setSupportZoom(true);
		// * 是否需要用户手势来播放Media，默认true
		webSetting.setMediaPlaybackRequiresUserGesture(true);
		// * 是否使用WebView内置的缩放组件，由浮动在窗口上的缩放控制和手势缩放控制组成，默认false
		webSetting.setBuiltInZoomControls(true);
		// * 是否显示窗口悬浮的缩放控制，默认true
		webSetting.setDisplayZoomControls(false);
		// * 是否允许访问WebView内部文件，默认true
		webSetting.setAllowFileAccess(true);
		// * 是否允许获取WebView的内容URL ，可以让WebView访问ContentPrivider存储的内容。 默认true
		webSetting.setAllowContentAccess(true);
		// * 是否启动概述模式浏览界面，当页面宽度超过WebView显示宽度时，缩小页面适应WebView。默认false
		webSetting.setLoadWithOverviewMode(true);
		// * 是否保存表单数据，默认false
		webSetting.setSaveFormData(false);
		// * 设置页面文字缩放百分比，默认100%
		//webSetting.setTextZoom(int textZoom);
		// * 是否支持ViewPort的meta tag属性，如果页面有ViewPort meta tag 指定的宽度，则使用meta tag指定的值，否则默认使用宽屏的视图窗口
		webSetting.setUseWideViewPort(true);
		// * 是否支持多窗口，如果设置为true ，WebChromeClient#onCreateWindow方法必须被主程序实现，默认false
		webSetting.setSupportMultipleWindows(true);
		// * 指定WebView的页面布局显示形式，调用该方法会引起页面重绘。默认LayoutAlgorithm#NARROW_COLUMNS
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		// * 设置标准的字体族，默认”sans-serif”。font-family 规定元素的字体系列。
		// * font-family 可以把多个字体名称作为一个“回退”系统来保存。如果浏览器不支持第一个字体，
		// * 则会尝试下一个。也就是说，font-family 属性的值是用于某个元素的字体族名称或/及类族名称的一个
		// * 优先表。浏览器会使用它可识别的第一个值。
		//webSetting.setStandardFontFamily(String font);
		// * 设置混合字体族。默认”monospace”
		//webSetting.setFixedFontFamily(String font);
		// * 设置SansSerif字体族。默认”sans-serif”
		//webSetting.setSansSerifFontFamily(String font);
		// * 设置SerifFont字体族，默认”sans-serif”
		//webSetting.setSerifFontFamily(String font);
		// * 设置CursiveFont字体族，默认”cursive”
		//webSetting.setCursiveFontFamily(String font);
		// * 设置FantasyFont字体族，默认”fantasy”
		//webSetting.setFantasyFontFamily(String font);
		// * 设置最小字体，默认8. 取值区间[1-72]，超过范围，使用其上限值。
		//webSetting.setMinimumFontSize(int size);
		// * 设置最小逻辑字体，默认8. 取值区间[1-72]，超过范围，使用其上限值。
		//webSetting.setMinimumLogicalFontSize(int size);
		// * 设置默认字体大小，默认16，取值区间[1-72]，超过范围，使用其上限值。
		//webSetting.setDefaultFontSize(int size);
		// * 设置默认填充字体大小，默认16，取值区间[1-72]，超过范围，使用其上限值。
		//webSetting.setDefaultFixedFontSize(int size);
		// * 设置是否加载图片资源，注意：方法控制所有的资源图片显示，包括嵌入的本地图片资源。
		// * 使用方法setBlockNetworkImage则只限制网络资源图片的显示。值设置为true后，
		// * webview会自动加载网络图片。默认true
		webSetting.setLoadsImagesAutomatically(true);
		// * 是否加载网络图片资源。注意如果getLoadsImagesAutomatically返回false，则该方法没有效果。
		// * 如果使用setBlockNetworkLoads设置为false，该方法设置为false，也不会显示网络图片。
		// * 当值从true改为false时。WebView会自动加载网络图片。
		//webSetting.setBlockNetworkImage(boolean flag);
		// * 设置是否加载网络资源。注意如果值从true切换为false后，WebView不会自动加载，
		// * 除非调用WebView#reload().如果没有android.Manifest.permission#INTERNET权限，
		// * 值设为false，则会抛出java.lang.SecurityException异常。
		// * 默认值：有android.Manifest.permission#INTERNET权限时为false，其他为true。
		//webSetting.setBlockNetworkLoads(boolean flag);
		// * 设置是否允许执行JS。
		webSetting.setJavaScriptEnabled(true);
		// * 是否允许Js访问任何来源的内容。包括访问file scheme的URLs。考虑到安全性，
		// * 限制Js访问范围默认禁用。注意：该方法只影响file scheme类型的资源，其他类型资源如图片类型的，
		// * 不会受到影响。ICE_CREAM_SANDWICH_MR1版本以及以下默认为true，JELLY_BEAN版本
		// * 以上默认为false
		webSetting.setAllowUniversalAccessFromFileURLs(false);
		// * 是否允许Js访问其他file scheme的URLs。包括访问file scheme的资源。考虑到安全性，
		// * 限制Js访问范围默认禁用。注意：该方法只影响file scheme类型的资源，其他类型资源如图片类型的，
		// * 不会受到影响。如果getAllowUniversalAccessFromFileURLs为true，则该方法被忽略。
		// * ICE_CREAM_SANDWICH_MR1版本以及以下默认为true，JELLY_BEAN版本以上默认为false
		webSetting.setAllowFileAccessFromFileURLs(false);
		// * 设置存储定位数据库的位置，考虑到位置权限和持久化Cache缓存，Application需要拥有指定路径的
		// * write权限
		webSetting.setGeolocationDatabasePath(MyApplication.getMyApp().getDir("geolocation", 0)
				.getPath());
		//* 是否允许Cache，默认false。考虑需要存储缓存，应该为缓存指定存储路径setAppCachePath
		webSetting.setAppCacheEnabled(true);
		webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
		// * 设置Cache API缓存路径。为了保证可以访问Cache，Application需要拥有指定路径的write权限。
		// * 该方法应该只调用一次，多次调用自动忽略。
		webSetting.setAppCachePath(MyApplication.getMyApp().getDir("appcache", 0).getPath());
		webSetting.setDatabasePath(MyApplication.getMyApp().getDir("databases", 0).getPath());
		// * 是否允许数据库存储。默认false。查看setDatabasePath API 如何正确设置数据库存储。
		// * 该设置拥有全局特性，同一进程所有WebView实例共用同一配置。注意：保证在同一进程的任一WebView
		// * 加载页面之前修改该属性，因为在这之后设置WebView可能会忽略该配置
		webSetting.setDatabaseEnabled(true);
		// * 是否存储页面DOM结构，默认false。
		webSetting.setDomStorageEnabled(true);
		// * 是否允许定位，默认true。注意：为了保证定位可以使用，要保证以下几点：
		// * Application 需要有android.Manifest.permission#ACCESS_COARSE_LOCATION的权限
		// * Application 需要实现WebChromeClient#onGeolocationPermissionsShowPrompt的回调，
		// * 接收Js定位请求访问地理位置的通知
		webSetting.setGeolocationEnabled(true);
		// * 是否允许JS自动打开窗口。默认false
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		// * 设置页面的编码格式，默认UTF-8
		webSetting.setDefaultTextEncodingName("UTF-8");
		// * 设置WebView代理，默认使用默认值
		//webSetting.setUserAgentString(String ua);
		// * 通知WebView是否需要设置一个节点获取焦点当
		// * WebView#requestFocus(int,android.graphics.Rect)被调用的时候，默认true
		webSetting.setNeedInitialFocus(true);
		// * 基于WebView导航的类型使用缓存：正常页面加载会加载缓存并按需判断内容是否需要重新验证。
		// * 如果是页面返回，页面内容不会重新加载，直接从缓存中恢复。setCacheMode允许客户端根据指定的模式来
		// * 使用缓存。
		// * LOAD_DEFAULT 默认加载方式
		// * LOAD_CACHE_ELSE_NETWORK 按网络情况使用缓存
		// * LOAD_NO_CACHE 不使用缓存
		// * LOAD_CACHE_ONLY 只使用缓存
		webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
		// 设置加载不安全资源的WebView加载行为。KITKAT版本以及以下默认为MIXED_CONTENT_ALWAYS_ALLOW方
		// 式，LOLLIPOP默认MIXED_CONTENT_NEVER_ALLOW。强烈建议：使用MIXED_CONTENT_NEVER_ALLOW
		//webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
		webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
		webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
		 this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
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
		//X5WebView 父类屏蔽了 onScrollChanged 方法 要用该方法
		if (mOnScrollChangedCallback != null) mOnScrollChangedCallback.onScroll(l, t);
	}
	@Override
	protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		//普通webview用这个
		if (mOnScrollChangedCallback != null) mOnScrollChangedCallback.onScroll(l, t);
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
	public OnScrollChangedCallback getOnScrollChangedCallback() {
		return mOnScrollChangedCallback;
	}

	public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback) {
		mOnScrollChangedCallback = onScrollChangedCallback;
	}

	/**
	 * webview 滑动回调
	 */
	public interface OnScrollChangedCallback {
		void onScroll(int l, int t);
	}
}
