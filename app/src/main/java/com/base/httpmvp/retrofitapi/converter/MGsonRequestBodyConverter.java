package com.base.httpmvp.retrofitapi.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * Created by Administrator on 2017/11/7.
 */
//MGsonRequestBodyConverter.java
final class MGsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
//    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private final MediaType MEDIA_TYPE;
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final Type type;
    MGsonRequestBodyConverter(Type type,MediaType MEDIA_TYPE) {
        this.type=type;
        this.MEDIA_TYPE=MEDIA_TYPE;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
//        Buffer buffer = new Buffer();
//        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
//        JsonWriter jsonWriter = gson.newJsonWriter(writer);
//        adapter.write(jsonWriter, value);
//        jsonWriter.close();
//        return RequestBody.create(MEDIA_TYPE, buffer.readUtf8());
//        return RequestBody.create(MEDIA_TYPE, adapter.toJson(value));
        return RequestBody.create(value.toString(),MEDIA_TYPE);
    }
}