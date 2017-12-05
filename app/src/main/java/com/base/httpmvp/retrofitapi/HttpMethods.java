package com.base.httpmvp.retrofitapi;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.base.applicationUtil.MyApplication;
import com.base.bankcard.BankCardInfo;
import com.base.config.BaseConfig;
import com.base.config.IntentAction;
import com.base.httpmvp.mode.databean.AboutUs;
import com.base.httpmvp.mode.databean.AppVersion;
import com.base.httpmvp.mode.databean.UploadBean;
import com.base.httpmvp.mode.databean.UploadData;
import com.base.httpmvp.retrofitapi.converter.MGsonConverterFactory;
import com.base.httpmvp.retrofitapi.proxy.ProxyHandler;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.httpmvp.retrofitapi.token.IGlobalManager;
import com.base.httpmvp.retrofitapi.token.TokenModel;
import com.jelly.jellybase.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import systemdb.Login;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpMethods implements IGlobalManager {
	private volatile static HttpMethods sInstance;
	private static final String CACHE_NAME  = "retrofitcache";

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
					//设置缓存目录
					File cacheFile = new File(MyApplication.getMyApp().getExternalCacheDir(), CACHE_NAME);
					//生成缓存，50M
					Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
					//缓存拦截器
					Interceptor cacheInterceptor = new Interceptor() {
						@Override
						public Response intercept(Chain chain) throws IOException {
							Request request = chain.request();
							//网络不可用
							if (!NetworkUtils.isAvailable(MyApplication.getMyApp())) {
								//在请求头中加入：强制使用缓存，不访问网络
								request = request.newBuilder()
										.cacheControl(CacheControl.FORCE_CACHE)
										.build();
								Log.i("sss","no network");
							}
							Response response = chain.proceed(request);
							//网络可用
							if (NetworkUtils.isAvailable(MyApplication.getMyApp())) {
								int maxAge = 0;
								// 有网络时 在响应头中加入：设置缓存超时时间0个小时
								Log.i("sss","has network maxAge="+maxAge);
								response.newBuilder()
										.header("Cache-Control", "public, max-age=" + maxAge)
										//.removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
										.build();
							} else {
								Log.i("sss","network error");
								// 无网络时，在响应头中加入：设置缓存超时为4周
								//离线缓存控制 总缓存时间=在线缓存时间+设置离线时的缓存时间
								int maxStale = 60 * 60 * 24 * 28;
								response.newBuilder()
										.header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
										//.removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
										.build();
								Log.i("sss","response build maxStale="+maxStale);
							}
							return response;
						}
					};
					//只有 网络拦截器环节 才会写入缓存写入缓存,在有网络的时候 设置缓存时间
					Interceptor rewriteCacheControlInterceptor = new Interceptor() {
						@Override
						public Response intercept(Chain chain) throws IOException {
							Request request = chain.request();
							Response originalResponse = chain.proceed(request);
							int maxAge = 1 * 60; // 在线缓存在1分钟内可读取 单位:秒
							return originalResponse.newBuilder()
									.removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
									.removeHeader("Cache-Control")
									.header("Cache-Control", "public, max-age=" + maxAge)
									.build();
						}
					};
					//手动创建一个OkHttpClient并设置超时时间
					OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
					httpClientBuilder
							.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
							.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
							.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
							//错误重连
							.retryOnConnectionFailure(false)
							//设置缓存
							.addInterceptor(cacheInterceptor)
							.addNetworkInterceptor(rewriteCacheControlInterceptor)
							.cache(cache);

					HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
					// 开发模式记录整个body，否则只记录基本信息如返回200，http协议版本等
					if (BuildConfig.DEBUG) {
						httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
					} else {
						httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
					}
					httpClientBuilder.addInterceptor(httpLoggingInterceptor);
					sOkHttpClient = httpClientBuilder.build();

					retrofit = new Retrofit.Builder()
							.client(sOkHttpClient)
//							.addConverterFactory(GsonConverterFactory.create())
							//.addConverterFactory(new ListConverterFactory())
							.addConverterFactory(MGsonConverterFactory.create())
							//.addConverterFactory(new MGsonConverterFactory())
							.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
		unDisposable();

		Intent intent=new Intent();
		intent.setAction(IntentAction.TOKEN_NOT_EXIST);
		MyApplication.getMyApp().sendBroadcast(intent);
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
	public void getToken(Object paramMap, ObservableTransformer composer, Observer<HttpResultData<TokenModel>> subscriber){
		Observable observable = get(IApiService.class).getToken(paramMap)
				.flatMap(new HttpResultFuncs<HttpResultData<TokenModel>>());
		toSubscribe(observable,subscriber,composer);
	}
	/***
	 * 登录
	 * @param subscriber
	 */
	public void userLogin(Object paramMap,ObservableTransformer composer, Observer<HttpResultData<Login>> subscriber){
		Observable observable =  getProxy(IApiService.class).userLogin(paramMap)
				.flatMap(new HttpResultFuncs<HttpResultData<Login>>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 获取验证码
	 * @param subscriber
	 */
	public void getVerifiCode(Object paramMap,ObservableTransformer composer, Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).getVerifiCode(paramMap)
				.flatMap(new HttpResultFuncs<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 注册
	 * @param subscriber
	 */
	public void userRegistration(Object paramMap,ObservableTransformer composer, Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).userRegistration(paramMap)
				//.map(new HttpResultFunc<List<HttpResult>>());
				.flatMap(new HttpResultFuncs<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 忘记密码
	 * @param subscriber
	 */
	public void forgetPassword(Object paramMap,ObservableTransformer composer, Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).forgetPassword(paramMap)
				//.map(new HttpResultFunc<List<HttpResult>>());
				.flatMap(new HttpResultFuncs<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 设置密码
	 * @param subscriber
	 */
	public void setPassWord(Object paramMap,ObservableTransformer composer, Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).setPassWord(paramMap)
				.flatMap(new HttpResultFuncs<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 修改密码
	 * @param subscriber
	 */
	public void updatePassword(Object paramMap,ObservableTransformer composer, Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).updatePassword(GlobalToken.getToken().getToken(),paramMap)
				.flatMap(new HttpResultFuncs<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 修改手机号
	 * @param subscriber
	 */
	public void updatePhone(Object paramMap,ObservableTransformer composer, Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).updatePhone(GlobalToken.getToken().getToken(),paramMap)
				.flatMap(new HttpResultFuncs<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 关于我们
	 * @param subscriber
	 */
	public void aboutUs(ObservableTransformer composer,Observer<HttpResultData<AboutUs>> subscriber){
		Observable observable =  getProxy(IApiService.class).aboutUs(GlobalToken.getToken().getToken())
				.flatMap(new HttpResultFuncs<HttpResultData<AboutUs>>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 检查版本
	 * @param subscriber
	 */
	public void getAppversionList(ObservableTransformer composer,Observer<HttpResultData<AppVersion>> subscriber){
		Observable observable =  getProxy(IApiService.class).getAppversionList(GlobalToken.getToken().getToken())
				.flatMap(new HttpResultFuncs<HttpResultData<AppVersion>>());
		toSubscribe(observable, subscriber,composer);
	}
	/**
	 * 上传文件(图片)
	 */
	public void upload(File file, UploadBean uploadBean, ObservableTransformer composer, Observer<HttpResultData<UploadData>> subscriber){
		// 创建 RequestBody，用于封装构建RequestBody
		RequestBody requestFile =
				RequestBody.create(MediaType.parse("multipart/form-data"), file);
		// MultipartBody.Part  和后端约定好Key，这里的partName是用image
		String type="image";
		if(!TextUtils.isEmpty(uploadBean.getFileType())){
			type=uploadBean.getFileType();
		}
		MultipartBody.Part body =
				MultipartBody.Part.createFormData(type, file.getName(), requestFile);
		// 添加描述
		String descriptionString =uploadBean.getFileDesc();
		RequestBody description =
				RequestBody.create(
						MediaType.parse("multipart/form-data"), descriptionString);
		// 执行请求
		Observable observable = getProxy(IApiService.class).upload(GlobalToken.getToken().getToken(),
				description, body)
				.flatMap(new HttpResultFuncs<HttpResultData<UploadData>>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 获取所属银行
	 * @param subscriber
	 */
	public void getBank(Object param,ObservableTransformer composer,Observer<HttpResultData<BankCardInfo>> subscriber){
		Observable observable =  getProxy(IApiService.class).getBank(GlobalToken.getToken().getToken(),param)
				.flatMap(new HttpResultFuncs<HttpResultData<BankCardInfo>>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 添加银行卡
	 * @param subscriber
	 */
	public void addbank(Object param,ObservableTransformer composer,Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).addbank(GlobalToken.getToken().getToken(),param)
				.flatMap(new HttpResultFuncs<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}

	/***
	 * 获取银行卡列表
	 * @param subscriber
	 */
	public void bankList(Object paramMap,ObservableTransformer composer,Observer<HttpResultList<BankCardInfo>> subscriber){
		Observable observable =  getProxy(IApiService.class).bankList(GlobalToken.getToken().getToken(),paramMap)
				.flatMap(new HttpResultFuncs<HttpResultList<BankCardInfo>>());
		toSubscribe(observable, subscriber,composer);
	}


	/***
	 * 统一异步,同步处理
	 * @param
	 * @param s
	 * @param <T>
	 */
	private <T> void toSubscribe(Observable<T> o, Observer<T> s
			,ObservableTransformer composer){
		if (composer!=null) {
			o.subscribeOn(Schedulers.io())
					.unsubscribeOn(Schedulers.io())
					.doOnSubscribe(new Consumer<Disposable>() {
						@Override
						public void accept(@NonNull Disposable disposable) throws Exception {
							addDisposable(disposable);//请求加入管理
						}
					})
					.observeOn(AndroidSchedulers.mainThread())
					.compose(composer)
					.subscribe(s);
		}else {
			o.subscribeOn(Schedulers.io())
					.unsubscribeOn(Schedulers.io())
					.doOnSubscribe(new Consumer<Disposable>() {
						@Override
						public void accept(@NonNull Disposable disposable) throws Exception {
							addDisposable(disposable);//请求加入管理
						}
					})
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(s);
		}
	}
	//map
	private class HttpResultFunc<T> implements Function<HttpStateData<T>, T> {

		@Override
		public T apply(@NonNull HttpStateData<T> tHttpStateData) throws Exception {
			return tHttpStateData.getData();
		}
	}
	//flatMap
	private class HttpResultFuncs<T> implements Function<HttpStateData<T>, Observable<T>> {

		@Override
		public Observable<T> apply(@NonNull HttpStateData<T> tHttpStateData) throws Exception {
			return Observable.just(tHttpStateData.getData());
		}
	}
}
