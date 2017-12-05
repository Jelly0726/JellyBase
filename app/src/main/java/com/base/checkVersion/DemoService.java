package com.base.checkVersion;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.allenliu.versionchecklib.core.AVersionService;
import com.base.applicationUtil.AppUtils;
import com.base.applicationUtil.MyApplication;
import com.base.httpmvp.mode.AppVersion;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.retrofitapi.HttpResultData;
import com.base.httpmvp.retrofitapi.HttpStateData;
import com.base.httpmvp.retrofitapi.HttpStateJson;
import com.base.httpmvp.retrofitapi.exception.ApiException;
import com.base.httpmvp.retrofitapi.exception.TokenInvalidException;
import com.base.httpmvp.retrofitapi.exception.TokenNotExistException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DemoService extends AVersionService {
    public DemoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("DemoService", "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e("DemoService", "onUnbind");
        super.onDestroy();
    }

    @Override
    public void onResponses(AVersionService service, String response) {
        Log.e("DemoService", response);
        try {
            Gson gson=new Gson();
            if (TextUtils.isEmpty(response)){
                throw new ApiException("服务器返回数据异常!");
            }
            HttpStateJson HttpState = gson.fromJson(response, HttpStateJson.class);
            if (!HttpState.isReturnState()) {
                // 特定 API 的错误，在相应的 Subscriber 的 onError 的方法中进行处理
                throw new ApiException(HttpState.getMessage());
            }
            Log.i("DemoService","getJson="+HttpState.getJson());
            HttpResult httpResult = gson.fromJson(gson.toJson(HttpState.getJson()), HttpResult.class);
            if (httpResult.getStatus()== HttpCode.TOKEN_NOT_EXIST) {
                throw new TokenNotExistException();
            } else if (httpResult.getStatus()== HttpCode.TOKEN_INVALID) {
                throw new TokenInvalidException();
            }

            HttpStateData<HttpResultData<AppVersion>> httpStateData=
                    gson.fromJson(response
                            ,new TypeToken<HttpStateData<HttpResultData<AppVersion>>>(){}.getType());
            AppVersion appVersion=httpStateData.getData().getData();
            Bundle paramBundle=new Bundle();
            if (appVersion!=null){
                if (AppUtils.getVersionCode(MyApplication.getMyApp())<appVersion.getAppversion()){
                    paramBundle.putBoolean("isUpdate",true);
                    showVersionDialog(appVersion.getIP()+appVersion.getUrl(), "检测到新版本","",paramBundle);
                }else {
                    paramBundle.putBoolean("isUpdate",false);
                    showVersionDialog("", "当前版本已是最新版本!","",paramBundle);
                }
            }else {
                paramBundle.putBoolean("isUpdate",false);
                showVersionDialog("", "当前版本已是最新版本!","",paramBundle);
            }
        } catch (Exception e){
            Log.i("DemoService","e="+e);
        }
        //可以在判断版本之后在设置是否强制更新或者VersionParams
        //eg
        // versionParams.isForceUpdate=true;
//        showVersionDialog("http://down1.uc.cn/down2/zxl107821.uc/miaokun1/UCBrowser_" +
//                "V11.5.8.945_android_pf145_bi800_(Build170627172528).apk", "检测到新版本", getString(R.string.updatecontent));
//        or
//        showVersionDialog("http://www.apk3.com/uploads/soft/guiguangbao/UCllq.apk", "检测到新版本", getString(R.string.updatecontent),bundle);
    }
}
