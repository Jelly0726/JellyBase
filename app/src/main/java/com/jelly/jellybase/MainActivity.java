package com.jelly.jellybase;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.allenliu.versionchecklib.callback.APKDownloadListener;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.CustomDownloadingDialogListener;
import com.allenliu.versionchecklib.v2.callback.CustomVersionDialogListener;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;
import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.baidu.autoupdatesdk.CPUpdateDownloadCallback;
import com.base.Contacts.ContactsActivity;
import com.base.appManager.BaseApplication;
import com.base.applicationUtil.AppUtils;
import com.base.bgabanner.GuideActivity;
import com.base.checkVersion.BaseDialog;
import com.base.config.BaseConfig;
import com.base.daemon.DaemonEnv;
import com.base.mic.MicService;
import com.base.multiClick.AntiShake;
import com.base.nodeprogress.NodeProgressDemo;
import com.base.permission.CallBack;
import com.base.permission.PermissionUtils;
import com.base.redpacket.StartActivity;
import com.base.toast.ToastUtils;
import com.base.view.FloatingDraftButton;
import com.base.webview.BaseWebViewActivity;
import com.base.webview.JSWebViewActivity;
import com.base.webview.WebConfig;
import com.base.webview.WebTools;
import com.base.xrefreshview.XRefreshView;
import com.base.xrefreshview.XRefreshViewFooter;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.view.ItemDecoration;
import com.jelly.jellybase.activity.AMapActivity;
import com.jelly.jellybase.activity.AddShopcartActivity;
import com.jelly.jellybase.activity.AlipayPassWordActivity;
import com.jelly.jellybase.activity.AnswerActivity;
import com.jelly.jellybase.activity.AutoTextAvtivity;
import com.jelly.jellybase.activity.BottomBarActivity;
import com.jelly.jellybase.activity.CancelOrderActivity;
import com.jelly.jellybase.activity.CopyActivity;
import com.jelly.jellybase.activity.CrashActivity;
import com.jelly.jellybase.activity.EvaluateActivity;
import com.jelly.jellybase.activity.GSYVideoActivity;
import com.jelly.jellybase.activity.GraphValiCodeActivity;
import com.jelly.jellybase.activity.HomeActivity;
import com.jelly.jellybase.activity.IDCartActivity;
import com.jelly.jellybase.activity.LineChartActivity;
import com.jelly.jellybase.activity.MessageActivity;
import com.jelly.jellybase.activity.PaymentActivity;
import com.jelly.jellybase.activity.PickActivity;
import com.jelly.jellybase.activity.ProductDetailsActivity;
import com.jelly.jellybase.activity.ResolveHtmlActivity;
import com.jelly.jellybase.activity.ScreenShortActivity;
import com.jelly.jellybase.activity.SendEmailActivity;
import com.jelly.jellybase.activity.SingMD5Activity;
import com.jelly.jellybase.activity.SpinnerActivity;
import com.jelly.jellybase.activity.TreeActivity;
import com.jelly.jellybase.activity.UniqueIDActivity;
import com.jelly.jellybase.adpater.MainAdapter;
import com.jelly.jellybase.bdocr.OCRMainActivity;
import com.jelly.jellybase.blebluetooth.BluetoothBLEActivity;
import com.jelly.jellybase.bluetooth.BluetoothActivity;
import com.jelly.jellybase.encrypt.EncryptActivity;
import com.jelly.jellybase.nfc.NFCMainActivity;
import com.jelly.jellybase.server.TraceServiceImpl;
import com.jelly.jellybase.shopcar.ShopCartActivity;
import com.jelly.jellybase.swipeRefresh.activity.XSwipeMainActivity;
import com.jelly.jellybase.userInfo.LoginActivity;
import com.jelly.jellybase.userInfo.RegisterActivity;
import com.jelly.jellybase.userInfo.SettingsActivity;
import com.tencent.tmselfupdatesdk.ITMSelfUpdateListener;
import com.tencent.tmselfupdatesdk.TMSelfUpdateManager;
import com.tencent.tmselfupdatesdk.model.TMSelfUpdateUpdateInfo;
import com.yanzhenjie.permission.Permission;

import java.io.File;

import hugo.weaving.DebugLog;

