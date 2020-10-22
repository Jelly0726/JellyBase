package com.base.httpmvp.retrofitapi;

import android.content.Intent;
import android.text.TextUtils;

import com.base.appManager.BaseApplication;
import com.base.bankcard.BankCardInfo;
import com.base.config.IntentAction;
import com.base.model.AboutUs;
import com.base.model.AccountDetail;
import com.base.model.AppVersion;
import com.base.model.Message;
import com.base.model.PersonalInfo;
import com.base.model.UploadBean;
import com.base.model.UploadData;
import com.base.httpmvp.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.converter.MGsonConverterFactory;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.httpmvp.retrofitapi.methods.HttpResultJson;
import com.base.httpmvp.retrofitapi.methods.HttpResultList;
import com.base.httpmvp.retrofitapi.proxy.ProxyHandler;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.httpmvp.retrofitapi.token.IGlobalManager;
import com.base.httpmvp.retrofitapi.token.TokenModel;
import com.base.httpmvp.retrofitapi.util.BaseInterceptor;
import com.base.httpmvp.retrofitapi.util.HttpCacheInterceptor;
import com.jelly.jellybase.BuildConfig;
import com.jelly.jellybase.datamodel.RecevierAddress;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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
import systemdb.Login;

/**
 * Created by BYPC006 on 2017/3/6.
 */

public class HttpMethods implements IGlobalManager {
	private volatile static HttpMethods sInstance;
	private static final String CACHE_NAME  = "retrofitcache";

