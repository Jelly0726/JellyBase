package com.base.httpmvp.mvpbase

import androidx.lifecycle.LifecycleOwner
import io.reactivex.ObservableTransformer

/**
 * mvp中的view
 */
interface IBaseView {
    /**
     * 显示操作进度
     */
    fun showProgress()

    /**
     * 关闭进度
     */
    fun closeProgress()

    /**
     * 绑定生命周期
     * @return
    </T> */
    fun bindLifecycle(): LifecycleOwner?
}