package com.base.httpmvp.retrofitapi

import androidx.lifecycle.LifecycleOwner
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * 调度器
 */
object NetScheduler {
    fun <T> compose(lifecycleOwner: LifecycleOwner?): ObservableTransformer<T, T> {
        return if (lifecycleOwner!=null)
            ObservableTransformer { observable ->
                observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .bindToLifecycle(lifecycleOwner)//绑定生命周期
                    .debounce(1, TimeUnit.SECONDS)//防止1s内重复请求
            }
        else
            ObservableTransformer { observable ->
                observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .debounce(1, TimeUnit.SECONDS)//防止1s内重复请求
            }
    }
}