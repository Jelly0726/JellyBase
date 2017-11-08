package com.base.httpmvp.mode.business;

import com.alibaba.fastjson.JSON;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResultAll;

import java.util.List;

import rx.Subscriber;

/**
 * Created by Administrator on 2017/11/8.
 */

public class Business implements IBusiness {

    private static final String TAG = Business.class.getSimpleName();

    @Override
    public void register(Object mUserVo, final ICallBackListener mICallBackListener) {
        HttpMethods.getInstance().userRegistration(JSON.toJSON(mUserVo),new Subscriber<List<HttpResultAll>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mICallBackListener.onFaild(e.getMessage());
            }

            @Override
            public void onNext(List<HttpResultAll> model) {
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
}