@DebugLog
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private XRefreshView xRefreshView;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainAdapter adapter;
    private TextView textView;
    private FloatingDraftButton floatingDraftButton;
    private String[] mList;
    private int startRownumber=0;
    private int pageSize=10;
    private long clickTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        iniXRefreshView();
        //启动或停止守护服务，运行在:watch子进程中
        TraceServiceImpl.sShouldStopService = true;//true  表示停止服务，false  表示启动服务
        DaemonEnv.startServiceMayBind(TraceServiceImpl.class);

       startService(new Intent(this, MicService.class));
        //初始化省流量更新SDK，传入的Context必须为ApplicationContext
        TMSelfUpdateManager.getInstance().init(getApplicationContext(), BaseConfig.SELF_UPDATE_CHANNEL, mSelfUpdateListener,
                null, null);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 申请权限。
                PermissionUtils.getInstance().requestPermission(MainActivity.this, new CallBack() {
                            @Override
                            public void onSuceess() {
//                                LogReport.getInstance().upload(MainActivity.this);//启动压缩崩溃信息并发送
                            }
                        },
                        Permission.Group.MICROPHONE,//扩音器，麦克风
                        Permission.Group.STORAGE,//存储
                        Permission.Group.CALENDAR,//日历
                        Permission.Group.CAMERA,//照相机
                        Permission.Group.CONTACTS,//联系人
                        Permission.Group.LOCATION,//定位
                        //Permission.SENSORS,//传感器，感应器；感测器
                        Permission.Group.SMS,//短信
                        new String[]{
                                android.Manifest.permission.READ_PHONE_STATE,//读取手机状态
                                android.Manifest.permission.CALL_PHONE//拨打电话
                        });
                //百度智能更新 SDK 的 AAR 文件
                //此接口用于查询当前服务端是否有新版本， 有的话取回新版本信息。 cpUpdateDownload  下载
