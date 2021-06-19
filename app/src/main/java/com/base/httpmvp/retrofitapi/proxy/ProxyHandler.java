package com.base.httpmvp.retrofitapi.proxy;

import android.text.TextUtils;
import android.util.Log;

import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.httpmvp.retrofitapi.exception.ApiException;
import com.base.httpmvp.retrofitapi.exception.TokenInvalidException;
import com.base.httpmvp.retrofitapi.exception.TokenNotExistException;
import com.jelly.baselibrary.moshi.JsonTool;
import com.jelly.baselibrary.token.GlobalToken;
import com.jelly.baselibrary.token.IGlobalManager;
import com.jelly.baselibrary.token.TokenModel;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import retrofit2.http.Query;


/**
 * Created by david on 16/8/21.
 */
public class ProxyHandler implements InvocationHandler {

    private final static String TAG = "Token_Proxy";

    private final static String TOKEN = "token";

    private static int REFRESH_TOKEN_VALID_TIME = 30;
    private static long tokenChangedTime = 0;
    private Throwable mRefreshTokenError = null;
    private boolean mIsTokenNeedRefresh;

    private Object mProxyObject;
    private IGlobalManager mGlobalManager;

    public ProxyHandler(Object proxyObject, IGlobalManager globalManager) {
        mProxyObject = proxyObject;
        mGlobalManager = globalManager;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return Observable.just(proxy).flatMap(new Function<Object, Observable<?>>() {
            @Override
            public Observable<?> apply(@NonNull Object o) throws Exception {
                try {
                    try {
                        if (mIsTokenNeedRefresh) {
                            updateMethodToken(method, args);
                        }
                        return (Observable<?>) method.invoke(mProxyObject, args);
                    } catch (InvocationTargetException e) {
                        return Observable.error(new ApiException(e));
                    }
                } catch (IllegalAccessException e) {
                    return Observable.error(new ApiException(e));
                }
            }
        }).retryWhen(new Function<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> apply(@NonNull Observable<? extends Throwable> observable) throws Exception {
                return observable.flatMap(new Function<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> apply(@NonNull Throwable throwable) throws Exception {
                        Log.i("sss","ProxyHandler throwable="+throwable);
                        if (throwable instanceof TokenInvalidException) {
                            //return refreshTokenWhenTokenInvalid();
                            mGlobalManager.exitLogin();
                            return Observable.empty();
                        } else if (throwable instanceof TokenNotExistException) {
                            // Token 不存在，执行退出登录的操作。（为了防止多个请求，都出现 Token 不存在的问题，
                            // 这里需要取消当前所有的网络请求）
                            mGlobalManager.exitLogin();
                            return Observable.empty();
                        }
                        return Observable.error(new ApiException(throwable));
                    }
                });
            }
        });
    }

    /**
     * Refresh the token when the current token is invalid.
     *
     * @return Observable
     */
    private Observable<?> refreshTokenWhenTokenInvalid() {
        synchronized (ProxyHandler.class) {
            REFRESH_TOKEN_VALID_TIME= GlobalToken.getToken().getTokenExpirationTime()*1000;
            tokenChangedTime= GlobalToken.getToken().getCreateTime();
            // Have refreshed the token successfully in the valid time.
            if (new Date().getTime() - tokenChangedTime < REFRESH_TOKEN_VALID_TIME) {
                mIsTokenNeedRefresh = true;
                return Observable.just(true);
            } else {
                // call the refresh token api.
                    Map<String,String> map=new TreeMap<>();
                    //map.put("saleid",login.getUserID()+"");
                    HttpMethods.getInstance().getToken(JsonTool.get().toJson(map),null
                            ,new Observer<HttpResultData<TokenModel>>() {

                                @Override
                                public void onError(Throwable e) {
                                    mRefreshTokenError = e;
                                }

                                @Override
                                public void onComplete() {

                                }

                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(HttpResultData<TokenModel> model) {
                                    if (model != null && model.getStatus()== HttpCode.SUCCEED) {
                                        TokenModel tokenModel = model.getData();
                                        if (tokenModel != null) {
                                            mIsTokenNeedRefresh = true;
                                            tokenChangedTime = new Date().getTime();
                                            GlobalToken.updateToken(tokenModel);
                                            Log.i(TAG, "Refresh token success, time = " + tokenChangedTime);
                                        }
                                    }
                                }
                            });
                if (mRefreshTokenError != null) {
                    return Observable.error(new ApiException(mRefreshTokenError));
                } else {
                    return Observable.just(true);
                }
            }
        }
    }

    /**
     * Update the token of the args in the method.
     *
     * PS： 因为这里使用的是 GET 请求，所以这里就需要对 Query 的参数名称为 token 的方法。
     * 若是 POST 请求，或者使用 Body ，自行替换。因为 参数数组已经知道，进行遍历找到相应的值，进行替换即可（更新为新的 token 值）。
     */
    private void updateMethodToken(Method method, Object[] args) {
        if (mIsTokenNeedRefresh && !TextUtils.isEmpty(GlobalToken.getToken().getToken())) {
            Annotation[][] annotationsArray = method.getParameterAnnotations();
            Annotation[] annotations;
            if (annotationsArray != null && annotationsArray.length > 0) {
                for (int i = 0; i < annotationsArray.length; i++) {
                    annotations = annotationsArray[i];
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Query) {
                            if (TOKEN.equals(((Query) annotation).value())) {
                                args[i] = GlobalToken.getToken();
                            }
                        }
                    }
                }
            }
            mIsTokenNeedRefresh = false;
        }
    }
}