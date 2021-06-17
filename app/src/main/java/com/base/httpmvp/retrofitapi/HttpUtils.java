package com.base.httpmvp.retrofitapi;

import android.text.TextUtils;
import android.util.Log;

import com.base.httpmvp.retrofitapi.exception.ApiException;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by Administrator on 2018/1/17.
 */

public class HttpUtils {
    //保证该类不能被实例化
    private HttpUtils() {

    }
    /**
     * 判断字符串是否为json数据
     * @param jsonStr
     * @return
     */
    public static boolean isJson(String jsonStr){
        if (TextUtils.isEmpty(jsonStr))return false;
        Object json = null;
        try {
            json = new JSONTokener(jsonStr).nextValue();
        } catch (JSONException e) {
            Log.i("SSS","isJsonArray e="+e);
            return false;
        }
        if (json==null)return false;
        if(json instanceof JSONArray
                ||json instanceof JSONObject){
            return true;
        }
        return false;
    }
    /**
     * 判断json数据是否为Array
     * @param jsonStr
     * @return
     */
    public static boolean isJsonArray(String jsonStr){
        if (!isJson(jsonStr))throw new NullPointerException("jsonStr == null");
        Object json = null;
        try {
            json = new JSONTokener(jsonStr).nextValue();
        } catch (JSONException e) {
            Log.i("SSS","isJsonArray e="+e);
            return false;
        }
        if(json instanceof JSONArray){
            Log.i("SSS","JSONArray");
            return true;
        }
        return false;
    }

    /**
     * 校验服务器返回码
     * @param response
     */
    public static void checkResponse(Response response) throws IOException {
        ResponseBody body=response.body();
        BufferedSource source = body.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.getBuffer();
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType =body.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        String string = buffer.clone().readString(charset);
        if (HttpUtils.isJson(string)) {
            HttpResult httpResult = new Gson().fromJson(string, HttpResult.class);
            if (httpResult.getStatus()!= HttpCode.SUCCEED
                    &&httpResult.getStatus()!= HttpCode.SUCCEED_CODE
                    &&httpResult.getStatus()!=HttpCode.TOKEN_INVALID
                    &&httpResult.getStatus()!=HttpCode.TOKEN_NOT_EXIST
                    &&TextUtils.isEmpty(httpResult.getMsg())) {
                throw new ApiException("HttpUtils:校验服务器返回码="+httpResult.getStatus()+",Message="+httpResult.getMsg());
            }
        }
    }
}
