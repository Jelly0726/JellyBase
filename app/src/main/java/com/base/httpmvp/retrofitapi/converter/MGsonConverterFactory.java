package com.base.httpmvp.retrofitapi.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/11/7.
 */
//MGsonConverterFactory.java
public class MGsonConverterFactory extends Converter.Factory {

    private final Gson gson;
    private MGsonConverterFactory(Gson gson) {
        this.gson = gson;
    }
    public static MGsonConverterFactory create() {
        return new MGsonConverterFactory(new Gson());
    }
    public static MGsonConverterFactory create(Gson gson) {
        return new MGsonConverterFactory(gson);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(final Type type, Annotation[] annotations, Retrofit retrofit) {

//        Type newType = new ParameterizedType() {
//            @Override
//            public Type[] getActualTypeArguments() {
//                return new Type[] { type };
//            }
//
//            @Override
//            public Type getOwnerType() {
//                return null;
//            }
//
//            @Override
//            public Type getRawType() {
//                return HttpStatus.class;
//            }
//        };
        //TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(newType));
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new MGsonResponseBodyConverter<>(gson,adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new MGsonRequestBodyConverter<>(gson, adapter);
    }
}
