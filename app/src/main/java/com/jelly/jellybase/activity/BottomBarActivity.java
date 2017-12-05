package com.jelly.jellybase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.base.applicationUtil.PermissionUtil;
import com.base.bottomBar.BottomBarItem;
import com.base.bottomBar.BottomBarLayout;
import com.base.bottomBar.FragmentAdapter;
import com.base.config.IntentAction;
import com.base.eventBus.NetEvent;
import com.base.httpmvp.presenter.IBasePresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.sqldao.DBHelper;
import com.base.view.BaseFragment;
import com.base.zxing.ScanerCodeActivity;
import com.base.zxing.decoding.ZXingUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.datamodel.CurrentItem;
import com.jelly.jellybase.fragment.HomeFragment;
import com.jelly.jellybase.fragment.MeFragment;
import com.jelly.jellybase.fragment.MiddleFragment;
import com.jelly.jellybase.fragment.OrderFragment;
import com.jelly.jellybase.fragment.WalletFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import systemdb.Login;
import xiaofei.library.hermeseventbus.HermesEventBus;

public class BottomBarActivity extends BaseActivityImpl {
    private final int zxingRequestCode=1;
    private ViewPager mVpContent;
    private BottomBarLayout mBottomBarLayout;
    private FragmentAdapter myAdapter;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private RotateAnimation mRotateAnimation;
    private Handler mHandler = new Handler();

    private Login login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HermesEventBus.getDefault().register(this);
        login= DBHelper.getInstance(getApplicationContext())
                .getLogin();
        setContentView(R.layout.bottombar_activity);
        initView();
        initData();
        initListener();

        //isLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(HermesEventBus.getDefault().isRegistered(this)){
            HermesEventBus.getDefault().unregister(this);
        }
    }

    private void isLogin(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (login!=null) {
                    //Log.i("WF","getDriverId="+login.getDriverId());
                    //Toast.makeText(WelcomeActivity.this,"getDriverId="+login.getDriverid(),Toast.LENGTH_LONG).show();
                    if(login.getId()!=0){
                    }else{
                        goLoginActivity();
                    }
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
    private void initView() {
        mVpContent = (ViewPager) findViewById(R.id.vp_content);
        mBottomBarLayout = (BottomBarLayout) findViewById(R.id.bbl);
    }

    private void initData() {

        HomeFragment homeFragment = new HomeFragment();
        mFragmentList.add(homeFragment);

        OrderFragment orderFragment = new OrderFragment();
        mFragmentList.add(orderFragment);

        MiddleFragment middleFragment = new MiddleFragment();
        mFragmentList.add(middleFragment);

        WalletFragment walletFragment = new WalletFragment();
        mFragmentList.add(walletFragment);

        MeFragment meFragment = new MeFragment();
        mFragmentList.add(meFragment);
    }

    private void initListener() {
        myAdapter= new FragmentAdapter(getSupportFragmentManager(),mFragmentList);
        mVpContent.setAdapter(myAdapter);
        mBottomBarLayout.setViewPager(mVpContent);
        mBottomBarLayout.setOnItemSelectedListener(new BottomBarLayout.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final BottomBarItem bottomBarItem, int position) {
                if (bottomBarItem.getIsHeave()){
                    Intent intent=new Intent(BottomBarActivity.this, ScanerCodeActivity.class);
                    //overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                    startActivityForResult(intent,zxingRequestCode);
                }
            }
        });

//        mBottomBarLayout.setUnread(0,20);//设置第一个页签的未读数为20
//        mBottomBarLayout.setUnread(1,101);//设置第二个页签的未读书
//        mBottomBarLayout.showNotify(2);//设置第三个页签显示提示的小红点
//        mBottomBarLayout.setMsg(3,"NEW");//设置第四个页签显示NEW提示文字
    }

    /**停止首页页签的旋转动画*/
    private void cancelTabLoading(BottomBarItem bottomItem) {
        Animation animation = bottomItem.getImageView().getAnimation();
        if (animation != null){
            animation.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_READ_PHONE_STATE, mPermissionGrant);
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_ACCESS_COARSE_LOCATION, mPermissionGrant);
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_CAMERA, mPermissionGrant);
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_READ_EXTERNAL_STORAGE, mPermissionGrant);
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_WRITE_EXTERNAL_STORAGE, mPermissionGrant);
    }

    @Override
    public IBasePresenter initPresenter() {
        return null;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtil.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }
    private PermissionUtil.PermissionGrant mPermissionGrant = new PermissionUtil.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
            }
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            return;
        }
        //扫描
        if(requestCode==zxingRequestCode && resultCode== ZXingUtils.resultCode){
            //Log.i("ss","data="+data.getStringExtra("result"));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetEvent netEvent){
        if (netEvent.getEventType().equals(CurrentItem.class.getName())){
            CurrentItem currentItem= (CurrentItem) netEvent.getEvent();
            mBottomBarLayout.setCurrentItem(currentItem.getItemIndex());
            BaseFragment baseFragment= (BaseFragment) myAdapter.getItem(currentItem.getItemIndex());
            baseFragment.setData(currentItem.getData());
        }
    }
}