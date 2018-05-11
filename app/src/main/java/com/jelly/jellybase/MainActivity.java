package com.jelly.jellybase;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.base.Contacts.ContactsActivity;
import com.base.applicationUtil.MyApplication;
import com.base.applicationUtil.ToastUtils;
import com.base.appservicelive.toolsUtil.CommonStaticUtil;
import com.base.bgabanner.GuideActivity;
import com.base.multiClick.AntiShake;
import com.base.nodeprogress.NodeProgressDemo;
import com.base.permission.PermissionUtils;
import com.base.view.FloatingDraftButton;
import com.base.webview.BaseWebViewActivity;
import com.base.webview.JSWebViewActivity;
import com.base.xrefreshview.XRefreshView;
import com.base.xrefreshview.XRefreshViewFooter;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.view.ItemDecoration;
import com.jelly.jellybase.activity.AMapActivity;
import com.jelly.jellybase.activity.AddShopcartActivity;
import com.jelly.jellybase.activity.AlipayPassWordActivity;
import com.jelly.jellybase.activity.AnswerActivity;
import com.jelly.jellybase.activity.BottomBarActivity;
import com.jelly.jellybase.activity.EvaluateActivity;
import com.jelly.jellybase.activity.GraphValiCodeActivity;
import com.jelly.jellybase.activity.HomeActivity;
import com.jelly.jellybase.activity.LineChartActivity;
import com.jelly.jellybase.activity.PaymentActivity;
import com.jelly.jellybase.activity.PickActivity;
import com.jelly.jellybase.activity.ProductDetailsActivity;
import com.jelly.jellybase.activity.ResolveHtmlActivity;
import com.jelly.jellybase.activity.ScreenShortActivity;
import com.jelly.jellybase.activity.SpinnerActivity;
import com.jelly.jellybase.activity.TreeActivity;
import com.jelly.jellybase.adpater.MainAdapter;
import com.jelly.jellybase.nfc.NFCMainActivity;
import com.jelly.jellybase.shopcar.ShopCartActivity;
import com.jelly.jellybase.swipeRefresh.activity.XSwipeMainActivity;
import com.jelly.jellybase.userInfo.LoginActivity;
import com.jelly.jellybase.userInfo.RegisterActivity;
import com.jelly.jellybase.userInfo.SettingsActivity;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

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
        //百度智能更新 SDK 的 AAR 文件
        BDAutoUpdateSDK.uiUpdateAction(this, new MyUICheckUpdateCallback(),false);
        //开启保活服务
        CommonStaticUtil.startService(MyApplication.getMyApp());

        // 申请权限。
        AndPermission.with(MainActivity.this)
                .requestCode(PermissionUtils.REQUEST_CODE_PERMISSION_MULTI)
                .permission(Permission.MICROPHONE,//扩音器，麦克风
                        Permission.STORAGE,//存储
                        Permission.CALENDAR,//日历
                        Permission.CAMERA,//照相机
                        Permission.CONTACTS,//联系人
                        Permission.LOCATION,//定位
                        //Permission.SENSORS,//传感器，感应器；感测器
                        Permission.SMS,//短信
                        new String[]{
                                android.Manifest.permission.READ_PHONE_STATE,//读取手机状态
                                android.Manifest.permission.CALL_PHONE//拨打电话
                        })
                .callback(this)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale(rationaleListener)
                .start();
    }
    private class MyUICheckUpdateCallback implements UICheckUpdateCallback {
        /**
         * 当检测到无版本更新时会触发回调该方法
         */
        @Override
        public void onNoUpdateFound() {

        }

        /**
         * 当检测到无版本更新或者用户关闭版本更新ᨀ示框
         或者用户点击了升级下载时会触发回调该方法
         */
        @Override
        public void onCheckComplete() {
        }

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
    }
    /**
     *申请权限。 Rationale支持，这里自定义对话框。
     */
    private RationaleListener rationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
            // 这里使用自定义对话框，如果不想自定义，用AndPermission默认对话框：
            //AndPermission.rationaleDialog(Context, Rationale).show();
            // 使用AndPermission提供的默认设置dialog，用户点击确定后会打开App的设置页面让用户授权。
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            AndPermission.rationaleDialog(MainActivity.this, rationale)
                    .setTitle(R.string.permission_title_dialog)
                    .setMessage(R.string.message_permission_failed)
                    .setPositiveButton(R.string.permission_ok)
                    .setNegativeButton(R.string.permission_no, null)
                    .show();
            // 更多自定dialog，请看上面。
            // 建议：自定义这个Dialog，提示具体需要开启什么权限，自定义Dialog具体实现上面有示例代码。
        }
    };

    /**
     * 申请权限。
     * @param grantedPermissions
     */
    @PermissionYes(PermissionUtils.REQUEST_CODE_PERMISSION_MULTI)
    private void getMultiYes(@NonNull List<String> grantedPermissions) {
        Toast.makeText(this, R.string.permission_successfully, Toast.LENGTH_SHORT).show();
    }

    /**
     * 申请权限。
     * @param deniedPermissions
     */
    @PermissionNo(PermissionUtils.REQUEST_CODE_PERMISSION_MULTI)
    private void getMultiNo(@NonNull List<String> deniedPermissions) {
        Toast.makeText(this, R.string.permission_failure, Toast.LENGTH_SHORT).show();
        if(AndPermission.hasPermission(this,deniedPermissions)) {
            // TODO 执行拥有权限时的下一步。
        } else {
            // 使用AndPermission提供的默认设置dialog，用户点击确定后会打开App的设置页面让用户授权。
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
                AndPermission.defaultSettingDialog(this, PermissionUtils.REQUEST_CODE_SETTING)
                        .setTitle(R.string.permission_title_dialog)
                        .setMessage(R.string.message_permission_failed)
                        .setPositiveButton(R.string.permission_ok)
                        .setNegativeButton(R.string.permission_no, null)
                        .show();
                // 更多自定dialog，请看上面。
            }
            // 建议：自定义这个Dialog，提示具体需要开启什么权限，自定义Dialog具体实现上面有示例代码。
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_SETTING: {
                Toast.makeText(this, R.string.message_setting_back, Toast.LENGTH_LONG).show();
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
                    intent=new Intent(MyApplication.getMyApp(), BottomBarActivity.class);
                    startActivity(intent);
                    break;
                case 1://购物车
                    intent=new Intent(MyApplication.getMyApp(), ShopCartActivity.class);
                    startActivity(intent);
                    break;
                case 2://引导页
                    intent=new Intent(MyApplication.getMyApp(), GuideActivity.class);
                    startActivity(intent);
                    break;
                case 3://图片多选
                    intent=new Intent(MyApplication.getMyApp(), AnswerActivity.class);
                    startActivity(intent);
                    break;
                case 4://获取验证码
                    intent=new Intent(MyApplication.getMyApp(), RegisterActivity.class);
                    startActivity(intent);
                    break;
                case 5://WebView
                    intent=new Intent(MyApplication.getMyApp(), BaseWebViewActivity.class);
                    startActivity(intent);
                    break;
                case 6://JS交互WebView
                    intent=new Intent(MyApplication.getMyApp(), JSWebViewActivity.class);
                    startActivity(intent);
                    break;
                case 7://仿支付宝密码输入
                    intent=new Intent(MyApplication.getMyApp(), AlipayPassWordActivity.class);
                    startActivity(intent);
                    break;
                case 8://支付方式选择
                    intent=new Intent(MyApplication.getMyApp(), PaymentActivity.class);
                    startActivity(intent);
                    break;
                case 9://加入购物车
                    intent=new Intent(MyApplication.getMyApp(), AddShopcartActivity.class);
                    startActivity(intent);
                    break;
                case 10://截图并保存图片
                    intent=new Intent(MyApplication.getMyApp(), ScreenShortActivity.class);
                    startActivity(intent);
                    break;
                case 11://快递节点
                    intent=new Intent(MyApplication.getMyApp(), NodeProgressDemo.class);
                    startActivity(intent);
                    break;
                case 12://悬停，搜索，扫描，弹窗
                    intent=new Intent(MyApplication.getMyApp(), HomeActivity.class);
                    startActivity(intent);
                    break;
                case 13://下拉选择
                    intent=new Intent(MyApplication.getMyApp(), SpinnerActivity.class);
                    startActivity(intent);
                    break;
                case 14://地址时间选择
                    intent=new Intent(MyApplication.getMyApp(), PickActivity.class);
                    startActivity(intent);
                    break;
                case 15://Android MVP+Retrofit+RxAndroid
                    intent=new Intent(MyApplication.getMyApp(), LoginActivity.class);
                    startActivity(intent);
                    break;
                case 16://商品详情
                    intent=new Intent(MyApplication.getMyApp(), ProductDetailsActivity.class);
                    intent.putExtra("isShanGou",true);
                    startActivity(intent);
                    break;
                case 17://提交评价
                    intent=new Intent(MyApplication.getMyApp(), EvaluateActivity.class);
                    startActivity(intent);
                    break;
                case 18://版本检查
                    intent=new Intent(MyApplication.getMyApp(), SettingsActivity.class);
                    startActivity(intent);
                    break;
                case 19://解析html
                    intent=new Intent(MyApplication.getMyApp(), ResolveHtmlActivity.class);
                    startActivity(intent);
                    break;
                case 20://Android图表视图/图形视图库
                    intent=new Intent(MyApplication.getMyApp(), LineChartActivity.class);
                    startActivity(intent);
                    break;
                case 21://图形验证码
                    intent=new Intent(MyApplication.getMyApp(), GraphValiCodeActivity.class);
                    startActivity(intent);
                    break;
                case 22://SystemBar一体化，状态栏和导航栏
                    intent=new Intent(MyApplication.getMyApp(), BottomBarActivity.class);
                    startActivity(intent);
                    break;
                case 23://手机通讯录
                    intent=new Intent(MyApplication.getMyApp(), ContactsActivity.class);
                    startActivity(intent);
                    break;
                case 24://多级树形
                    intent=new Intent(MyApplication.getMyApp(), TreeActivity.class);
                    startActivity(intent);
                    break;
                case 25://RecyclerView侧滑菜单
                    intent=new Intent(MyApplication.getMyApp(), XSwipeMainActivity.class);
                    startActivity(intent);
                    break;
                case 26://高德地图
                    intent=new Intent(MyApplication.getMyApp(), AMapActivity.class);
                    intent.putExtra("name","怡富花园二期-东门");
                    intent.putExtra("address","福建省厦门市思明区莲前西路314号");
                    intent.putExtra("latitude",24.4771500111);
                    intent.putExtra("longitude",118.1387329102);
                    startActivity(intent);
                    break;
                case 27://NFC
                    intent=new Intent(MyApplication.getMyApp(), NFCMainActivity.class);
                    startActivity(intent);
                    break;
            }

        }
    };
}
