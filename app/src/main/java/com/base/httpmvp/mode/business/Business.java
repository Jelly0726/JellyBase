package com.base.httpmvp.mode.business;

import android.text.TextUtils;

import com.base.bankcard.BankCardInfo;
import com.base.httpmvp.mode.databean.AboutUs;
import com.base.httpmvp.mode.databean.AppVersion;
import com.base.httpmvp.mode.databean.UploadBean;
import com.base.httpmvp.mode.databean.UploadData;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.retrofitapi.HttpResultData;
import com.base.httpmvp.retrofitapi.HttpResultList;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.httpmvp.retrofitapi.token.TokenModel;
import com.google.gson.Gson;

import java.io.File;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import systemdb.Login;

/**
 * Created by Administrator on 2017/11/8.
 */

public class Business implements IBusiness {
    private static Gson gson= new Gson();
    private static final String TAG = Business.class.getSimpleName();
    //获取token
    @Override
    public void getToken(Object obj,ObservableTransformer composer) {
        HttpMethods.getInstance().getToken(gson.toJson(obj),composer
                ,new Observer<HttpResultData<TokenModel>>() {

                    @Override
                    public void onError(Throwable e) {

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
                            if (tokenModel != null && !TextUtils.isEmpty(tokenModel.getToken())) {
                                GlobalToken.updateToken(tokenModel);
                            }
                        }
                    }
                });
    }
    @Override
    public void register(Object obj,ObservableTransformer composer, final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().userRegistration(gson.toJson(obj),composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResult model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }
    @Override
    public void getVerifiCode(Object mUserVo,ObservableTransformer composer, final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().getVerifiCode(gson.toJson(mUserVo),composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResult model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }

    @Override
    public void setPassword(Object mUserVo,ObservableTransformer composer, final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().setPassWord(gson.toJson(mUserVo),composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResult model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }
    @Override
    public void updatePassword(Object mUserVo,ObservableTransformer composer, final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().updatePassword(gson.toJson(mUserVo),composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResult model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }
    @Override
    public void updatePhone(Object mUserVo,ObservableTransformer composer, final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().updatePhone(gson.toJson(mUserVo),composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResult model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }

    @Override
    public void login(Object obj,ObservableTransformer composer,final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().userLogin(gson.toJson(obj),composer,new Observer<HttpResultData<Login>>() {

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResultData<Login> model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }
    @Override
    public void forgetPwd(Object obj,ObservableTransformer composer,final ICallBackListener mICallBackListener) {
        // TODO
        HttpMethods.getInstance().forgetPassword(gson.toJson(obj),composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResult model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }

    @Override
    public void aboutUs(ObservableTransformer composer,final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().aboutUs(composer,new Observer<HttpResultData<AboutUs>>() {

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResultData<AboutUs> model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }
    @Override
    public void getAppversionList(ObservableTransformer composer,final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().getAppversionList(composer,new Observer<HttpResultData<AppVersion>>() {

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResultData<AppVersion> model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }
    @Override
    public void getBank(Object param,ObservableTransformer composer,final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().getBank(gson.toJson(param),composer,new Observer<HttpResultData<BankCardInfo>>() {

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResultData<BankCardInfo> model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }
    @Override
    public void addbank(Object param,ObservableTransformer composer,final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().addbank(gson.toJson(param),composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResult model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }

    @Override
    public void upload(Object obj,ObservableTransformer composer,final ICallBackListener mICallBackListener) {
        UploadBean uploadBean= (UploadBean) obj;
        File file= new File(uploadBean.getFilePath());
        if (file.exists()) {
            HttpMethods.getInstance().upload(file,uploadBean,composer,new Observer<HttpResultData<UploadData>>() {

                @Override
                public void onError(Throwable e) {
                    mICallBackListener.onFaild(e.getMessage());
                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onNext(HttpResultData<UploadData> model) {
                    mICallBackListener.onSuccess(model);
                }
            });
        }else {
            mICallBackListener.onFaild("文件不存在");
        }

    }
    @Override
    public void bankList(Object obj,ObservableTransformer composer,final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().bankList(gson.toJson(obj),composer,new Observer<HttpResultList<BankCardInfo>>() {

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResultList<BankCardInfo> model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }
}
