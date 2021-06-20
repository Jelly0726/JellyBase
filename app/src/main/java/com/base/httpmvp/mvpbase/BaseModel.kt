package com.base.httpmvp.mvpbase

import androidx.lifecycle.LifecycleOwner
import com.base.httpmvp.retrofitapi.HttpCode
import com.base.httpmvp.retrofitapi.HttpMethods.Companion.instance
import com.base.httpmvp.retrofitapi.NetScheduler.compose
import com.base.httpmvp.retrofitapi.methods.HttpResult
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * mvp中的model
 * @param <T>
</T> */
open class BaseModel {
    /**
     * 封装线程管理和订阅的过程
     * @param observable  被订阅者
     * @param listener    接口回调
     */
    fun <T : HttpResult>subscribe(
        observable: Observable<T>,
        lifecycleOwner: LifecycleOwner?, listener: ObserverResponseListener<T>
    ) {
        val observer: Observer<T> = object : Observer<T> {
            override fun onError(e: Throwable) {
                listener?.onFailure(e.message!!)
                removeDisposable(this.hashCode())
            }

            override fun onComplete() {
                removeDisposable(this.hashCode())
            }

            override fun onSubscribe(disposable: Disposable) {
                addDisposable(this.hashCode(), disposable)
            }

            override fun onNext(model: T) {
                if (model!!.status == HttpCode.SUCCEED) {
                    listener?.onSuccess(model)
                } else {
                    listener?.onFailure(model.msg)
                }
            }
        }
        instance!!.toSubscribe(observable, observer, lifecycleOwner)
    }

    /**
     * 以下下为配合RxJava2+retrofit2使用的
     * 将所有正在处理的Subscription都添加到CompositeSubscription中。统一退出的时候注销观察
     * 一般来讲使用了RxLifecycle 就不需要再手动通过CompositeDisposable 注销观察
     * 特殊情况下需要时再调用addDisposable和unDisposable管理
     */
    private var mCompositeDisposable: CompositeDisposable? = null

    /**
     * 将Disposable添加
     *
     * @param subscription
     */
    fun addDisposable(key: Int, subscription: Disposable) {
        //csb 如果解绑了的话添加 sb 需要新的实例否则绑定时无效的
        if (mCompositeDisposable == null || mCompositeDisposable!!.isDisposed) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable!!.add(subscription)
    }

    /**
     * 在界面退出等需要解绑观察者的情况下调用此方法统一解绑，防止Rx造成的内存泄漏
     */
    fun unDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.dispose()
            mCompositeDisposable!!.clear()
        }
    }

    /**
     * 完成后移除订阅
     */
    fun removeDisposable(subscription: Disposable) {
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.remove(subscription)
        }
    }

    /**
     * 完成后移除订阅
     */
    fun removeDisposable(key: Int) {}
}