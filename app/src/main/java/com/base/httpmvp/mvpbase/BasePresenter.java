package com.base.httpmvp.mvpbase;

import com.google.gson.Gson;

import java.util.Map;
import java.util.TreeMap;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * mvp中的Presenter
 */
public abstract class BasePresenter<V extends IBaseView,E extends BaseModel>{
    public Gson mGson = new Gson();
    public V mView;//给子类使用view
    public E mModel;
    //记录每个Disposable方便移除
    private Map<Integer,Disposable> mDisposable;
    public void attachView(V v){
        mView = v;
        start();
    }
    public void detachView(){
        mView.closeProgress();
        mView = null;
        mModel = null;
        unDisposable();
    }

    /**
     * 初始化
     */
    public abstract void start();

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
            mDisposable=new TreeMap<>();
        }
        mCompositeDisposable.add(subscription);
        mDisposable.put(key,subscription);
    }

    /**
     * 在界面退出等需要解绑观察者的情况下调用此方法统一解绑，防止Rx造成的内存泄漏
     */
    public void unDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
        if (mDisposable!=null){
            mDisposable.clear();
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
        if (mCompositeDisposable != null&&mDisposable!=null) {
            Disposable subscription= mDisposable.get(key);
            subscription.dispose();
            mCompositeDisposable.remove(subscription);
            mDisposable.remove(key);
        }
    }

}
