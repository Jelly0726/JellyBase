package com.jelly.jellybase;

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
import android.widget.TextView;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.base.applicationUtil.MyApplication;
import com.base.appservicelive.toolsUtil.CommonStaticUtil;
import com.base.bgabanner.GuideActivity;
import com.base.config.IntentAction;
import com.base.eventBus.NetEvent;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.multiClick.AntiShake;
import com.base.nodeprogress.NodeProgressDemo;
import com.base.webview.BaseWebViewActivity;
import com.base.webview.JSWebViewActivity;
import com.base.xrefreshview.XRefreshView;
import com.base.xrefreshview.XRefreshViewFooter;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.view.ItemDecoration;
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
import com.jelly.jellybase.adpater.MainAdapter;
import com.jelly.jellybase.server.LocationService;
import com.jelly.jellybase.shopcar.ShopCartActivity;
import com.jelly.jellybase.userInfo.LoginActivity;
import com.jelly.jellybase.userInfo.RegisterActivity;
import com.jelly.jellybase.userInfo.SettingsActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import systemdb.PositionEntity;
import xiaofei.library.hermeseventbus.HermesEventBus;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private XRefreshView xRefreshView;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainAdapter adapter;
    private TextView textView;
    private String[] mList;
    private int startRownumber=0;
    private int pageSize=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        iniXRefreshView();
        //百度智能更新 SDK 的 AAR 文件
        BDAutoUpdateSDK.uiUpdateAction(this, new MyUICheckUpdateCallback());
        //开启保活服务
        CommonStaticUtil.startService(MyApplication.getMyApp());
        HermesEventBus.getDefault().register(this);
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
    protected void onDestroy() {
        HermesEventBus.getDefault().unregister(this);
        MyApplication.getMyApp().removeEvent(this);
        super.onDestroy();
    }

    private void isLogin(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!TextUtils.isEmpty(GlobalToken.getToken().getToken())) {
                }else{
                    goLoginActivity();
                }
            }
        },1000);
    }
    /**
     * 进入登陆界面
     */
    public void goLoginActivity() {
        try{
            Intent intent = new Intent();
            //intent.setClass(this, LoginActivity.class);
            intent.setAction(IntentAction.ACTION_LOGIN);
            startActivity(intent);
            finish();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void iniXRefreshView(){
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetEvent netEvent){
        if (netEvent.getEventType().equals(PositionEntity.class.getName())){
            Intent intent=new Intent(MyApplication.getMyApp(), LocationService.class);
            MyApplication.getMyApp().stopService(intent);
            PositionEntity entity= (PositionEntity) netEvent.getEvent();
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
            }

        }
    };
}
