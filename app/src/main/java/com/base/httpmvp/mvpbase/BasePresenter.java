package com.base.httpmvp.mvpbase;

import com.google.gson.Gson;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/12/5.
 */
public abstract class BasePresenter<V extends IBaseView>{
    public Gson mGson = new Gson();
    public V mView;//给子类使用view
    public void attachView(V v){
        mView = v;
    }
    public void detachView(){
        mView.closeProgress();
        mView = null;
        unDisposable();
    }
    //以下下为配合RxJava2+retrofit2使用的
    //将所有正在处理的Subscription都添加到CompositeSubscription中。统一退出的时候注销观察
    private CompositeDisposable mCompositeDisposable;

    /**
     * 将Disposable添加
     *
     * @param subscription
     */
    public void addDisposable(Disposable subscription) {
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
        }
    }

}
