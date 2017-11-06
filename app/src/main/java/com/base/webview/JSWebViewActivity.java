package com.base.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.base.MapUtil.DestinationActivity;
import com.base.MapUtil.PositionEntity;
import com.base.MapUtil.RouteTask;
import com.base.applicationUtil.AppUtils;
import com.base.applicationUtil.MyApplication;
import com.base.applicationUtil.ToastUtils;
import com.base.eventBus.NetEvent;
import com.base.webview.jsbridge.BridgeHandler;
import com.base.webview.jsbridge.BridgeTBSWebView;
import com.base.webview.jsbridge.BridgeTBSWebViewClient;
import com.base.webview.jsbridge.CallBackFunction;
import com.jelly.jellybase.R;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.utils.TbsLog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * Created by Administrator on 2017/2/7.
 */

public class JSWebViewActivity extends Activity {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 99;                                 //定位权限请求参数
    private static final int REQUEST_CODE_ASK_PERMISSIONS_EXTERNAL_STORAGE = 96;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_CAMERA = 97;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_SD = 98;

    private BridgeTBSWebView mWebView;
    private WebTools webTools;
    // private static ProgressDialog progressDialog;
    private PositionEntity entity = new PositionEntity();
    private boolean isFirst = true;
    private static String address = "0", city = "0", adCode = "0";//地址

    private ValueCallback<Uri> mUploadMessage;// 表单的数据信息
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int FILECHOOSER_RESULTCODE = 1;// 表单的结果回调</span>
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_tbs_webview);
        HermesEventBus.getDefault().register(this);
        webTools = (WebTools) getIntent().getParcelableExtra(WebConfig.CONTENT);
        if (webTools == null) {
            webTools = new WebTools();
        }
        //WebView
