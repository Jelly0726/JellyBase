package com.base.httpmvp.retrofitapi.converter;

import com.jelly.baselibrary.moshi.ColorAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;

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
    private Moshi moshi;
    private MGsonConverterFactory() {
        moshi = new Moshi
                .Builder()
                .addLast(new ColorAdapter())
                .addLast(new KotlinJsonAdapterFactory())
                .build();
    }
    public static MGsonConverterFactory create() {
        return new MGsonConverterFactory();
    }
    // 这里创建从ResponseBody其它类型的Converter，如果不能处理返回null
    // 主要用于对响应体的处理
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
//                return HttpState.class;
//            }
//        };
        //TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(newType));
//        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        JsonAdapter<?> adapter = moshi.adapter(type);
        return new MGsonResponseBodyConverter<>(moshi,adapter);
    }
    // 在这里创建 从自定类型到ResponseBody 的Converter,不能处理就返回null，
    // 主要用于对Part、PartMap、Body注解的处理
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        JsonAdapter<?> adapter = moshi.adapter(type);
        return new MGsonRequestBodyConverter<>(moshi, adapter);
    }
    // 这里用于对Field、FieldMap、Header、Path、Query、QueryMap注解的处理
    // Retrfofit对于上面的几个注解默认使用的是调用toString方法
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations,
                                                Retrofit retrofit) {
        for(int i=0;i<annotations.length;i++){
            if (annotations[0].annotationType().equals(retrofit2.http.Field.class)){
                String value=((retrofit2.http.Field)annotations[0]).value();
                if (value.equals("data")){
                    return new MStringRequestConverter<>();
                }
            }
            return null;
        }
        return null;
    }
}
