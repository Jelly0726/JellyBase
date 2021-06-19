package com.jelly.baselibrary.moshi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class JsonTool {
    private Moshi moshi;
    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder {
        private static final JsonTool instance = new JsonTool();
    }
    private JsonTool(){
        moshi = new Moshi
                .Builder()
                .addLast(new ColorAdapter())
                .addLast(new KotlinJsonAdapterFactory())
                .build();
    }
    /**
     * 单一实例
     */
    public static JsonTool get() {
        return JsonTool.SingletonHolder.instance;
    }

    /**
     * 要杜绝单例对象在反序列化时重新生成对象，那么必须加入如下方法：
     *
     * @return
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return JsonTool.SingletonHolder.instance;
    }
    /**
     * 转JSON
     * @param obj
     * @param <T>
     * @return
     */
    public  <T> String toJson(T obj){
        Class< T> rawType = (Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        JsonAdapter<T> jsonAdapter = moshi.adapter(rawType);
        String json = jsonAdapter.toJson(obj);
        return json;
    }

    /**
     * 转JSON
     * @param obj
     * @param factory
     * @param <T>
     * @return
     */
    public <T> String toJson(T obj,JsonAdapter.Factory factory){
        Moshi moshi = new Moshi.Builder().addLast(factory).build();
        Class< T> rawType = (Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        JsonAdapter<T> jsonAdapter = moshi.adapter(rawType);
        String json = jsonAdapter.toJson(obj);
        return json;
    }

    /**
     * JSON 转 T
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T fromJson(String json,Class<T> tClass){
        JsonAdapter<T> jsonAdapter = moshi.adapter(tClass);
        T obj = null;
        try {
            obj = jsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }
    /**
     * JSON 转 T
     * @param json
     * @param tClass
     * @param <T>
     * @param factory
     * @return
     */
    public <T> T fromJson(String json,Class<T> tClass,JsonAdapter.Factory factory){
        Moshi moshi = new Moshi.Builder().addLast(factory).build();
        JsonAdapter<T> jsonAdapter = moshi.adapter(tClass);
        T obj = null;
        try {
            obj = jsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }
    /**
     * JSON 转 List<T>
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> List<T> toList(String json,Class<T> tClass){
        Type type = Types.newParameterizedType(List.class,tClass);
        JsonAdapter<List<T>> adapter = moshi.adapter(type);
        List<T> list = null;
        try {
            list = adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * JSON 转 List<T>
     * @param json
     * @param tClass
     * @param <T>
     * @param factory
     * @return
     */
    public <T> List<T> toList(String json,Class<T> tClass,JsonAdapter.Factory factory){
        Moshi moshi = new Moshi.Builder().addLast(factory).build();
        Type type = Types.newParameterizedType(List.class,tClass);
        JsonAdapter<List<T>> adapter = moshi.adapter(type);
        List<T> list = null;
        try {
            list = adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
