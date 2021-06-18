package com.base.httpmvp.retrofitapi

import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import com.base.BaseApplication
import com.base.httpmvp.retrofitapi.converter.MGsonConverterFactory
import com.base.httpmvp.retrofitapi.function.HttpFunctions
import com.base.httpmvp.retrofitapi.methods.HttpResultData
import com.base.httpmvp.retrofitapi.methods.HttpResultJson
import com.base.httpmvp.retrofitapi.proxy.ProxyHandler
import com.base.httpmvp.retrofitapi.util.BaseInterceptor
import com.base.httpmvp.retrofitapi.util.HttpCacheInterceptor
import com.jelly.baselibrary.config.IntentAction
import com.jelly.baselibrary.token.IGlobalManager
import com.jelly.baselibrary.token.TokenModel
import com.jelly.jellybase.BuildConfig
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.lang.reflect.Proxy
import java.util.concurrent.TimeUnit

/**
 * Created by BYPC006 on 2017/3/6.
 */
class HttpMethods private constructor() : IGlobalManager {
    private var retrofit: Retrofit? = null
    companion object {
        @Volatile
        private var sInstance: HttpMethods? = null
        private const val CACHE_NAME = "retrofitcache"

        //	public static final String sBASE_URL = "http://120.26.208.28:8088/";
        private const val DEFAULT_TIMEOUT = 5
        private var sOkHttpClient: OkHttpClient? = null
        private val mRetrofitLock = Any()

        //获取单例
        @JvmStatic
        val instance: HttpMethods?
            get() {
                if (sInstance == null) {
                    synchronized(HttpMethods::class.java) {
                        if (sInstance == null) {
                            sInstance = HttpMethods()
                            return sInstance
                        }
                    }
                }
                return sInstance
            }
    }
    //构造方法私有
    init {
        if (retrofit == null) {
            synchronized(mRetrofitLock) {
                if (retrofit == null) {
                    //设置缓存目录
                    val cacheFile = File(BaseApplication.instance.externalCacheDir, CACHE_NAME)
                    //生成缓存，50M
                    val cache = Cache(cacheFile, 1024 * 1024 * 50)
                    //手动创建一个OkHttpClient并设置超时时间
                    val httpClientBuilder = OkHttpClient.Builder()
                    httpClientBuilder
                        .connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                        .writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                        .readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS) //错误重连
                        .retryOnConnectionFailure(false) //设置拦截器
                        .addInterceptor(BaseInterceptor(BaseApplication.instance))
                        .addNetworkInterceptor(HttpCacheInterceptor(BaseApplication.instance))
                        .cache(cache)
                    val httpLoggingInterceptor = HttpLoggingInterceptor()
                    // 开发模式记录整个body，否则只记录基本信息如返回200，http协议版本等
                    if (BuildConfig.DEBUG) {
                        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                    } else {
                        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
                    }
                    httpClientBuilder.addInterceptor(httpLoggingInterceptor)
                    sOkHttpClient = httpClientBuilder.build()
                    retrofit = Retrofit.Builder()
                        .client(sOkHttpClient) //							.addConverterFactory(GsonConverterFactory.create())
                        //.addConverterFactory(new ListConverterFactory())
                        .addConverterFactory(MGsonConverterFactory.create())
                        .addConverterFactory(ScalarsConverterFactory.create()) //.addConverterFactory(new MGsonConverterFactory())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .baseUrl(BuildConfig.API_HOST)
                        .build()
                }
            }
        }
    }
    override fun exitLogin() {
        // Cancel all the netWorkRequest
        sOkHttpClient!!.dispatcher.cancelAll()
        val intent = Intent()
        intent.action = IntentAction.TOKEN_NOT_EXIST
        BaseApplication.instance.sendBroadcast(intent)
    }

    fun reset() {
        sInstance = null
    }

    operator fun <T> get(tClass: Class<T>): T {
        return retrofit!!.create(tClass)
    }

    fun <T> getProxy(tClass: Class<T>): T {
        val t = retrofit!!.create(tClass)
        return Proxy.newProxyInstance(
            tClass.classLoader, arrayOf<Class<*>>(tClass),
            ProxyHandler(t, this)
        ) as T
    }

    /***
     * 发送崩溃信息
     */
    fun <T>sendError(
        url: String,
        paramMap: Map<Any, Any>,
        lifecycleOwner: LifecycleOwner?,
        subscriber: Observer<HttpResultJson>
    ) {
        val observable: Observable<HttpResultJson> = get(IApiService::class.java).sendError(url, paramMap)
            .flatMap<HttpResultJson>(HttpFunctions())
        toSubscribe<HttpResultJson>(observable, subscriber, lifecycleOwner)
    }

    /***
     * 获取Token
     */
    fun <T>getToken(
        paramMap: String?,
        lifecycleOwner: LifecycleOwner?,
        subscriber: Observer<HttpResultData<TokenModel>>
    ) {
        val observable: Observable<HttpResultData<TokenModel>> = get(IApiService::class.java).getToken(paramMap)
            .flatMap<HttpResultData<TokenModel>>(HttpFunctions())
        toSubscribe<HttpResultData<TokenModel>>(observable, subscriber, lifecycleOwner)
    }

    /***
     * 统一异步,同步处理
     * @param
     * @param observer
     * @param <T>
    </T> */
    fun <T> toSubscribe(
        observable: Observable<T>, observer: Observer<T>, lifecycleOwner: LifecycleOwner?
    ) {
        observable
            .compose(NetScheduler.compose(lifecycleOwner))
            .subscribe(observer)
    }

}