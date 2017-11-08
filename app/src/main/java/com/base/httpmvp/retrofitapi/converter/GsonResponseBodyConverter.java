package com.base.httpmvp.retrofitapi.converter;

import android.util.Log;

import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpStatus;
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
//GsonResponseBodyConverter.java
final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    //private final Type type;

        GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }
//    GsonResponseBodyConverter(Gson gson, Type type) {
//        this.gson = gson;
//        this.type = type;
//    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        Log.i("ss","response="+response);
        try {
            HttpStatus httpStatus = gson.fromJson(response, HttpStatus.class);
            if (!httpStatus.isReturnState()) {
                if (httpStatus.getMessage().equals(HttpCode.TOKEN_NOT_EXIST)) {
                    throw new TokenNotExistException();
                } else if (httpStatus.getMessage().equals(HttpCode.TOKEN_INVALID)) {
                    throw new TokenInvalidException();
                } else {
                    // 特定 API 的错误，在相应的 Subscriber 的 onError 的方法中进行处理
                    throw new ApiException(httpStatus.getMessage());
                }
            }
//            MediaType contentType = value.contentType();
//            Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
//            InputStream inputStream = new ByteArrayInputStream(response.getBytes());
//            Reader reader = new InputStreamReader(inputStream, charset);
//            JsonReader jsonReader = gson.newJsonReader(reader);
//            return adapter.read(jsonReader);
            //T users = gson.fromJson(response,type);
            T users = adapter.fromJson(response);
            return users;
        } catch (Exception e){
            throw new ApiException(e.getMessage());
        }finally {
            value.close();
        }
    }
}