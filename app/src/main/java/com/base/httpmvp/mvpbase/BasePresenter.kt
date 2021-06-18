package com.base.httpmvp.mvpbase

import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * mvp中的Presenter
 */
abstract class BasePresenter<V : IBaseView?, E : BaseModel> {
    @JvmField
    var mGson = Gson()
    val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    @JvmField
    var mView //给子类使用view
            : V? = null
    @JvmField
    var mModel //执行请求
            : E? = null

    /**
     * 附加到view上
     *
     * @param v
     */
    fun attachView(v: V) {
        mView = v
        start()
    }

    /**
     * 从view分离
     */
    fun detachView() {
        if (mView != null) {
            mView!!.closeProgress()
            mView = null
        }
        if (mModel != null) {
            mModel!!.unDisposable()
            mModel = null
        }
    }

    /**
     * 初始化
     */
    abstract fun start()
}