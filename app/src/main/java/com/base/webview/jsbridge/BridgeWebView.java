package com.base.webview.jsbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.base.applicationUtil.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("SetJavaScriptEnabled")
public class BridgeWebView extends WebView implements WebViewJavascriptBridge {

    private final String TAG = "BridgeWebView";
    private BridgeWebViewClient bridgeWebViewClient;
    private OpenFileCallBack openFileCallBack;
    public static final String toLoadJs = "WebViewJavascriptBridge.js";
    Map<String, CallBackFunction> responseCallbacks = new HashMap<String, CallBackFunction>();
    Map<String, BridgeHandler> messageHandlers = new HashMap<String, BridgeHandler>();
    BridgeHandler defaultHandler = new DefaultHandler();

    private List<Message> startupMessage = new ArrayList<Message>();

    public List<Message> getStartupMessage() {
        return startupMessage;
    }

    public void setStartupMessage(List<Message> startupMessage) {
        this.startupMessage = startupMessage;
    }

    private long uniqueId = 0;

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public BridgeWebView(Context context) {
        super(context);
        init(context);
    }

    /**
     * @param handler default handler,handle messages send by js without assigned handler name,
     *                if js message has handler name, it will be handled by named handlers registered by native
     */
    public void setDefaultHandler(BridgeHandler handler) {
        this.defaultHandler = handler;
    }