	public static String sBASE_URL =BuildConfig.API_HOST;
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
		BaseApplication.getInstance().sendBroadcast(intent);
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
			mCompositeDisposable.clear();
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
	/***
	 * 登录
	 * @param subscriber
	 */
	public void userLogin(String paramMap,ObservableTransformer composer, Observer<HttpResultData<Login>> subscriber){
		Observable observable =  getProxy(IApiService.class).userLogin(paramMap)
				.flatMap(new HttpFunctions<HttpResultData<Login>>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 获取验证码
	 * @param subscriber
	 */
	public void getVerifiCode(String paramMap,ObservableTransformer composer, Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).getVerifiCode(paramMap)
				.flatMap(new HttpFunctions<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 注册
	 * @param subscriber
	 */
	public void userRegistration(String paramMap,ObservableTransformer composer, Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).userRegistration(paramMap)
				//.map(new HttpResultFunc<List<HttpResult>>());
				.flatMap(new HttpFunctions<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 忘记密码
	 * @param subscriber
	 */
	public void forgetPassword(String paramMap,ObservableTransformer composer, Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).forgetPassword(paramMap)
				//.map(new HttpResultFunc<List<HttpResult>>());
				.flatMap(new HttpFunctions<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 设置密码
	 * @param subscriber
	 */
	public void setPassWord(String paramMap,ObservableTransformer composer, Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).setPassWord(paramMap)
				.flatMap(new HttpFunctions<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 修改密码
	 * @param subscriber
	 */
	public void updatePassword(String paramMap,ObservableTransformer composer, Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).updatePassword(GlobalToken.getToken().getToken(),paramMap)
				.flatMap(new HttpFunctions<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 修改手机号
	 * @param subscriber
	 */
	public void updatePhone(String paramMap,ObservableTransformer composer, Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).updatePhone(GlobalToken.getToken().getToken(),paramMap)
				.flatMap(new HttpFunctions<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 获取个人资料
	 * @param subscriber
	 */
	public void findBuyerInfo(ObservableTransformer composer,Observer<HttpResultData<PersonalInfo>> subscriber){
		Observable observable =  getProxy(IApiService.class).findBuyerInfo(GlobalToken.getToken().getToken())
				.flatMap(new HttpFunctions<HttpResultData<PersonalInfo>>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 完善个人资料
	 * @param subscriber
	 */
	public void updateBuyerInfo(String paramMap,ObservableTransformer composer,Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).updateBuyerInfo(GlobalToken.getToken().getToken(),
				paramMap)
				.flatMap(new HttpFunctions<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 关于我们
	 * @param subscriber
	 */
	public void aboutUs(ObservableTransformer composer,Observer<HttpResultData<AboutUs>> subscriber){
		Observable observable =  getProxy(IApiService.class).aboutUs(GlobalToken.getToken().getToken())
				.flatMap(new HttpFunctions<HttpResultData<AboutUs>>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 检查版本
	 * @param subscriber
	 */
	public void getAppversionList(ObservableTransformer composer,Observer<HttpResultData<AppVersion>> subscriber){
		Observable observable =  getProxy(IApiService.class).getAppversionList(GlobalToken.getToken().getToken())
				.flatMap(new HttpFunctions<HttpResultData<AppVersion>>());
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
				.flatMap(new HttpFunctions<HttpResultData<UploadData>>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 获取所属银行
	 * @param subscriber
	 */
	public void getBank(String param,ObservableTransformer composer,Observer<HttpResultData<BankCardInfo>> subscriber){
		Observable observable =  getProxy(IApiService.class).getBank(GlobalToken.getToken().getToken(),param)
				.flatMap(new HttpFunctions<HttpResultData<BankCardInfo>>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 添加银行卡
	 * @param subscriber
	 */
	public void addbank(String param,ObservableTransformer composer,Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).addbank(GlobalToken.getToken().getToken(),param)
				.flatMap(new HttpFunctions<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}

	/***
	 * 获取银行卡列表
	 * @param subscriber
	 */
	public void bankList(String paramMap,ObservableTransformer composer,Observer<HttpResultList<BankCardInfo>> subscriber){
		Observable observable =  getProxy(IApiService.class).bankList(GlobalToken.getToken().getToken(),paramMap)
				.flatMap(new HttpFunctions<HttpResultList<BankCardInfo>>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 删除银行卡
	 * @param subscriber
	 */
	public void deletebank(String param,ObservableTransformer composer,Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).deletebank(GlobalToken.getToken().getToken(),param)
				.flatMap(new HttpFunctions<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 提现
	 * @param subscriber
	 */
	public void withdrawals(String paramMap,ObservableTransformer composer, Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).withdrawals(GlobalToken.getToken().getToken(),paramMap)
				.flatMap(new HttpFunctions<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 账户明细
	 * @param subscriber
	 */
	public void accountDetails(String paramMap,ObservableTransformer composer, Observer<HttpResultList<AccountDetail>> subscriber){
		Observable observable =  getProxy(IApiService.class).accountDetails(GlobalToken.getToken().getToken(),paramMap)
				.flatMap(new HttpFunctions<HttpResultList<AccountDetail>>());
		toSubscribe(observable, subscriber,composer);
	}
	/**
	 * 配置地址
	 */
	public void operaAddress(String paramMap,ObservableTransformer composer,Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).operaAddress(GlobalToken.getToken().getToken(),paramMap)
				.flatMap(new HttpFunctions<HttpResult>());
		toSubscribe(observable, subscriber,composer);
	}
	/**
	 * 获取地址列表
	 */
	public void getAddressList(String paramMap,ObservableTransformer composer,Observer<HttpResultList<RecevierAddress>> subscriber){
		Observable observable =  getProxy(IApiService.class).getAddressList(GlobalToken.getToken().getToken(),paramMap)
				.flatMap(new HttpFunctions<HttpResultList<RecevierAddress>>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 消息通知列表
	 * @param subscriber
	 */
	public void getMessage(String paramMap,ObservableTransformer composer,Observer<HttpResultList<Message>> subscriber){
		Observable observable =  getProxy(IApiService.class).getMessage(GlobalToken.getToken().getToken(),paramMap)
				.flatMap(new HttpFunctions<HttpResultList<Message>>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 消息通知详情
	 * @param subscriber
	 */
	public void getMessageDetails(String paramMap,ObservableTransformer composer,Observer<HttpResultData<Message>> subscriber){
		Observable observable =  getProxy(IApiService.class).getMessageDetails(GlobalToken.getToken().getToken(),paramMap)
				.flatMap(new HttpFunctions<HttpResultData<Message>>());
		toSubscribe(observable, subscriber,composer);
	}
	/***
	 * 取消订单
	 * @param subscriber
	 */
	public void cancelOrder(String paramMap,ObservableTransformer composer,Observer<HttpResult> subscriber){
		Observable observable =  getProxy(IApiService.class).cancelOrder(GlobalToken.getToken().getToken(),paramMap)
				.flatMap(new HttpFunctions<HttpResult>());
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
}
