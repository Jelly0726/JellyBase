package com.jelly.mvp.presenter;

import com.base.httpmvp.retrofitapi.function.HttpFunctions;
import com.jelly.mvp.contact.PersonalInfoContact;
import com.base.httpmvp.mvpbase.BaseModel;
import com.base.httpmvp.mvpbase.ObserverResponseListener;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.IApiService;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.httpmvp.retrofitapi.token.GlobalToken;
import com.base.model.PersonalInfo;
import com.base.model.UploadBean;
import com.base.model.UploadData;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

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
                removeDisposable(this.hashCode());
            }

            @Override
            public void onFailure(String msg) {
                mView.findPersonalInfoFailed(msg);
                mView.closeProgress();
                removeDisposable(this.hashCode());
            }

            @Override
            public void onDisposable(Disposable disposable) {
                addDisposable(this.hashCode(), disposable);
            }
        });
    }

    @Override
    public void upload() {
        mView.showProgress();
        UploadBean uploadParm = (UploadBean) mView.getUpParam();
        File file = new File(uploadParm.getFilePath());
        if (file.exists()) {
            HttpMethods.getInstance().upload(file, uploadParm, mView.bindLifecycle(), new Observer<HttpResultData<UploadData>>() {

                @Override
                public void onError(Throwable e) {
                    mView.uploadFailed(e.getMessage());
                    mView.closeProgress();
                    removeDisposable(this.hashCode());
                }

                @Override
                public void onComplete() {
                }

                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    addDisposable(this.hashCode(),d);
                }

                @Override
                public void onNext(HttpResultData<UploadData> model) {
                    if (model.getStatus() == HttpCode.SUCCEED) {
                        mView.uploadSuccess(model.getData());
                    } else {
                        mView.uploadFailed(model.getMsg());
                    }
                    mView.closeProgress();
                    removeDisposable(this.hashCode());
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
                removeDisposable(this.hashCode());
            }

            @Override
            public void onFailure(String msg) {
                mView.personalInfoFailed(msg);
                mView.closeProgress();
                removeDisposable(this.hashCode());
            }

            @Override
            public void onDisposable(Disposable disposable) {
                addDisposable(this.hashCode(), disposable);
            }
        });
    }

    @Override
    public void start() {
        mModel = new BaseModel();
    }
}