//                BDAutoUpdateSDK.cpUpdateCheck(MainActivity.this,new MyCheckUpdateCallback(),false);
                //检查更新
                TMSelfUpdateManager.getInstance().checkSelfUpdate();

            }
        });
    }
    //此接口用于查询当前服务端是否有新版本， 有的话取回新版本信息。
    private class MyCheckUpdateCallback implements CPCheckUpdateCallback {
        private boolean isDownload=false;//是否点击下载
        @Override
        public void onCheckUpdateCallback(final AppUpdateInfo appUpdateInfo, AppUpdateInfoForInstall appUpdateInfoForInstall) {

            if (appUpdateInfo!=null){
                isDownload=false;
               final BaseDialog baseDialog = new BaseDialog(MainActivity.this, R.style.BaseDialog
                        , R.layout.checkversion_dialog_layout);
                TextView textView = baseDialog.findViewById(R.id.tv_msg);
                textView.setText(appUpdateInfo.getAppChangeLog().replaceAll("<br>","\n"));
                Button commit=baseDialog.findViewById(R.id.versionchecklib_version_dialog_commit);
                commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isDownload=true;
                        baseDialog.dismiss();
                        BDAutoUpdateSDK.cpUpdateDownload(MainActivity.this, appUpdateInfo, cpUpdateDownloadCallback);
                    }
                });
                baseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(!isDownload){
                            if (appUpdateInfo.getAppVersionName().contains("F")){
                                baseDialog.show();
                            }
                        }
                    }
                });
                baseDialog.setCanceledOnTouchOutside(true);
                baseDialog.show();

            }else if (appUpdateInfoForInstall!=null){
                BDAutoUpdateSDK.cpUpdateInstall(MainActivity.this,appUpdateInfoForInstall.getInstallPath());
            }else {

            }


        }
    }
    //下载回调
    private CPUpdateDownloadCallback cpUpdateDownloadCallback=new CPUpdateDownloadCallback(){
        /**
         * 当 cpUpdateDownload 被调用时会
         触发回调该方法
         */
        private BaseDialog baseDialog;
        private String apkPath;
        @Override
        public void onStart() {
            baseDialog = new BaseDialog(MainActivity.this, R.style.BaseDialog,
                    R.layout.checkversion_download_layout);
            baseDialog.setCanceledOnTouchOutside(false);
            baseDialog.setCancelable(false);
            baseDialog.show();
        }

        /**
         * 下载进度通过该方法通知应用
         * @param Percent： 进度百分比
         @param rcvLen：已下载文件大
         小
         @param fileSize:文件总大小
         下载进度通过该方法通知应用
         */
        @Override
        public void onPercent(int Percent, long rcvLen, long fileSize) {
            if (baseDialog!=null) {
                TextView tvProgress = baseDialog.findViewById(R.id.tv_progress);
                ProgressBar progressBar = baseDialog.findViewById(R.id.pb);
                progressBar.setProgress(Percent);
                tvProgress.setText(getString(R.string.versionchecklib_progress, Percent));
            }
        }

        /**
         * 下载完成后本地的 APK 包路径回调
         接口
         * @param apkPath：下载完成后的apk 包路径
         */
        @Override
        public void onDownloadComplete(String apkPath) {
           this.apkPath=apkPath;
        }

        /**
         * 下载失败或者发送错误时回调此接
         口
         * @param Error： 异常信息
         @param Content： 异常提示
         *
         */
        @Override
        public void onFail(Throwable Error, String Content) {
            if (baseDialog!=null) {
                baseDialog.dismiss();
            }
            ToastUtils.showShort(MainActivity.this,Content);
        }

        /**
         * 下载流程结束后统一调此接口
         */
        @Override
        public void onStop() {
            if (baseDialog!=null) {
                baseDialog.dismiss();
            }
            if (!TextUtils.isEmpty(apkPath))
            AppUtils.installApk(MainActivity.this,new File(apkPath));
        }
    };
    /**
     * 自更新任务监听，包括检查更新回调和下载状态回调
     */
    private ITMSelfUpdateListener mSelfUpdateListener = new ITMSelfUpdateListener() {
        /**
         * 更新时的下载状态回调
         * @param state 状态码
         * @param errorCode 错误码
         * @param errorMsg 错误信息
         */
        private BaseDialog baseDialog;
        private boolean isDownload=false;//是否点击下载
        @Override
        public void onDownloadAppStateChanged(int state, int errorCode, String errorMsg) {
            //TODO 更新包下载状态变化的处理逻辑
//            baseDialog = new BaseDialog(MainActivity.this, R.style.BaseDialog,
//                    R.layout.checkversion_download_layout);
//            baseDialog.setCanceledOnTouchOutside(false);
//            baseDialog.setCancelable(false);
//            baseDialog.show();
//
//            baseDialog.dismiss();
//            ToastUtils.showShort(MainActivity.this,Content);
//
//            baseDialog.dismiss();
        }

        /**
         * 点击普通更新时的下载进度回调
         * @param receiveDataLen 已经接收的数据长度
         * @param totalDataLen 全部需要接收的数据长度（如果无法获取目标文件的总长度，此参数返回-1）
         */
        @Override
        public void onDownloadAppProgressChanged(final long receiveDataLen, final long totalDataLen) {
            //TODO 更新包下载进度发生变化的处理逻辑
            int Percent= (int) (receiveDataLen/totalDataLen);
//            TextView tvProgress = baseDialog.findViewById(R.id.tv_progress);
//            ProgressBar progressBar = baseDialog.findViewById(R.id.pb);
//            progressBar.setProgress(Percent);
//            tvProgress.setText(getString(R.string.versionchecklib_progress, Percent));
        }

        /**
         * 检查更新返回更新信息回调
         * @param tmSelfUpdateUpdateInfo 更新信息结构体
         */
        @Override
        public void onUpdateInfoReceived(final TMSelfUpdateUpdateInfo tmSelfUpdateUpdateInfo) {
            //TODO 收到更新信息的处理逻辑
            if (null != tmSelfUpdateUpdateInfo) {
                int state = tmSelfUpdateUpdateInfo.getStatus();
                if (state == TMSelfUpdateUpdateInfo.STATUS_CHECKUPDATE_FAILURE) {
                    ToastUtils.showShort(MainActivity.this, "检查更新失败");
                    return;
                }
                switch (tmSelfUpdateUpdateInfo.getUpdateMethod()) {
                    case TMSelfUpdateUpdateInfo.UpdateMethod_NoUpdate:
                        //无更新
//                        ToastUtils.showShort(SettingsActivity.this, "已是最新版本(" + tmSelfUpdateUpdateInfo.versionname+")");
                        break;
                    case TMSelfUpdateUpdateInfo.UpdateMethod_Normal:
                        isDownload=false;
                        //普通更新
                        UIData uiData = UIData.create();
                        uiData.setTitle("检测到新版本");
                        uiData.setDownloadUrl(tmSelfUpdateUpdateInfo.getUpdateDownloadUrl());
                        uiData.setContent(tmSelfUpdateUpdateInfo.getNewFeature().replaceAll("<br>","\n"));
                        final DownloadBuilder builder= AllenVersionChecker
                                .getInstance()
                                .downloadOnly(uiData);
                        builder.setForceRedownload(true); //下载忽略本地缓存 默认false
                        //自定义显示更新界面
                        builder.setCustomVersionDialogListener(new CustomVersionDialogListener(){
                            @Override
                            public Dialog getCustomVersionDialog(Context context, UIData versionBundle) {
                                BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.checkversion_dialog_layout);
                                TextView textView = baseDialog.findViewById(R.id.tv_msg);
                                if (versionBundle!=null)
                                textView.setText(versionBundle.getContent());
                                baseDialog.setCanceledOnTouchOutside(true);
                                return baseDialog;
                            }
                        });
                        //自定义下载中对话框界面
                        builder.setCustomDownloadingDialogListener(new CustomDownloadingDialogListener() {
                            @Override
                            public Dialog getCustomDownloadingDialog(Context context, int progress, UIData versionBundle) {
                                BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.checkversion_download_layout);
                                baseDialog.setCanceledOnTouchOutside(false);
                                baseDialog.setCancelable(false);
                                return baseDialog;
                            }
                            //下载中会不断回调updateUI方法
                            @Override
                            public void updateUI(Dialog dialog, int progress, UIData versionBundle) {
                                isDownload=true;
                                TextView tvProgress = dialog.findViewById(R.id.tv_progress);
                                ProgressBar progressBar = dialog.findViewById(R.id.pb);
                                progressBar.setProgress(progress);
                                tvProgress.setText(getString(R.string.versionchecklib_progress, progress));
                            }
                        });
                        //设置此listener即代表需要强制更新，会在用户想要取消下载的时候回调 需要你自己关闭所有界面
                        builder.setForceUpdateListener(new ForceUpdateListener() {
                            @Override
                            public void onShouldForceUpdate() {
                                if (!isDownload) {
                                    if (tmSelfUpdateUpdateInfo.versionname.contains("F")) {
                                        builder.excuteMission(MainActivity.this);
                                    }
                                }
                            }
                        });
                        //builder.setDownloadAPKPath(address);//默认：/storage/emulated/0/AllenVersionPath/
                        //下载监听
                        builder.setApkDownloadListener(new APKDownloadListener() {
                            @Override
                            public void onDownloading(int progress) {

                            }

                            @Override
                            public void onDownloadSuccess(File file) {

                            }

                            @Override
                            public void onDownloadFail() {

                            }
                        });
                        builder.excuteMission(MainActivity.this);
                        break;
                    case TMSelfUpdateUpdateInfo.UpdateMethod_ByPatch:
                        //省流量更新
                        break;
                    default:
                        break;
                }

            }
        }
    };

    @Override
    protected void onDestroy() {
        //释放
        TMSelfUpdateManager.getInstance().destroy();
        if (cpUpdateDownloadCallback!=null)
            cpUpdateDownloadCallback.onStop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis()-clickTime)>5000){
            clickTime=System.currentTimeMillis();
            ToastUtils.showShort(this,"再按一次，返回桌面");
            return;
        }
        super.onBackPressed();

    }
    private void iniXRefreshView(){
        floatingDraftButton=findViewById(R.id.floatingActionButton);
        floatingDraftButton.setScreenEdge(true);
        floatingDraftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShort(MainActivity.this,"自定义悬浮按钮");
            }
        });
        mList= getResources().getStringArray(R.array.mainArray);
        adapter=new MainAdapter(this,mList);
        xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);
        xRefreshView.setPullLoadEnable(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_test_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Rect rect=new Rect();
        rect.bottom=1;
        rect.top=0;
        rect.left=0;
        rect.right=0;
        recyclerView.addItemDecoration(new ItemDecoration(rect,1, ItemDecoration.NONE));
        // 静默加载模式不能设置footerview
        recyclerView.setAdapter(adapter);
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        adapter.setOnItemClickListener(onItemClickListener);

        xRefreshView.setXRefreshViewListener(simpleXRefreshListener);
    }
    /**
     * 滑动刷新
     */
    private XRefreshView.SimpleXRefreshListener simpleXRefreshListener =new XRefreshView.SimpleXRefreshListener() {

        @Override
        public void onRefresh(boolean isPullDown) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    xRefreshView.stopRefresh();
                }
            }, 2000);
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //xRefreshView.setLoadComplete(true);
                    // 刷新完成必须调用此方法停止加载
                    xRefreshView.stopLoadMore();
                }
            }, 1000);
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
//        if (!NotifyManger.notificationListenerEnable()){
//            ToastUtils.showShort(this, "通知权限未开启！");
//            NotifyManger.gotoNotificationAccessSetting(BaseApplication.getInstance());
//        }else {
//            //重新触发通知绑定
//            NotifyManger.toggleNotificationListenerService();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_SETTING: {
                Toast.makeText(this, R.string.message_setting_comeback, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
    private OnItemClickListener onItemClickListener=new OnItemClickListener(){

        @Override
        public void onItemClick(View view, int position) {
            if (AntiShake.check(position)) {    //判断是否多次点击
                return;
            }
            Intent intent;
            switch (position){
                case 0://轻量底部导航栏
                    intent=new Intent(BaseApplication.getInstance(), BottomBarActivity.class);
                    startActivity(intent);
                    break;
                case 1://购物车
                    intent=new Intent(BaseApplication.getInstance(), ShopCartActivity.class);
                    startActivity(intent);
                    break;
                case 2://引导页
                    intent=new Intent(BaseApplication.getInstance(), GuideActivity.class);
                    startActivity(intent);
                    break;
                case 3://图片多选
                    intent=new Intent(BaseApplication.getInstance(), AnswerActivity.class);
                    startActivity(intent);
                    break;
                case 4://获取验证码
                    intent=new Intent(BaseApplication.getInstance(), RegisterActivity.class);
                    startActivity(intent);
                    break;
                case 5://WebView
                    intent=new Intent(BaseApplication.getInstance(), BaseWebViewActivity.class);
                    startActivity(intent);
                    break;
                case 6://JS交互WebView
                    intent=new Intent(BaseApplication.getInstance(), JSWebViewActivity.class);
                    startActivity(intent);
                    break;
                case 7://仿支付宝密码输入
                    intent=new Intent(BaseApplication.getInstance(), AlipayPassWordActivity.class);
                    startActivity(intent);
                    break;
                case 8://支付方式选择
                    intent=new Intent(BaseApplication.getInstance(), PaymentActivity.class);
                    startActivity(intent);
                    break;
                case 9://加入购物车
                    intent=new Intent(BaseApplication.getInstance(), AddShopcartActivity.class);
                    startActivity(intent);
                    break;
                case 10://截图并保存图片
                    intent=new Intent(BaseApplication.getInstance(), ScreenShortActivity.class);
                    startActivity(intent);
                    break;
                case 11://快递节点
                    intent=new Intent(BaseApplication.getInstance(), NodeProgressDemo.class);
                    startActivity(intent);
                    break;
                case 12://悬停，搜索，扫描，弹窗
                    intent=new Intent(BaseApplication.getInstance(), HomeActivity.class);
                    startActivity(intent);
                    break;
                case 13://下拉选择
                    intent=new Intent(BaseApplication.getInstance(), SpinnerActivity.class);
                    startActivity(intent);
                    break;
                case 14://地址时间选择
                    intent=new Intent(BaseApplication.getInstance(), PickActivity.class);
                    startActivity(intent);
                    break;
                case 15://Android MVP+Retrofit+RxAndroid
                    intent=new Intent(BaseApplication.getInstance(), LoginActivity.class);
                    startActivity(intent);
                    break;
                case 16://商品详情
                    intent=new Intent(BaseApplication.getInstance(), ProductDetailsActivity.class);
                    intent.putExtra("isShanGou",true);
                    startActivity(intent);
                    break;
                case 17://提交评价
                    intent=new Intent(BaseApplication.getInstance(), EvaluateActivity.class);
                    startActivity(intent);
                    break;
                case 18://版本检查
                    intent=new Intent(BaseApplication.getInstance(), SettingsActivity.class);
                    startActivity(intent);
                    break;
                case 19://解析html
                    intent=new Intent(BaseApplication.getInstance(), ResolveHtmlActivity.class);
                    startActivity(intent);
                    break;
                case 20://Android图表视图/图形视图库
                    intent=new Intent(BaseApplication.getInstance(), LineChartActivity.class);
                    startActivity(intent);
                    break;
                case 21://图形验证码
                    intent=new Intent(BaseApplication.getInstance(), GraphValiCodeActivity.class);
                    startActivity(intent);
                    break;
                case 22://SystemBar一体化，状态栏和导航栏
                    intent=new Intent(BaseApplication.getInstance(), BottomBarActivity.class);
                    startActivity(intent);
                    break;
                case 23://手机通讯录
                    intent=new Intent(BaseApplication.getInstance(), ContactsActivity.class);
                    startActivity(intent);
                    break;
                case 24://多级树形
                    intent=new Intent(BaseApplication.getInstance(), TreeActivity.class);
                    startActivity(intent);
                    break;
                case 25://RecyclerView侧滑菜单
                    intent=new Intent(BaseApplication.getInstance(), XSwipeMainActivity.class);
                    startActivity(intent);
                    break;
                case 26://高德地图
                    intent=new Intent(BaseApplication.getInstance(), AMapActivity.class);
                    intent.putExtra("name","怡富花园二期-东门");
                    intent.putExtra("address","福建省厦门市思明区莲前西路314号");
                    intent.putExtra("latitude",24.4771500111);
                    intent.putExtra("longitude",118.1387329102);
                    startActivity(intent);
                    break;
                case 27://NFC
                    intent=new Intent(BaseApplication.getInstance(), NFCMainActivity.class);
                    startActivity(intent);
                    break;
                case 28://消息通知
                    intent=new Intent(BaseApplication.getInstance(), MessageActivity.class);
                    startActivity(intent);
                    break;
                case 29://获取安卓设备唯一编码
                    intent=new Intent(BaseApplication.getInstance(), UniqueIDActivity.class);
                    startActivity(intent);
                    break;
                case 30://视频播放器gsyVideoPlayer
                    intent=new Intent(BaseApplication.getInstance(), GSYVideoActivity.class);
                    startActivity(intent);
                    break;
                case 31://百度文字识别（身份证等）
                    intent=new Intent(BaseApplication.getInstance(), OCRMainActivity.class);
                    startActivity(intent);
                    break;
                case 32://AutoComleteTextView自动补齐
                    intent=new Intent(BaseApplication.getInstance(), AutoTextAvtivity.class);
                    startActivity(intent);
                    break;
                case 33://低功率蓝牙
                    intent=new Intent(BaseApplication.getInstance(), BluetoothBLEActivity.class);
                    startActivity(intent);
                    break;
                case 34://传统蓝牙
                    intent=new Intent(BaseApplication.getInstance(), BluetoothActivity.class);
                    startActivity(intent);
                    break;
                case 35://取消订单
                    intent=new Intent(BaseApplication.getInstance(), CancelOrderActivity.class);
                    startActivity(intent);
                    break;
                case 36://身份证校验
                    intent=new Intent(BaseApplication.getInstance(), IDCartActivity.class);
                    startActivity(intent);
                    break;
                case 37://复制、粘贴
                    intent=new Intent(BaseApplication.getInstance(), CopyActivity.class);
                    startActivity(intent);
                    break;
                case 38://自动抢红包
                    intent=new Intent(BaseApplication.getInstance(), StartActivity.class);
                    startActivity(intent);
                    break;
                case 39://webView下载文件
                    intent=new Intent(BaseApplication.getInstance(), JSWebViewActivity.class);
                    WebTools webTools=new WebTools();
                    webTools.title="文件下载";
                    webTools.url="https://www.easyicon.net/1521-blue_box_info_information_icon.html";
                    intent.putExtra(WebConfig.CONTENT, webTools);
                    startActivity(intent);
                    break;
                case 40://崩溃日志
                    intent=new Intent(BaseApplication.getInstance(), CrashActivity.class);
                    startActivity(intent);
                    break;
                case 41://获取应用签名MD5值
                    intent=new Intent(BaseApplication.getInstance(), SingMD5Activity.class);
                    startActivity(intent);
                    break;
                case 42://NDK(.so)测试
                    intent=new Intent(BaseApplication.getInstance(), SingMD5Activity.class);
                    startActivity(intent);
                    break;
                case 43://    <item>native加密解密</item>
                    intent=new Intent(BaseApplication.getInstance(), EncryptActivity.class);
                    startActivity(intent);
                    break;
                case 44:// 发送邮件
                    intent=new Intent(BaseApplication.getInstance(), SendEmailActivity.class);
                    startActivity(intent);
                    break;
            }

        }
    };
}
