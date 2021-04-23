package com.base.httpmvp.retrofitapi;

import android.content.Intent;
import android.text.TextUtils;

import com.base.BaseApplication;
import com.base.config.IntentAction;
import com.base.httpmvp.retrofitapi.converter.MGsonConverterFactory;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.httpmvp.retrofitapi.methods.HttpResultJson;
import com.base.httpmvp.retrofitapi.proxy.ProxyHandler;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.httpmvp.retrofitapi.token.IGlobalManager;
import com.base.httpmvp.retrofitapi.token.TokenModel;
import com.base.httpmvp.retrofitapi.util.BaseInterceptor;
import com.base.httpmvp.retrofitapi.util.HttpCacheInterceptor;
import com.jelly.baselibrary.model.UploadBean;
import com.jelly.baselibrary.model.UploadData;
import com.jelly.jellybase.BuildConfig;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpMethods implements IGlobalManager {
	private volatile static HttpMethods sInstance;
	private static final String CACHE_NAME  = "retrofitcache";

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
					File cacheFile = new File(BaseApplication.getInstance().getExternalCacheDir(), CACHE_NAME);
					//生成缓存，50M
					Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
					//手动创建一个OkHttpClient并设置超时时间
					OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
					httpClientBuilder
							.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
							.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
							.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
							//错误重连
							.retryOnConnectionFailure(false)
							//设置拦截器
							.addInterceptor(new BaseInterceptor(BaseApplication.getInstance()))
							.addNetworkInterceptor(new HttpCacheInterceptor(BaseApplication.getInstance()))
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
							.addConverterFactory(ScalarsConverterFactory.create())
							//.addConverterFactory(new MGsonConverterFactory())
							.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
							.baseUrl(BuildConfig.API_HOST)
							.build();
				}
			}
		}
	}

	@Override
	public void exitLogin() {
		// Cancel all the netWorkRequest
		sOkHttpClient.dispatcher().cancelAll();

		Intent intent=new Intent();
		intent.setAction(IntentAction.TOKEN_NOT_EXIST);
		BaseApplication.getInstance().sendBroadcast(intent);
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
	 *发送崩溃信息
	 */
	public void sendError(String url, Map paramMap, ObservableTransformer composer, Observer<HttpResultJson> subscriber){
		Observable observable = get(IApiService.class).sendError(url,paramMap)
				.flatMap(new HttpFunctions<HttpResultJson>());
		toSubscribe(observable,subscriber,composer);
	}
	/***
	 *获取Token
	 */
	public void getToken(String paramMap, ObservableTransformer composer, Observer<HttpResultData<TokenModel>> subscriber){
		Observable observable = get(IApiService.class).getToken(paramMap)
				.flatMap(new HttpFunctions<HttpResultData<TokenModel>>());
		toSubscribe(observable,subscriber,composer);
	}
	/**
	 * 上传文件(图片)
	 */
	public void upload(File file, UploadBean uploadBean, ObservableTransformer composer, Observer<HttpResultData<UploadData>> subscriber){
		// 创建 RequestBody，用于封装构建RequestBody
		RequestBody requestFile =
				RequestBody.create(MediaType.parse("multipart/form-data"), file);
		String type="image";
		if(!TextUtils.isEmpty(uploadBean.getFileType())){
			type=uploadBean.getFileType();
		}
		// MultipartBody.Part  和后端约定好Key
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
				.flatMap(new HttpFunctions<HttpResultData<UploadData>>());
		toSubscribe(observable, subscriber,composer);
	}



	/***
	 * 统一异步,同步处理
	 * @param
	 * @param observer
	 * @param <T>
	 */
	public   <T> void toSubscribe(Observable<T> observable, Observer<T> observer
			,ObservableTransformer composer){
		if (composer!=null) {
			observable.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.unsubscribeOn(Schedulers.io())
					.compose(composer)
					.subscribe(observer);
		}else {
			observable.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.unsubscribeOn(Schedulers.io())
					.subscribe(observer);
		}
	}
}
