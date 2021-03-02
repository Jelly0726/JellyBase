package com.base.httpmvp.mvpbase;

import io.reactivex.ObservableTransformer;

/**
 * mvp中的view
 */

public interface IBaseView {

    /**
     * 显示操作进度
     */
    public void showProgress();

    /**
     * 关闭进度
     */
    public void closeProgress();
    /**
     * 绑定生命周期
     * @param <T>
     * @return
     */
    <T> ObservableTransformer<T,T> bindLifecycle();
}
