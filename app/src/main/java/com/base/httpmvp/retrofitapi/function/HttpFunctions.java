package com.base.httpmvp.retrofitapi.function;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by Administrator on 2018/1/16.
 */

public class HttpFunctions<T> implements Function<T, Observable<T>> {

    @Override
    public Observable<T> apply(@NonNull T tHttpStateData) throws Exception {
        return Observable.just(tHttpStateData);
    }
}
