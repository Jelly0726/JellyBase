package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.allenliu.versionchecklib.core.VersionParams;
import com.allenliu.versionchecklib.core.http.HttpHeaders;
import com.base.applicationUtil.AppUtils;
import com.base.applicationUtil.MyApplication;
import com.base.applicationUtil.ToastUtils;
import com.base.checkVersion.CheckVersionActivity;
import com.base.circledialog.CircleDialog;
import com.base.circledialog.callback.ConfigDialog;
import com.base.circledialog.params.DialogParams;
import com.base.config.IntentAction;
import com.base.httpmvp.mode.databean.AppVersion;
import com.base.httpmvp.presenter.GetAppversionListPresenter;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.httpmvp.view.IGetAppversionListView;
import com.base.mprogressdialog.MProgressUtil;
import com.base.multiClick.AntiShake;
import com.base.view.MyActivity;
import com.jelly.jellybase.R;
import com.maning.mndialoglibrary.MProgressDialog;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.BuildConfig;

/**
 * Created by Administrator on 2017/9/27.
 */

public class SettingsActivity extends MyActivity implements IGetAppversionListView {
    private LinearLayout left_back;
    private LinearLayout change_pwd;
    private LinearLayout change_phone;
    private LinearLayout check_updata;
    private LinearLayout about;
    private TextView exit_tv;

    private GetAppversionListPresenter getAppversionListPresenter;
    private MProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings_activity);
        iniView();
        progressDialog= MProgressUtil.getInstance().getMProgressDialog(this);
        getAppversionListPresenter=new GetAppversionListPresenter(this);
    }

    @Override
    protected void onDestroy() {
        AllenChecker.cancelMission();
        if (progressDialog!=null){
            progressDialog.dismiss();
            progressDialog=null;
        }
        super.onDestroy();
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
                    getAppversionListPresenter.execute(true
                            ,lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
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
                    new CircleDialog.Builder(SettingsActivity.this)
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

                                    MyApplication.getMyApp().finishAllActivity();
                                    Intent intent = new Intent();
                                    //intent.setClass(this, LoginActivity.class);
                                    intent.setAction(IntentAction.ACTION_LOGIN);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .show();

                    break;
            }
        }
    };

    @Override
    public void showProgress() {
        if (progressDialog!=null){
            progressDialog.show();
        }
    }

    @Override
    public void closeProgress() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public Object getAppversionListParam() {
        return null;
    }

    @Override
    public void getAppversionListSuccess(boolean isRefresh, Object mCallBackVo) {
        AppVersion appVersion= (AppVersion) mCallBackVo;

        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.put("token", GlobalToken.getToken().getToken());

        String title="当前版本已是最新版本";
        String message="";
        String downloadUrl="";
        VersionParams.Builder builder = new VersionParams.Builder();
        Bundle paramBundle=new Bundle();
        paramBundle.putBoolean("isUpdate",false);
        if (appVersion!=null){
            if (AppUtils.getVersionCode(MyApplication.getMyApp())<appVersion.getAppversion()){
                paramBundle.putBoolean("isUpdate",true);
                //demoService.showVersionDialog(appVersion.getIP()+appVersion.getUrl(), "检测到新版本","",paramBundle);
                title="检测到新版本";
                message="";
                downloadUrl=appVersion.getIP()+appVersion.getUrl();
            }
        }
        //如果仅使用下载功能，downloadUrl是必须的
        builder.setOnlyDownload(true)
                .setDownloadUrl(downloadUrl)
                .setTitle(title)
                .setUpdateMsg(message)
                .setPauseRequestTime(-1)
                .setCustomDownloadActivityClass(CheckVersionActivity.class)
                .setForceRedownload(false)
                .setParamBundle(paramBundle);
        AllenChecker.init(BuildConfig.DEBUG);
        AllenChecker.startVersionCheck(this, builder.build());
    }

    @Override
    public void getAppversionListFailed(boolean isRefresh, String message) {
        ToastUtils.showToast(this,message);
    }
}