    private void init(Context context) {
        //android:scrollbars="none"   隐藏滚动条
        this.setHorizontalScrollBarEnabled(false);//水平不显示
        this.setVerticalScrollBarEnabled(false); //垂直不显示
        this.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//滚动条在WebView内侧显示
        this.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条在WebView外侧显示
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放
        this.getSettings().setLoadWithOverviewMode(true);
        this.getSettings().setLoadsImagesAutomatically(true);  //支持自动加载图片

        // 应用可以有数据库
        this.getSettings().setDatabaseEnabled(true);
        this.getSettings().setGeolocationEnabled(true);
        this.getSettings().setAppCachePath(context.getApplicationContext().getDir("appcache", 0).getPath());
        this.getSettings().setDatabasePath(context.getApplicationContext().getDir("databases", 0).getPath());
        this.getSettings().setGeolocationDatabasePath(context.getApplicationContext().getDir("geolocation", Context.MODE_PRIVATE)
                .getPath());
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);//设置缓冲大小，我设的是8M
        //this.getSettings().setAppCacheMaxSize(Long.MAX_VALUE);
        this.getSettings().setAllowFileAccess(true);
        this.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.getSettings().setAppCachePath(MyApplication.getMyApp().getDir("appcache", 0).getPath());
        this.getSettings().setDatabasePath(MyApplication.getMyApp().getDir("databases", 0).getPath());
        this.getSettings().setGeolocationDatabasePath(MyApplication.getMyApp().getDir("geolocation", 0)
                .getPath());
        this.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        this.getSettings().setSupportZoom(true);
        this.getSettings().setBuiltInZoomControls(true);
        this.getSettings().setSupportMultipleWindows(true);
        //this.getSettings().setLoadWithOverviewMode(true);
        //this.getSettings().setDatabaseEnabled(true);
        // this.getSettings().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        this.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        this.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        bridgeWebViewClient = generateBridgeWebViewClient(context);
        this.setWebViewClient(bridgeWebViewClient);
        //配置权限
        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                Log.i("msg", "onReceivedIcon");
                super.onReceivedIcon(view, icon);

            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                Log.i("msg", "origin=" + origin);
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);

            }

            @Override
            public void onGeolocationPermissionsHidePrompt() {
                Log.i("msg", "onGeolocationPermissionsHidePrompt=");
                super.onGeolocationPermissionsHidePrompt();
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                Log.i("msg", "request=" + request);
                super.onPermissionRequest(request);
            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest request) {
                Log.i("msg", "onPermissionRequestCanceled=" + request);
                super.onPermissionRequestCanceled(request);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (openFileCallBack != null) {
                    return openFileCallBack.onShowFileChooser(filePathCallback);
                }
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }

            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                if (openFileCallBack != null) {
                    openFileCallBack.openFileChooser(uploadFile, acceptType);
                }
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                if (openFileCallBack != null) {
                    openFileCallBack.openFileChooser(uploadMsg, "");
                }
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                if (openFileCallBack != null) {
                    openFileCallBack.openFileChooser(uploadMsg, acceptType);
                }
            }
            @Override
            public void onReceivedTitle(WebView arg0, final String arg1) {
                super.onReceivedTitle(arg0, arg1);
                Log.i("yuanhaizhou", "webpage title is " + arg1);
                if (arg1.contains("404")){
                    showErrorPage();
                }

            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.i("ss", "BridgeTBSWebViewClient onProgressChanged:----------->" + newProgress);
                if (newProgress == 100) {
                    //loadingLayout.setVisibility(View.GONE);
                    if (bridgeWebViewClient != null) {
                        bridgeWebViewClient.progressDialogDismiss();
                    }
                }
            }
        });
    }
    /**
     * 设置视图是否可见
     * @param visible
     */
    public void setVisible(boolean visible) {
        if (bridgeWebViewClient != null) {
            bridgeWebViewClient.setVisible(visible);
        }
    }
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
        if (bridgeWebViewClient != null) {
            bridgeWebViewClient.progressDialogDismiss();
        }
        bridgeWebViewClient=null;
        super.onDetachedFromWindow();
    }
    protected BridgeWebViewClient generateBridgeWebViewClient(Context context) {
        return new BridgeWebViewClient(this, context);
    }

    public void setWebViewClientCallBack(BridgeWebViewClient.
                                                 WebViewClientCallBack webViewClientCallBack) {
        if (bridgeWebViewClient != null) {
            bridgeWebViewClient.setWebViewClientCallBack(webViewClientCallBack);
        }
        ;
    }

    void handlerReturnData(String url) {
        String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
        CallBackFunction f = responseCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
            return;
        }
    }

    @Override
    public void send(String data) {
        send(data, null);
    }

    @Override
    public void send(String data, CallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
        Message m = new Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
            responseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    private void queueMessage(Message m) {
        if (startupMessage != null) {
            startupMessage.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    void dispatchMessage(Message m) {
        String messageJson = m.toJson();
        //escape special characters for json string
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
        String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.loadUrl(javascriptCommand);
        }
    }

    void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    //Log.i("msg","data="+data);
                    // deserializeMessage
                    List<Message> list = null;
                    try {
                        list = Message.toArrayList(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Message m = list.get(i);
                        String responseId = m.getResponseId();
                        // 是否是response
                        if (!TextUtils.isEmpty(responseId)) {
                            CallBackFunction function = responseCallbacks.get(responseId);
                            String responseData = m.getResponseData();
                            function.onCallBack(responseData);
                            responseCallbacks.remove(responseId);
                        } else {
                            CallBackFunction responseFunction = null;
                            // if had callbackId
                            final String callbackId = m.getCallbackId();
                            if (!TextUtils.isEmpty(callbackId)) {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        Message responseMsg = new Message();
                                        responseMsg.setResponseId(callbackId);
                                        responseMsg.setResponseData(data);
                                        queueMessage(responseMsg);
                                    }
                                };
                            } else {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        // do nothing
                                    }
                                };
                            }
                            BridgeHandler handler;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = messageHandlers.get(m.getHandlerName());
                            } else {
                                handler = defaultHandler;
                            }
                            if (handler != null) {
                                handler.handler(m.getData(), responseFunction);
                            }
                        }
                    }
                }
            });
        }
    }
    /**
     * 显示自定义错误提示页面，用一个View覆盖在WebView
     */
    public void showErrorPage() {
        loadUrl("file:///android_asset/webpage/404.html");
    }
    public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
        this.loadUrl(jsUrl);
        responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
    }

    /**
     * register handler,so that javascript can call it
     *
     * @param handlerName
     * @param handler
     */
    public void registerHandler(String handlerName, BridgeHandler handler) {
        if (handler != null) {
            messageHandlers.put(handlerName, handler);
        }
    }

    /**
     * call javascript registered handler
     *
     * @param handlerName
     * @param data
     * @param callBack
     */
    public void callHandler(String handlerName, String data, CallBackFunction callBack) {
        doSend(handlerName, data, callBack);
    }

    public void setOpenFileCallBack(OpenFileCallBack openFileCallBack) {
        this.openFileCallBack = openFileCallBack;
    }

    /**
     * 文件选择回调
     */
    public interface OpenFileCallBack {
        public boolean onShowFileChooser(ValueCallback<Uri[]> arg1);

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType);
    }
}
