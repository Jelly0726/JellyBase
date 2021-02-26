package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.base.appManager.AppSubject;
import com.base.config.IntentAction;
import com.base.httpmvp.contact.SettingContact;
import com.base.httpmvp.presenter.SettingPresenter;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.model.AppVersion;
import com.base.multiClick.AntiShake;
import com.base.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.DialogParams;
import com.trello.rxlifecycle3.android.ActivityEvent;

import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/9/27.
 */

public class SettingsActivity extends BaseActivityImpl<SettingContact.View,SettingContact.Presenter> implements SettingContact.View {
    private LinearLayout left_back;
    private LinearLayout change_pwd;
    private LinearLayout change_phone;
    private LinearLayout check_updata;
    private LinearLayout about;
    private TextView exit_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.user_settings_activity;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public SettingContact.Presenter initPresenter() {
        return new SettingPresenter();
    }
    @Override
    public SettingContact.View initIBView() {
        return this;
    }

    @Override
    public <T> ObservableTransformer<T, T> bindLifecycle() {
        return lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY);
    }
    private void iniView(){
        left_back= (LinearLayout) findViewById(R.id.left_back);
        left_back.setOnClickListener(listener);
        change_pwd= (LinearLayout) findViewById(R.id.change_pwd);
        change_pwd.setOnClickListener(listener);
        change_phone= (LinearLayout) findViewById(R.id.change_phone);
        change_phone.setOnClickListener(listener);
        check_updata= (LinearLayout) findViewById(R.id.check_updata);
        check_updata.setOnClickListener(listener);
        about= (LinearLayout) findViewById(R.id.about);
        about.setOnClickListener(listener);
        exit_tv= (TextView) findViewById(R.id.exit_tv);
        exit_tv.setOnClickListener(listener);
    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (AntiShake.check(v.getId())) {    //判断是否多次点击
                return;
            }
            Intent intent;
            switch (v.getId()){
                case R.id.left_back:
                    finish();
                    break;
                case R.id.change_pwd:
//                    intent=new Intent(SettingsActivity.this,ChangePWDActivity.class);
//                    startActivity(intent);
                    break;
                case R.id.change_phone:
//                    intent=new Intent(SettingsActivity.this,ChangePhoneActivity.class);
//                    startActivity(intent);
                    break;
                case R.id.check_updata:
                    presenter.getAppversion();
                    //百度智能更新 SDK 的 AAR 文件
                    //BDAutoUpdateSDK.uiUpdateAction(SettingsActivity.this, new MyUICheckUpdateCallback());
//                    AllenChecker.init(true);
//                    HttpHeaders httpHeaders=new HttpHeaders();
//                    httpHeaders.put("token",GlobalToken.getToken().getToken());
//                    VersionParams.Builder builder = new VersionParams.Builder()
//                            .setRequestUrl(BaseConfig.CHECK_VERSION)
//                            .setCustomDownloadActivityClass(CheckVersionActivity.class)
//                            .setHttpHeaders(httpHeaders)
//                            .setPauseRequestTime(-1)
//                            .setService(DemoService.class);
//                    if (progressDialog!=null){
//                        progressDialog.show();
//                    }
//                    AllenChecker.startVersionCheck(SettingsActivity.this, builder.build());
                    break;
                case R.id.about:
//                    intent=new Intent(SettingsActivity.this,AboutActivity.class);
//                    startActivity(intent);
                    break;
                case R.id.exit_tv:
                    new CircleDialog.Builder()
                            .configDialog(new ConfigDialog() {
                                @Override
                                public void onConfig(DialogParams params) {
                                    params.width=0.6f;
                                }
                            })
                            .setCanceledOnTouchOutside(false)
                            .setCancelable(false)
                            .setTitle("退出")
                            .setText("确定要退出登录？")
                            .setNegative("取消", null)
                            .setPositive("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    GlobalToken.removeToken();

                                    AppSubject.getInstance().detachAll();
                                    Intent intent = new Intent();
                                    //intent.setClass(this, LoginActivity.class);
                                    intent.setAction(IntentAction.ACTION_LOGIN);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .show(getSupportFragmentManager());

                    break;
            }
        }
    };
    private class MyUICheckUpdateCallback implements UICheckUpdateCallback {
        /**
         * 当检测到无版本更新时会触发回调该方法
         */
        @Override
        public void onNoUpdateFound() {
            new CircleDialog.Builder()
                    .configDialog(new ConfigDialog() {
                        @Override
                        public void onConfig(DialogParams params) {
                            params.width=0.6f;
                        }
                    })
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(false)
                    .setTitle("检查更新")
                    .setText("当前版本已是最新版本!")
                    .setNegative("取消", null)
                    .setPositive("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .show(getSupportFragmentManager());
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
    public void getAppversionSuccess( Object mCallBackVo) {
        AppVersion appVersion= (AppVersion) mCallBackVo;
    }

    @Override
    public void getAppversionFailed(String message) {
        ToastUtils.showToast(this,message);
    }
}
