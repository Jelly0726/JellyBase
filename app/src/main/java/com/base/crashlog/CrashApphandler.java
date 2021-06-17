package com.base.crashlog;

import android.text.TextUtils;

import com.base.BaseApplication;
import com.jelly.baselibrary.Utils.FilesUtil;
import com.jelly.baselibrary.config.BaseConfig;
import com.jelly.baselibrary.log.LogUtils;
import com.jelly.jellybase.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**奔溃日志 保存为文件
 * Created by Administrator on 2016/12/21.
 */
public class CrashApphandler extends CrashAppLog{
    public static CrashApphandler mCrashApphandler = null;


    private CrashApphandler(){};
    public static CrashApphandler getInstance() {

        if (mCrashApphandler == null)
            mCrashApphandler = new CrashApphandler();

        return mCrashApphandler;

    }

    @Override
    public void initParams(CrashAppLog crashAppLog) {
        //动态的改变缓存目录和缓存文件数量
        if (crashAppLog != null){

            crashAppLog.setCAHCE_CRASH_LOG(BaseApplication.getInstance().getExternalFilesDir(null) + File.separator+"crashLog");
            crashAppLog.setLIMIT_LOG_COUNT(5);
        }
    }
    private boolean isSendErr() {
        return BuildConfig.SEND_ERR;
    }
    @Override
    public void sendCrashLogToServer(File folder,final File file) {
        //发送服务端
        LogUtils.e("*********", "文件夹:"+folder.getAbsolutePath()+" - "+file.getAbsolutePath()+"");
        if (isSendErr()) {
            String data = FilesUtil.getInstance().read(file).toString();
            if (!TextUtils.isEmpty(data)) {
                JSONObject map=new JSONObject();
                try {
                    map.put("msgtype", "text");
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("content", data);
                    map.put("text", jsonObject1.toString());
                    OkHttpClient httpClient = new OkHttpClient();
                    MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                    RequestBody requestBody = RequestBody.Companion.create(map.toString(),JSON);
                    Request request = new Request.Builder()
                            .url(BaseConfig.sendError_URL)
                            .post(requestBody)
                            .build();
                    try {
                        Response response = httpClient.newCall(request).execute();
                        if (response.isSuccessful()){
                            String ss=response.body().string();
                            LogUtils.i("response="+ss);
                            JSONObject object=new JSONObject(ss);
                            if (object.getInt("errcode")==0){
                                boolean is= FilesUtil.getInstance().deleteDirectory(file);
                                LogUtils.i("is="+is);
                            }
                        }
                    } catch (IOException e) {
                        LogUtils.e("e="+e.getMessage());
                    }
                } catch (JSONException e) {
                    LogUtils.e("e="+e.getMessage());
                }
            }

        }
    }
}
