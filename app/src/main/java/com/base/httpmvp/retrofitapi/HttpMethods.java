package com.base.httpmvp.retrofitapi;

import android.text.TextUtils;

import com.base.config.BaseConfig;
import com.base.httpmvp.mode.databean.TokenModel;
import com.base.httpmvp.retrofitapi.converter.MGsonConverterFactory;
import com.base.httpmvp.retrofitapi.proxy.ProxyHandler;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.httpmvp.retrofitapi.token.IGlobalManager;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpMethods implements IGlobalManager {
	private volatile static HttpMethods sInstance;

	public static String sBASE_URL = BaseConfig.SERVICE_IP;
	public static String sUrl = "http://" + sBASE_URL + "/";
//	public static final String sBASE_URL = "http://120.26.208.28:8088/";
	private static final int DEFAULT_TIMEOUT = 5;

	private Retrofit retrofit;
	private static OkHttpClient sOkHttpClient;
	private final static Object mRetrofitLock = new Object();

	//构造方法私有
	private HttpMethods() {
		if (retrofit == null) {
			synchronized (mRetrofitLock) {
				if (retrofit == null) {
					//手动创建一个OkHttpClient并设置超时时间
					OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
					httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

//					HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//					httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//					httpClientBuilder.addInterceptor(httpLoggingInterceptor);

					sOkHttpClient = httpClientBuilder.build();

					retrofit = new Retrofit.Builder()
							.client(sOkHttpClient)
//							.addConverterFactory(GsonConverterFactory.create())
							//.addConverterFactory(new ListConverterFactory())
							.addConverterFactory(MGsonConverterFactory.create())
							//.addConverterFactory(new MGsonConverterFactory())
							.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
							.baseUrl(sUrl)
							.build();
				}
			}
		}
	}

	@Override
	public void exitLogin() {
		// Cancel all the netWorkRequest
		sOkHttpClient.dispatcher().cancelAll();
		// Goto the home page
//		new Handler(Looper.getMainLooper()).post(new Runnable() {
//			@Override
//			public void run() {
//				Intent intent = new Intent(BaseApplication.getContext(), MainActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				BaseApplication.getContext().startActivity(intent);
//				Toast.makeText(BaseApplication.getContext(), "Token is not existed!!", Toast.LENGTH_SHORT).show();
//			}
//		});
	}
	//获取单例
	public static HttpMethods getInstance(){
		if (sInstance == null){
			synchronized (HttpMethods.class){
				if (sInstance == null){
					sInstance = new HttpMethods();
					return sInstance;
				}
			}
		}
		return sInstance;
	}

	public void reset(){
		sInstance = null;
	}

	public <T> T get(Class<T> tClass) {
		return retrofit.create(tClass);
	}

	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> tClass) {
		T t = retrofit.create(tClass);
		return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[] { tClass },
				new ProxyHandler(t, this));
	}
	/***
	 *获取Token
	 */
	public void getToken(){
		Observable observable = get(IApiService.class).getToken();
		toSubscribe(observable, new Subscriber<TokenModel>() {
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {

			}

			@Override
			public void onNext(TokenModel model) {
				if (model != null && !TextUtils.isEmpty(model.token)) {
					GlobalToken.updateToken(model.token);
				}
			}
		});
	}
	/***
	 *重新获取Token
	 */
	public void regainToken(){
		Observable observable = get(IApiService.class).refreshToken();
		toSubscribe(observable, new Subscriber<TokenModel>() {
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {

			}

			@Override
			public void onNext(TokenModel model) {
				if (model != null && !TextUtils.isEmpty(model.token)) {
					GlobalToken.updateToken(model.token);
				}
			}
		});
	}
	/***
	 * 注册
	 * @param subscriber
	 */
	public void userRegistration(Object paramMap, Subscriber<List<HttpResultAll>> subscriber){
		Observable observable =  getProxy(IApiService.class).userRegistration(paramMap)
				.map(new HttpResultFunc<List<HttpResultAll>>());
		toSubscribe(observable, subscriber);
	}
	/***
	 * 统一异步,同步处理
	 * @param o
	 * @param s
	 * @param <T>
	 */
	private <T> void toSubscribe(Observable<T> o, Subscriber<T> s){
		o.subscribeOn(Schedulers.io())
				.unsubscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(s);
	}

	private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {
		@Override
		public T call(HttpResult<T> tHttpResult) {
//			if (tHttpResult.getStatus() == 0) {
//				throw new ApiException(tHttpResult.getMsg());
//			}
			return tHttpResult.getData();
		}
	}
}
