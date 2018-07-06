package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.base.appManager.MyApplication;
import com.base.applicationUtil.AppPrefs;
import com.base.config.ConfigKey;
import com.base.multiClick.AntiShake;
import com.base.permission.PermissionUtils;
import com.base.social.SocialUtil;
import com.base.view.BackInterface;
import com.base.view.BaseActivity;
import com.base.view.BaseFragment;
import com.base.view.NoPreloadViewPager;
import com.google.gson.Gson;
import com.jelly.jellybase.BuildConfig;
import com.jelly.jellybase.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

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
                AndPermission.with(LoginActivity.this)
                        .requestCode(com.base.permission.PermissionUtils.REQUEST_CODE_PERMISSION_MULTI)
                        .permission(
                                Permission.STORAGE,
                                Permission.CALENDAR,
                                Permission.CAMERA,
                                Permission.LOCATION,
                                new String[]{
                                        android.Manifest.permission.READ_PHONE_STATE,
                                        android.Manifest.permission.CALL_PHONE
                                })
                        .callback(this)
                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                        // 这样避免用户勾选不再提示，导致以后无法申请权限。
                        // 你也可以不设置。
                        .rationale(rationaleListener)
                        .start();
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("s'ss","intent="+intent);
        if (SocialUtil.getInstance() != null) {
            SocialUtil.getInstance().socialHelper().onNewIntent(intent);
        }
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
            AndPermission.rationaleDialog(LoginActivity.this, rationale)
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
        //Toast.makeText(this, R.string.permission_successfully, Toast.LENGTH_SHORT).show();
    }

    /**
     * 申请权限。
     * @param deniedPermissions
     */
    @PermissionNo(PermissionUtils.REQUEST_CODE_PERMISSION_MULTI)
    private void getMultiNo(@NonNull List<String> deniedPermissions) {
        //Toast.makeText(this, R.string.permission_failure, Toast.LENGTH_SHORT).show();
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
}
