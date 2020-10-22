package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.PersonalInfoContact;
import com.base.model.PersonalInfo;
import com.base.model.UploadBean;
import com.base.model.UploadData;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;

import java.io.File;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/12/6.
 */

public class PersonalInfoPresenter extends BasePresenterImpl<PersonalInfoContact.View>
        implements PersonalInfoContact.Presenter{
    public PersonalInfoPresenter(PersonalInfoContact.View view){
        super(view);

    }
    @Override
    public void getInfo(ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().findBuyerInfo(composer, new Observer<HttpResultData<PersonalInfo>>() {
            @Override
            public void onSubscribe(Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResultData<PersonalInfo> personalInfoHttpResultData) {
                if (personalInfoHttpResultData.getStatus()== HttpCode.SUCCEED){
                    view.findPersonalInfoSuccess(personalInfoHttpResultData.getData());
                }else {
                    view.findPersonalInfoFailed(personalInfoHttpResultData.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.findPersonalInfoFailed(e.getMessage());
            }

            @Override
            public void onComplete() {
                view.closeProgress();
            }
        });
    }

    @Override
    public void upload(ObservableTransformer composer) {
        view.showProgress();
        UploadBean uploadParm = (UploadBean) view.getUpParam();
        File file= new File(uploadParm.getFilePath());
        if (file.exists()) {
            HttpMethods.getInstance().upload(file,uploadParm,composer,new Observer<HttpResultData<UploadData>>() {

                @Override
                public void onError(Throwable e) {
                    view.closeProgress();
                    view.uploadFailed(e.getMessage());
                }

                @Override
                public void onComplete() {
                    view.closeProgress();
                }

                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    addDisposable(d);
                }

                @Override
                public void onNext(HttpResultData<UploadData> model) {
                    view.closeProgress();
                    if (model.getStatus()== HttpCode.SUCCEED){
                        view.uploadSuccess(model.getData());
                    }else {
                        view.uploadFailed(model.getMsg());
                    }
                }
            });
        }else {
            view.closeProgress();
            view.uploadFailed("文件不存在");
        }
    }

    @Override
    public void upPersonalInfo(ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().updateBuyerInfo(gson.toJson(view.getPersonalInfoParam())
                , composer, new Observer<HttpResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        view.closeProgress();
                        if (httpResult.getStatus()== HttpCode.SUCCEED){
                            view.personalInfoSuccess(httpResult.getMsg());
                        }else {
                            view.personalInfoFailed(httpResult.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.closeProgress();
                        view.personalInfoFailed(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.closeProgress();
                    }
                });
    }
}
