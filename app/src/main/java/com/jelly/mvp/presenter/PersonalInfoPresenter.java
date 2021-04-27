package com.jelly.mvp.presenter;

import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.jelly.baselibrary.token.GlobalToken;
import com.jelly.baselibrary.model.PersonalInfo;
import com.jelly.baselibrary.model.UploadBean;
import com.jelly.mvp.contact.PersonalInfoContact;
import com.jelly.mvp.model.PersonalInfoModel;

import java.io.File;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/12/6.
 */

public class PersonalInfoPresenter extends PersonalInfoContact.Presenter {
    @Override
    public void getInfo() {
        mView.showProgress();
        Observable observable = HttpMethods.getInstance()
                .getProxy(IApiService.class)
                .findBuyerInfo(GlobalToken.getToken().getToken())
                .flatMap(new HttpFunctions<HttpResultData<PersonalInfo>>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResultData<PersonalInfo>>() {
            @Override
            public void onSuccess(HttpResultData<PersonalInfo> model) {
                mView.findPersonalInfoSuccess(model.getData());
                mView.closeProgress();
            }

            @Override
            public void onFailure(String msg) {
                mView.findPersonalInfoFailed(msg);
                mView.closeProgress();
            }
        });
    }

    @Override
    public void upload() {
        mView.showProgress();
        UploadBean uploadParm = (UploadBean) mView.getUpParam();
        File file = new File(uploadParm.getFilePath());
        if (file.exists()) {
            mModel.upload(file, uploadParm, mView.bindLifecycle(), new ObserverResponseListener<HttpResult>() {
                @Override
                public void onSuccess(HttpResult model) {
                    mView.uploadSuccess(model.getMsg());
                    mView.closeProgress();
                }

                @Override
                public void onFailure(String msg) {
                    mView.uploadFailed(msg);
                    mView.closeProgress();
                }
            });
        } else {
            mView.closeProgress();
            mView.uploadFailed("文件不存在");
        }
    }

    @Override
    public void upPersonalInfo() {
        mView.showProgress();
        Observable observable = HttpMethods.getInstance().getProxy(IApiService.class).updateBuyerInfo(GlobalToken.getToken().getToken(),
                mGson.toJson(mView.getPersonalInfoParam()))
                .flatMap(new HttpFunctions<HttpResult>());
        mModel.subscribe(observable, mView.bindLifecycle(), new ObserverResponseListener<HttpResult>() {
            @Override
            public void onSuccess(HttpResult model) {
                mView.personalInfoSuccess(model.getMsg());
                mView.closeProgress();
            }

            @Override
            public void onFailure(String msg) {
                mView.personalInfoFailed(msg);
                mView.closeProgress();
            }
        });
    }

    @Override
    public void start() {
        mModel = new PersonalInfoModel();
    }
}
