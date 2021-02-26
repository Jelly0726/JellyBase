package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.PersonalInfoContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;
import com.base.model.PersonalInfo;
import com.base.model.UploadBean;
import com.base.model.UploadData;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/12/6.
 */

public class PersonalInfoPresenter extends PersonalInfoContact.Presenter{
    @Override
    public void getInfo() {
        mView.showProgress();
        HttpMethods.getInstance().findBuyerInfo(mView.bindLifecycle(), new Observer<HttpResultData<PersonalInfo>>() {
            @Override
            public void onSubscribe(Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResultData<PersonalInfo> personalInfoHttpResultData) {
                if (personalInfoHttpResultData.getStatus()== HttpCode.SUCCEED){
                    mView.findPersonalInfoSuccess(personalInfoHttpResultData.getData());
                }else {
                    mView.findPersonalInfoFailed(personalInfoHttpResultData.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                mView.closeProgress();
                mView.findPersonalInfoFailed(e.getMessage());
            }

            @Override
            public void onComplete() {
                mView.closeProgress();
            }
        });
    }

    @Override
    public void upload() {
        mView.showProgress();
        UploadBean uploadParm = (UploadBean) mView.getUpParam();
        File file= new File(uploadParm.getFilePath());
        if (file.exists()) {
            HttpMethods.getInstance().upload(file,uploadParm, mView.bindLifecycle(),new Observer<HttpResultData<UploadData>>() {

                @Override
                public void onError(Throwable e) {
                    mView.closeProgress();
                    mView.uploadFailed(e.getMessage());
                }

                @Override
                public void onComplete() {
                    mView.closeProgress();
                }

                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    addDisposable(d);
                }

                @Override
                public void onNext(HttpResultData<UploadData> model) {
                    mView.closeProgress();
                    if (model.getStatus()== HttpCode.SUCCEED){
                        mView.uploadSuccess(model.getData());
                    }else {
                        mView.uploadFailed(model.getMsg());
                    }
                }
            });
        }else {
            mView.closeProgress();
            mView.uploadFailed("文件不存在");
        }
    }

    @Override
    public void upPersonalInfo() {
        mView.showProgress();
        HttpMethods.getInstance().updateBuyerInfo(mGson.toJson(mView.getPersonalInfoParam())
                , mView.bindLifecycle(), new Observer<HttpResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        mView.closeProgress();
                        if (httpResult.getStatus()== HttpCode.SUCCEED){
                            mView.personalInfoSuccess(httpResult.getMsg());
                        }else {
                            mView.personalInfoFailed(httpResult.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.closeProgress();
                        mView.personalInfoFailed(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mView.closeProgress();
                    }
                });
    }
}
