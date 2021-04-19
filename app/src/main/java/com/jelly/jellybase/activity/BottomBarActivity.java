package com.jelly.jellybase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;

import com.base.BackInterface;
import com.base.BaseActivity;
import com.base.BaseApplication;
import com.base.BaseFragment;
import com.base.FragmentAdapter;
import com.base.HermesManager;
import com.base.config.IntentAction;
import com.base.sqldao.LoginDaoUtils;
import com.chaychan.library.BottomBarItem;
import com.chaychan.library.BottomBarLayout;
import com.chaychan.library.NoPreloadViewPager;
import com.jelly.baselibrary.addressmodel.Address;
import com.jelly.baselibrary.eventBus.NetEvent;
import com.jelly.jellybase.R;
import com.jelly.jellybase.datamodel.CurrentItem;
import com.jelly.jellybase.fragment.HomeFragment;
import com.jelly.jellybase.fragment.MeFragment;
import com.jelly.jellybase.fragment.MiddleFragment;
import com.jelly.jellybase.fragment.OrderFragment;
import com.jelly.jellybase.fragment.WalletFragment;
import com.jelly.jellybase.server.LocationService;
import com.zxingx.library.activity.CodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import systemdb.Login;
import systemdb.PositionEntity;

public class BottomBarActivity extends BaseActivity implements BackInterface {
    private static final int areaRresultCode=0;
    private final int zxingRequestCode=1;
    private NoPreloadViewPager mVpContent;
    private BottomBarLayout mBottomBarLayout;
    private FragmentAdapter myAdapter;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private Login login;
    private PositionEntity entity;
    private Address address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        login= LoginDaoUtils.getInstance(getApplicationContext())
                .getItem();
        initView();
        initData();
        initListener();
        HermesManager.getHermesManager().addEvent(this);
        //开启定位服务
        Intent stateGuardService =  new Intent(BaseApplication.getInstance(), LocationService.class);
        startService(stateGuardService);
        //isLogin();
    }
    @Override
    public int getLayoutId(){
        return R.layout.bottombar_activity;
    }
    @Override
    protected void onDestroy() {
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        HermesManager.getHermesManager().removeEvent(this);
        if (mFragmentList != null)
            mFragmentList.clear();
        mFragmentList = null;
        super.onDestroy();
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
        mVpContent = (NoPreloadViewPager) findViewById(R.id.vp_content);
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
            public void onItemSelected(final BottomBarItem bottomBarItem, int position, int currentPosition) {
            }
        });

//        mBottomBarLayout.setUnread(0,20);//设置第一个页签的未读数为20
//        mBottomBarLayout.setUnread(1,101);//设置第二个页签的未读书
//        mBottomBarLayout.showNotify(2);//设置第三个页签显示提示的小红点
//        mBottomBarLayout.setMsg(3,"NEW");//设置第四个页签显示NEW提示文字
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            return;
        }
        //扫描
        if(requestCode==zxingRequestCode && resultCode== CodeUtils.RESULT_SUCCESS){
            //Log.i("ss","data="+data.getStringExtra("result"));
        }
        if(requestCode==areaRresultCode && resultCode== areaRresultCode){
            address=data.getParcelableExtra("address");
            if (address!=null){
                mBottomBarLayout.setText(3,address.getDistrict().getAreaName());//设置第三个页签显示的文字
            }else if (entity!=null) {
                mBottomBarLayout.setText(3,entity.district);//设置第三个页签显示的文字
            }
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
        if (netEvent.getEventType().equals(PositionEntity.class.getName())){
            Intent intent=new Intent(BaseApplication.getInstance(), LocationService.class);
            BaseApplication.getInstance().stopService(intent);
            PositionEntity entity= (PositionEntity) netEvent.getEvent();
            if (entity.latitue!=0d&&entity.longitude!=0d) {
                this.entity=entity;
                if (address!=null){
                    mBottomBarLayout.setText(3,address.getDistrict().getAreaName());//设置第三个页签显示的文字
                }else if (entity!=null) {
                    mBottomBarLayout.setText(3,entity.district);//设置第三个页签显示的文字
                }
                //停止定位服务
                Intent stateGuardService =  new Intent(BaseApplication.getInstance(), LocationService.class);
                stopService(stateGuardService);
            }
        }
    }
    private BaseFragment mBaseFragment;
    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        this.mBaseFragment = selectedFragment;
    }
    @Override
    public void onBackPressed() {
        BaseFragment mBaseFragment= (BaseFragment) mFragmentList.get(mBottomBarLayout.getCurrentItem());
        if(mBaseFragment == null || !mBaseFragment.onBackPressed()){
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                super.onBackPressed();
            }else{
                getSupportFragmentManager().popBackStack();
            }
        }
    }
    //go back
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            // Check if the key event was the Back button and if there's history
            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                BaseFragment mBaseFragment= (BaseFragment) mFragmentList.get(mBottomBarLayout.getCurrentItem());
                if(mBaseFragment == null || !mBaseFragment.onBackPressed()){
                    if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                        super.onBackPressed();
                    }else{
                        getSupportFragmentManager().popBackStack();
                    }
                    return super.onKeyDown(keyCode, event);

                } else  return true;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onKeyDown(keyCode, event);
    }
}