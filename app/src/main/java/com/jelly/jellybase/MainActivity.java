package com.jelly.jellybase;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.base.applicationUtil.MyApplication;
import com.base.bgabanner.GuideActivity;
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
import com.jelly.jellybase.activity.HomeActivity;
import com.jelly.jellybase.activity.MvpRetrofitRXAndroidActivity;
import com.jelly.jellybase.activity.PaymentActivity;
import com.jelly.jellybase.activity.PickActivity;
import com.jelly.jellybase.activity.ProductDetailsActivity;
import com.jelly.jellybase.activity.ScreenShortActivity;
import com.jelly.jellybase.activity.SettingsActivity;
import com.jelly.jellybase.activity.SpinnerActivity;
import com.jelly.jellybase.adpater.MainAdapter;
import com.jelly.jellybase.login.RegisterActivity;
import com.jelly.jellybase.shopcar.ShopCartActivity;

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
                    intent=new Intent(MyApplication.getMyApp(), MvpRetrofitRXAndroidActivity.class);
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
            }

        }
    };
}
