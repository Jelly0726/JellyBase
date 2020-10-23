package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.base.appManager.BaseApplication;
import com.base.applicationUtil.AppPrefs;
import com.base.config.ConfigKey;
import com.base.multiClick.AntiShake;
import com.base.permission.CallBack;
import com.base.permission.PermissionUtils;
import com.base.social.SocialUtil;
import com.base.view.BackInterface;
import com.base.view.BaseActivity;
import com.base.view.BaseFragment;
import com.base.view.NoPreloadViewPager;
import com.google.gson.Gson;
import com.jelly.jellybase.BuildConfig;
import com.jelly.jellybase.R;
import com.yanzhenjie.permission.runtime.Permission;

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
        iniView();
        AppPrefs.remove(getApplicationContext(),
                ConfigKey.DEFAULT_BANK);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 申请权限。
                PermissionUtils.getInstance().requestPermission(LoginActivity.this, new CallBack() {
                            @Override
                            public void onSucess() {

                            }

                            @Override
                            public void onFailure(List<String> permissions) {

                            }
                        },
                        Permission.Group.STORAGE,
                        Permission.Group.CALENDAR,
                        Permission.Group.CAMERA,
                        //Permission.Group.LOCATION, //部分手机设置为仅使用时返回授权失败所以在开启定位前再请求授权
                        new String[]{
                                Permission.READ_PHONE_STATE,
                                Permission.CALL_PHONE
                        });
            }
        });
    }

    @Override
    public void getExtra() {
        super.getExtra();
        phone=getIntent().getStringExtra("phone");
        password=getIntent().getStringExtra("password");
        from=getIntent().getIntExtra("from",-1);
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
            BaseApplication.getInstance().exit();
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
}
