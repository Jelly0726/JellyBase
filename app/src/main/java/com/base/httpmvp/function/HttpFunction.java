package com.base.httpmvp.function;

import com.base.httpmvp.retrofitapi.HttpResultData;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by Administrator on 2018/1/16.
 */

public class HttpFunction<T> implements Function<HttpResultData<T>, HttpResultData<T>> {

    @Override
    public HttpResultData<T> apply(@NonNull HttpResultData<T> tHttpStateData) throws Exception {
        return tHttpStateData;
    }
}
