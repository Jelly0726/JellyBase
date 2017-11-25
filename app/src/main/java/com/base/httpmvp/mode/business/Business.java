package com.base.httpmvp.mode.business;

import android.text.TextUtils;

import com.base.bankcard.BankCardInfo;
import com.base.httpmvp.mode.databean.UploadBean;
import com.base.httpmvp.mode.databean.UploadData;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.retrofitapi.HttpResultData;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.httpmvp.retrofitapi.token.TokenModel;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 */

public class Business implements IBusiness {
    private static Gson gson= new Gson();
    private static final String TAG = Business.class.getSimpleName();
    //获取token
    @Override
    public void getToken(Object mUserVo) {
        HttpMethods.getInstance().getToken(gson.toJson(mUserVo)
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
    public void register(Object mUserVo, final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().userRegistration(gson.toJson(mUserVo),new Observer<List<HttpResult>>() {

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
            public void onNext(List<HttpResult> model) {
                mICallBackListener.onSuccess(model);
            }
        });
    }

    @Override
    public void login(Object obj, ICallBackListener mICallBackListener) {
    }

    @Override
    public void forgetPwd(Object obj, ICallBackListener mICallBackListener) {
        // TODO
    }

    @Override
    public void feedBack(Object obj, ICallBackListener mICallBackListener) {
        // TODO
    }
    @Override
    public void upload(Object obj,final ICallBackListener mICallBackListener) {
        UploadBean uploadBean= (UploadBean) obj;
        File file= new File(uploadBean.getFilePath());
        if (file.exists()) {
            HttpMethods.getInstance().upload(file,uploadBean,new Observer<HttpResultData<UploadData>>() {

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
    public void getBank(Object param,final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().getBank(gson.toJson(param),new Observer<HttpResultData<BankCardInfo>>() {


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
}