//        progressDialog=new ProgressDialog(this);
//        //progressDialog.setTitle("加载提示");
//        progressDialog.setMessage("正在加载.....");
//        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
//        progressDialog.setCancelable(true);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        init();
    }

    private void init() {
        mWebView = (BridgeTBSWebView) findViewById(R.id.web_filechooser);
        mWebView.setWebViewClientCallBack(webViewClientCallBack);
        //mWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        long time = System.currentTimeMillis();
        if (webTools != null) {
            if (!TextUtils.isEmpty(webTools.url)) {
                mWebView.loadUrl(webTools.url);
            }
        }
//        if(progressDialog!=null){
//            if(!progressDialog.isShowing()){
//                progressDialog.show();
//            }
//        }
        TbsLog.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        mWebView.registerHandler("reginboxid", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                if (RouteTask.getInstance(getApplicationContext()).getStartPoint() == null) {
                    ToastUtils.showToast(JSWebViewActivity.this, "定位异常！请检查网络与定位权限是否允许！");
                    return;
                }
                if (TextUtils.isEmpty(RouteTask.getInstance(getApplicationContext()).getStartPoint().city)) {
                    ToastUtils.showToast(JSWebViewActivity.this, "定位异常！请检查网络与定位权限是否允许！");
                    return;
                }
                Intent intent = new Intent(JSWebViewActivity.this, DestinationActivity.class);
                startActivity(intent);
                //function.onCallBack("submitFromWeb exe, response data 中文 from Java");
            }

        });
        mWebView.registerHandler("again_postion", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                starLocation();
            }

        });
        mWebView.registerHandler("search_btn", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mWebView.getWindowToken(), 0); //强制隐藏键盘
            }

        });

        mWebView.setOpenFileCallBack(openFileCallBack);


        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
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
                    MyApplication.getMyApp().exit();
                return super.onKeyDown(keyCode, event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyApplication.getMyApp().exit();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        MyApplication.getMyApp().exit();
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
        HermesEventBus.getDefault().unregister(this);
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Map<String, Integer> perms = new HashMap<String, Integer>();
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                // Initial
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }
                if (perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限设置成功", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(this, "权限设置失败", Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(JSWebViewActivity.this)
                            .setMessage("定位权限被限制,app可能不能正常运行,请到设置里开启定位权限")
                            .setPositiveButton("了解", null)
                            .create()
                            .show();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_SD:
                // Initial
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }
                if (perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限设置成功", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(this, "权限设置失败", Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(JSWebViewActivity.this)
                            .setMessage("SD卡读写权限被限制,app可能不能正常运行,请到设置里开启SD卡读写权限")
                            .setPositiveButton("了解", null)
                            .create()
                            .show();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_EXTERNAL_STORAGE:
                // Initial
                perms.put(android.Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }
                if (perms.get(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限设置成功", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(this, "权限设置失败", Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(JSWebViewActivity.this)
                            .setMessage("拨打电话功能被限制,app可能不能正常运行,请到设置里开启相应权限")
                            .setPositiveButton("了解", null)
                            .create()
                            .show();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_CAMERA:
                // Initial
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }
                if (perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限设置成功", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(this, "权限设置失败", Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(JSWebViewActivity.this)
                            .setMessage("摄像头功能被限制,app可能不能正常运行,请到设置里开启相应权限")
                            .setPositiveButton("了解", null)
                            .create()
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private BridgeTBSWebViewClient.TBSWebViewClientCallBack webViewClientCallBack = new
            BridgeTBSWebViewClient.TBSWebViewClientCallBack() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
                    return false;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    // mWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
//                    if(progressDialog!=null){
//                        if(progressDialog.isShowing()){
//                            progressDialog.dismiss();
//                        }
//                    }
                    if (isFirst) {
                        starLocation();
                        isFirst = false;
                    }
                }
            };

    /**
     * 开始定位
     */
    private void starLocation() {
        NetEvent netEvent = new NetEvent();
        netEvent.setEvent(true);
        netEvent.setEventType(Boolean.class.getName());
        HermesEventBus.getDefault().post(netEvent);
    }

    /**
     * 停止定位
     */
    private void stopLocation() {
        NetEvent netEvent = new NetEvent();
        netEvent.setEvent(false);
        netEvent.setEventType(Boolean.class.getName());
        HermesEventBus.getDefault().post(netEvent);
    }

    /**
     * 定位结束回调
     */
    public void onLocationGet(AMapLocation aMapLocation) throws UnsupportedEncodingException, JSONException {
        // todo 这里在网络定位时可以减少一个逆地理编码
        if (aMapLocation.getErrorCode() == 0) {
            if (!aMapLocation.getProvider().equals("gps")) {
                address = aMapLocation.getStreet()
                        + aMapLocation.getPoiName() + aMapLocation.getStreetNum();
                city = aMapLocation.getCity();
                adCode = aMapLocation.getAdCode();
            }
            PositionEntity entity = new PositionEntity();
            entity.latitue = aMapLocation.getLatitude();
            entity.longitude = aMapLocation.getLongitude();
            entity.address = address;
            entity.city = city;
            RouteTask.getInstance(getApplicationContext()).setStartPoint(entity);
            String lo = "javascript:GeoLocation2(\"" + city + "\",\"" + address.replace("null", "")
                    + "\"," + entity.latitue + "," + entity.longitude + ")";
            Log.i("msg", "lo=" + lo);
            mWebView.loadUrl(lo);
        }
    }

    /**
     * 坐标转地址回调
     */
    public void onRegecodeGet(RegeocodeResult regeocodeReult) {
        // mAddressTextView.setText(entity.address);
        try {
            address = regeocodeReult.getRegeocodeAddress().getFormatAddress();
            city = regeocodeReult.getRegeocodeAddress().getCity();
            adCode = regeocodeReult.getRegeocodeAddress().getAdCode();
            entity.address = address;
            entity.city = city;
            RouteTask.getInstance(getApplicationContext()).setStartPoint(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 地址转坐标回调
     */
    public void onGecodeGet(GeocodeResult geocodeReult) {

    }

    /***
     * 订阅 定位回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetEvent netEvent) throws UnsupportedEncodingException, JSONException {
        if (AppUtils.isStar(MyApplication.getMyApp(), JSWebViewActivity.class)) {
            if (netEvent.getEventType().equals(AMapLocation.class.getName())) {
                onLocationGet((AMapLocation) netEvent.getEvent());
            }
            if (netEvent.getEventType().equals(RegeocodeResult.class.getName())) {
                onRegecodeGet((RegeocodeResult) netEvent.getEvent());
            }
        }
        /***
         * 订阅 检索地址
         */
        if (netEvent.getEventType().equals(PositionEntity.class.getName())) {
            if (netEvent.getArg() == 0) {
                PositionEntity entity = (PositionEntity) netEvent.getEvent();
                stopLocation();
                String lo = "javascript:GeoLocation2(\"" + entity.city + "\",\"" + entity.address.replace("null", "")
                        + "\"," + entity.latitue + "," + entity.longitude + ")";
                mWebView.loadUrl(lo);
//            String lol="javascript:takeout(\"jack\","+entity.latitue+","+entity.longitude+")";
//            mWebView.loadUrl(lol);
                Log.i("msg", "lo=" + lo);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {

                if (result != null) {
                    String path = getPath(getApplicationContext(),
                            result);
                    Uri uri = Uri.fromFile(new File(path));
                    mUploadMessage
                            .onReceiveValue(uri);
                } else {
                    //Uri[] results = new Uri[]{imageUri};
                    mUploadMessage.onReceiveValue(imageUri);
                }
                mUploadMessage = null;
            }
        }
    }


    private BridgeTBSWebView.OpenFileCallBack openFileCallBack = new BridgeTBSWebView.OpenFileCallBack() {
        @Override
        public boolean onShowFileChooser(ValueCallback<Uri[]> filePathCallback) {

            mUploadCallbackAboveL = filePathCallback;
            take();
            return true;
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            take();
        }
    };

    @SuppressWarnings("null")
    @TargetApi(Build.VERSION_CODES.BASE)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE
                || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {

            if (data == null) {
                results = new Uri[]{imageUri};

            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
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
        if (results != null) {
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        } else {
            results = new Uri[]{imageUri};
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }

        return;
    }


    private void take() {
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
        // Create the storage directory if it does not exist
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }
        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

        imageUri = Uri.fromFile(file);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent i = new Intent(captureIntent);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(i);

        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        JSWebViewActivity.this.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
