package com.base.httpmvp.mode.business;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.base.httpmvp.mode.databean.UploadBean;
import com.base.httpmvp.mode.databean.UploadData;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.retrofitapi.HttpResultData;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.httpmvp.retrofitapi.token.TokenModel;

import java.io.File;
import java.util.List;

import rx.Subscriber;

/**
 * Created by Administrator on 2017/11/8.
 */

public class Business implements IBusiness {

    private static final String TAG = Business.class.getSimpleName();
    //获取token
    @Override
    public void getToken(Object mUserVo) {
        HttpMethods.getInstance().getToken(JSON.toJSON(mUserVo)
                ,new Subscriber<HttpResultData<TokenModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

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
        HttpMethods.getInstance().userRegistration(JSON.toJSON(mUserVo),new Subscriber<List<HttpResult>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
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
            HttpMethods.getInstance().upload(file,uploadBean,new Subscriber<HttpResultData<UploadData>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    mICallBackListener.onFaild(e.getMessage());
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
}