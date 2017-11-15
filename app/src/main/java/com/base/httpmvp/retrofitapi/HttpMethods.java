package com.base.httpmvp.retrofitapi;

import android.content.Intent;
import android.text.TextUtils;

import com.base.applicationUtil.MyApplication;
import com.base.bankcard.BankCardInfo;
import com.base.config.BaseConfig;
import com.base.config.IntentAction;
import com.base.eventBus.LoginEvent;
import com.base.eventBus.NetEvent;
import com.base.httpmvp.mode.databean.UploadBean;
import com.base.httpmvp.mode.databean.UploadData;
import com.base.httpmvp.retrofitapi.converter.MGsonConverterFactory;
import com.base.httpmvp.retrofitapi.proxy.ProxyHandler;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.httpmvp.retrofitapi.token.IGlobalManager;
import com.base.httpmvp.retrofitapi.token.TokenModel;
import com.base.sqldao.DBHelper;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import xiaofei.library.hermeseventbus.HermesEventBus;

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

					HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
					httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
					httpClientBuilder.addInterceptor(httpLoggingInterceptor);

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
		GlobalToken.removeToken();
		DBHelper.getInstance(MyApplication.getMyApp()).clearLogin();
		NetEvent netEvent = new NetEvent();
		netEvent.setEvent(new LoginEvent(false));
		HermesEventBus.getDefault().post(netEvent);

		Intent intent=new Intent();
		intent.setAction(IntentAction.TOKEN_NOT_EXIST);
		MyApplication.getMyApp().sendBroadcast(intent);
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
	public void getToken(Object paramMap, Subscriber<HttpResultData<TokenModel>> subscriber){
		Observable observable = get(IApiService.class).getToken(paramMap)
				.flatMap(new HttpResultFuncs<HttpResultData<TokenModel>>());;
		//.onErrorResumeNext(new HttpResponseFunc<HttpResult>());;
		toSubscribe(observable,subscriber);
	}
	/***
	 * 注册
	 * @param subscriber
	 */
	public void userRegistration(Object paramMap, Subscriber<List<HttpResult>> subscriber){
		Observable observable =  getProxy(IApiService.class).userRegistration(paramMap)
				.map(new HttpResultFunc<List<HttpResult>>());
				//.flatMap(new HttpResultFuncs<HttpResult>());
		//.onErrorResumeNext(new HttpResponseFunc<HttpResult>());;
		toSubscribe(observable, subscriber);
	}
	/**
	 * 上传文件(图片)
	 */
	public void upload(File file, UploadBean uploadBean, Subscriber<HttpResultData<UploadData>> subscriber){
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
		Observable observable = getProxy(IApiService.class).upload(GlobalToken.getToken().getToken()
				,description, body)
				.flatMap(new HttpResultFuncs<HttpResultData<UploadData>>());
		//.onErrorResumeNext(new HttpResponseFunc<HttpResult>());;
		toSubscribe(observable, subscriber);
	}

	/***
	 * 获取所属银行
	 * @param subscriber
	 */
	public void getBank(Object param,Subscriber<HttpResultData<BankCardInfo>> subscriber){
		Observable observable =  getProxy(IApiService.class).getBank(GlobalToken.getToken().getToken(),param)
				.flatMap(new HttpResultFuncs<HttpResultData<BankCardInfo>>());
		//.onErrorResumeNext(new HttpResponseFunc<HttpResult>());;
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
	//map
	private class HttpResultFunc<T> implements Func1<HttpStateData<T>, T> {
		@Override
		public T call(HttpStateData<T> tHttpStateData) {
//			if (tHttpStateData.getStatus() == 0) {
//				throw new ApiException(tHttpStateData.getMsg());
//			}
			return tHttpStateData.getData();
		}
	}
	//flatMap
	private class HttpResultFuncs<T> implements Func1<HttpStateData<T>, Observable<T>> {
		@Override
		public Observable<T> call(HttpStateData<T> tHttpStateData) {
//			if (!tHttpStateData.isReturnState()) {
//				throw new ApiException(tHttpStateData.getMsg());
//			}
			return Observable.just(tHttpStateData.getData());
		}
	}

	//ExceptionEngine为处理异常的驱动器
	private class HttpResponseFunc<T> implements Func1<Throwable, Observable<T>> {
		@Override public Observable<T> call(Throwable throwable) {
			//ExceptionEngine为处理异常的驱动器
			//return Observable.error(new Throwable(throwable));
			return Observable.error(throwable);
		}
	}
}
