package com.base.httpmvp.mvpbase;

import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
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
                removeDisposable(this.hashCode());
            }

            @Override
            public void onComplete() {
                removeDisposable(this.hashCode());
            }

            @Override
            public void onSubscribe(@NonNull Disposable disposable) {
                addDisposable(this.hashCode(),disposable);
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
    /**
     *  以下下为配合RxJava2+retrofit2使用的
     *  将所有正在处理的Subscription都添加到CompositeSubscription中。统一退出的时候注销观察
     *  一般来讲使用了RxLifecycle 就不需要再手动通过CompositeDisposable 注销观察
     *  特殊情况下需要时再调用addDisposable和unDisposable管理
     */
    private CompositeDisposable mCompositeDisposable;

    /**
     * 将Disposable添加
     *
     * @param subscription
     */
    public void addDisposable(int key,Disposable subscription) {
        //csb 如果解绑了的话添加 sb 需要新的实例否则绑定时无效的
        if (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(subscription);
    }

    /**
     * 在界面退出等需要解绑观察者的情况下调用此方法统一解绑，防止Rx造成的内存泄漏
     */
    public void unDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }
    /**
     * 完成后移除订阅
     */
    public void removeDisposable(Disposable subscription) {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.remove(subscription);
        }
    }
    /**
     * 完成后移除订阅
     */
    public void removeDisposable(int key) {
    }
}
