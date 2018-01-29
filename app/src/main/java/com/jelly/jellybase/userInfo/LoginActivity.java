package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.base.applicationUtil.AppPrefs;
import com.base.applicationUtil.MyApplication;
import com.base.config.ConfigKey;
import com.base.multiClick.AntiShake;
import com.base.social.SocialUtil;
import com.base.view.BackInterface;
import com.base.view.BaseActivity;
import com.base.view.BaseFragment;
import com.base.view.NoPreloadViewPager;
import com.google.gson.Gson;
import com.jelly.jellybase.BuildConfig;
import com.jelly.jellybase.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/9/18.
 */

public class LoginActivity extends BaseActivity implements
        NoPreloadViewPager.OnPageChangeListener,BackInterface {

    @BindView(R.id.register_account)
    TextView register_account;
    private String phone="";
    private String password;
    private int from=-1;
    @BindView(R.id.topbar_rg)
    RadioGroup topbar_rg;
    @BindView(R.id.left_rb)
    RadioButton left_rb;
    @BindView(R.id.vp_content)
    NoPreloadViewPager mVpContent;
    private List<BaseFragment> mFragmentList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_activity);
        ButterKnife.bind(this);
        phone=getIntent().getStringExtra("phone");
        password=getIntent().getStringExtra("password");
        from=getIntent().getIntExtra("from",-1);
        iniView();

        AppPrefs.remove(getApplicationContext(),
                ConfigKey.DEFAULT_BANK);
    }
    private void iniView (){

        mVpContent.setOnPageChangeListener(this);
        topbar_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                for (int i=0;i<topbar_rg.getChildCount();i++){
                    if (topbar_rg.getChildAt(i).getId()==checkedId){
                        mVpContent.setCurrentItem(i, false);
                    }else {
                    }
                }
            }
        });
    }
    private void iniData(){
        Log.i("ss","phone="+phone);
        Log.i("ss","password="+password);
        Log.i("ss","from="+from);
        Map map=new HashMap();
        map.put("phone",phone);
        map.put("password",password);
        map.put("from",from);

        mFragmentList.clear();

        UserPwLoginFragment boutiquesStoreFragment = new UserPwLoginFragment();
        boutiquesStoreFragment.setData(new Gson().toJson(map));
        mFragmentList.add(boutiquesStoreFragment);

        UserDyLoginFragment allianceFragment = new UserDyLoginFragment();
        mFragmentList.add(allianceFragment);
        left_rb.setChecked(true);
    }

    private void initListener() {
        mVpContent.setAdapter(new MyAdapter(getSupportFragmentManager()));
        if (mVpContent.getAdapter().getCount() != topbar_rg.getChildCount()) {
            throw new IllegalArgumentException("RadioGroup的子RadioButton数量必须和ViewPager条目数量一致");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        iniData();
        initListener();
    }



    @OnClick({R.id.register_account})
    public void onClick(View v) {
        if (AntiShake.check(v.getId())) {    //判断是否多次点击
            return;
        }
        Intent intent;
        switch (v.getId()){
            case R.id.register_account:
                intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(BuildConfig.IS_MUST_LOGIN){//是否必须登录
            MyApplication.getMyApp().exit();
        }
    }
    @Override
    protected void onDestroy() {
        SocialUtil.getInstance().socialHelper().clear();
        super.onDestroy();
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        ((RadioButton)topbar_rg.getChildAt(position)).setChecked(true);
        mVpContent.setCurrentItem(position, false);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private BaseFragment mBaseFragment;
    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        this.mBaseFragment = selectedFragment;
    }
    class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("s'ss","intent  ss="+data);
        if (data != null && SocialUtil.getInstance() != null) {//qq分享如果选择留在qq，通过home键退出，再进入app则不会有回调
            SocialUtil.getInstance().socialHelper().onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("s'ss","intent="+intent);
        if (SocialUtil.getInstance() != null) {
            SocialUtil.getInstance().socialHelper().onNewIntent(intent);
        }
    }
}
