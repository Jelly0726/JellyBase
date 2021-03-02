package com.base.httpmvp.mvpbase;

import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * mvp中的model
 * @param <T>
 */
public class BaseModel<T extends HttpResult> {
    /**
     * 封装线程管理和订阅的过程
     * @param observable  被订阅者
     * @param transformer
     * @param listener    接口回调
     */
    public void subscribe(final Observable observable,
                          ObservableTransformer<T,T> transformer, ObserverResponseListener<T> listener) {
        final Observer<T> observer = new Observer<T>() {

            @Override
            public void onError(Throwable e) {

                if (listener!=null) {
                    listener.onFailure(e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                if (listener!=null) {
                    listener.onDisposable(d);
                }
            }

            @Override
            public void onNext(T model) {
                if (model.getStatus()== HttpCode.SUCCEED){
                    if (listener!=null) {
                        listener.onSuccess(model);
                    }
                }else {
                    if (listener!=null) {
                        listener.onFailure(model.getMsg());
                    }
                }
            }
        };
        HttpMethods.getInstance().toSubscribe(observable,observer, transformer);
    }
}
