package com.base.httpmvp.retrofitapi.converter;

import android.text.TextUtils;
import android.util.Log;

import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.methods.HttpState;
import com.base.httpmvp.retrofitapi.exception.ApiException;
import com.base.httpmvp.retrofitapi.exception.TokenInvalidException;
import com.base.httpmvp.retrofitapi.exception.TokenNotExistException;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by Administrator on 2017/11/7.
 */
//MGsonResponseBodyConverter.java
final class MGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    //private final Type type;

    MGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }
//    MGsonResponseBodyConverter(Gson gson, Type type) {
//        this.gson = gson;
//        this.type = type;
//    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        Log.i("ss","response="+response);
        if (TextUtils.isEmpty(response)){
            throw new ApiException("服务器返回数据异常!");
        }
        HttpState httpState = gson.fromJson(response, HttpState.class);
        if (!httpState.isReturnState()) {
            if (httpState.getMessage().trim().equals(HttpCode.TOKEN_NOT_EXIST+"")) {
                throw new TokenNotExistException();
            } else if (httpState.getMessage().trim().equals(HttpCode.TOKEN_INVALID+"")) {
                throw new TokenInvalidException();
            } else {
                // 特定 API 的错误，在相应的 Subscriber 的 onError 的方法中进行处理
                throw new ApiException(httpState.getMessage());
            }
        }
        try {
//            MediaType contentType = value.contentType();
//            Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
//            InputStream inputStream = new ByteArrayInputStream(response.getBytes());
//            Reader reader = new InputStreamReader(inputStream, charset);
//            JsonReader jsonReader = gson.newJsonReader(reader);
//            return adapter.read(jsonReader);
            //json转换为list
            //List<SearchResul> list=gson.fromJson(json, new TypeToken<List<SearchResul>>(){}.getType());
            //T users = gson.fromJson(response,type);
            T users = adapter.fromJson(response);
            return users;
        } catch (Exception e){
            Log.i("ss","convert e="+e);
            throw new ApiException(e);
        }finally {
            value.close();
        }
    }
}