package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.base.BaseApplication;
import com.google.gson.Gson;
import com.jelly.baselibrary.BackInterface;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.BaseFragment;
import com.jelly.baselibrary.applicationUtil.AppPrefs;
import com.jelly.baselibrary.config.ConfigKey;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.social.SocialUtil;
import com.jelly.baselibrary.view.NoPreloadViewPager;
import com.jelly.jellybase.BuildConfig;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.UserLoginActivityBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/18.
 */

public class LoginActivity extends BaseActivity<UserLoginActivityBinding> implements
        NoPreloadViewPager.OnPageChangeListener,BackInterface, View.OnClickListener {

    private String phone="";
    private String password;
    private int from=-1;
    private List<BaseFragment> mFragmentList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
        AppPrefs.remove(getApplicationContext(),
                ConfigKey.DEFAULT_BANK);

    }
    @Override
    public void getExtra() {
        super.getExtra();
        phone=getIntent().getStringExtra("phone");
        password=getIntent().getStringExtra("password");
        from=getIntent().getIntExtra("from",-1);
    }

    private void iniView (){
getViewBinding().registerAccount.setOnClickListener(this);
        getViewBinding().vpContent.setOnPageChangeListener(this);
        getViewBinding().topbarRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                for (int i=0;i<getViewBinding().topbarRg.getChildCount();i++){
                    if (getViewBinding().topbarRg.getChildAt(i).getId()==checkedId){
                        getViewBinding().vpContent.setCurrentItem(i, false);
                    }else {
                    }
                }
            }
        });
    }
    private void iniData(){
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
        getViewBinding().leftRb.setChecked(true);
    }

    private void initListener() {
        getViewBinding().vpContent.setAdapter(new MyAdapter(getSupportFragmentManager()));
        if (getViewBinding().vpContent.getAdapter().getCount() != getViewBinding().topbarRg.getChildCount()) {
            throw new IllegalArgumentException("RadioGroup的子RadioButton数量必须和ViewPager条目数量一致");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        iniData();
        initListener();
    }



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
            BaseApplication.getInstance().exit();
        }
    }
    @Override
    protected void onDestroy() {
        SocialUtil.getInstance().socialHelper().clear();
        if(mFragmentList!=null)
            mFragmentList.clear();
        mFragmentList=null;
        super.onDestroy();
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        ((RadioButton)getViewBinding().topbarRg.getChildAt(position)).setChecked(true);
        getViewBinding().vpContent.setCurrentItem(position, false);
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
}
