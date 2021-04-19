package com.jelly.baselibrary.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2018/4/8.
 * Gson工具类
 */

public class GsonUtils {
    /**
     * Google Gson
     * @param jsonInString
     * @return
     */
    public final static boolean isJSONValid3(String jsonInString) {
        try {
            new Gson().fromJson(jsonInString, Object.class);
            return true;
        } catch(JsonSyntaxException ex) {
            return false;
        }
    }
    /**
     * 是否是合法的Gson字符串
     * @param targetStr
     * @return
     */
    private static boolean isGoodGson(String targetStr,Class clazz) {
        if(StringUtils.isBlank(targetStr)){
            return false;
        }
        try {
            new Gson().fromJson(targetStr,clazz);
            return true;
        } catch(JsonSyntaxException ex) {
            return false;
        }
    }

    /**
     * 是否是合法的JsonArray (alibaba 认为前1种不是JSON串)
     * 例如：[{a:b}]  [{'a':'b'}]  [{"a":"b"}]
     * @param targetStr
     * @return
     */
    public static boolean isJsonArray(String targetStr){
        return isGoodGson(targetStr,JsonArray.class);
    }

    /**
     * 是否是合法的JsonObject(alibaba 认为前1种不是JSON串)
     * 例如：{a:b} {'a':'b'} {"a":"b"}
     * @param targetStr
     * @return
     */
    public static boolean isJsonObject(String targetStr){
        return isGoodGson(targetStr,JsonObject.class);
    }
}
