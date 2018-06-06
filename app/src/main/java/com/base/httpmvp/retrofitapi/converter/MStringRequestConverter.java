package com.base.httpmvp.retrofitapi.converter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit2.Converter;

/**
 * 对请求参数进行处理
 * 这里用于对Field、FieldMap、Header、Path、Query、QueryMap注解的处理
 * @param <T>
 */
final class MStringRequestConverter<T> implements Converter<T, String> {
    @Override
    public String convert(T value) throws IOException {
        String strUTF8=value.toString();
        try {
            strUTF8 = URLEncoder.encode(value.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return strUTF8;
    }
}
