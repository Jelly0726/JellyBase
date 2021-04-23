package com.base.httpmvp.mvpbase;

import com.google.gson.Gson;

/**
 * mvp中的Presenter
 */
public abstract class BasePresenter<V extends IBaseView, E extends BaseModel> {
    public Gson mGson = new Gson();
    public V mView;//给子类使用view
    public E mModel;//执行请求

    /**
     * 附加到view上
     *
     * @param v
     */
    public void attachView(V v) {
        mView = v;
        start();
    }

    /**
     * 从view分离
     */
    public void detachView() {
        if (mView!=null) {
            mView.closeProgress();
            mView = null;
        }
        if (mModel != null) {
            mModel.unDisposable();
            mModel = null;
        }
    }

    /**
     * 初始化
     */
    public abstract void start();
}
