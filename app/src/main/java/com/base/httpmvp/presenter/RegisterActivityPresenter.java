package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.RegisterContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：注册View(activityview)对应的Presenter
 */
public class RegisterActivityPresenter extends BasePresenterImpl<RegisterContact.View>
implements RegisterContact.Presenter{


    public RegisterActivityPresenter(RegisterContact.View interfaceView) {
        super(interfaceView);
    }

    public void userRegister( ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().userRegistration(gson.toJson(view.getRegParam()),composer
                ,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.excuteFailed(e.getMessage());
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
            public void onNext(HttpResult model) {
                view.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    view.excuteSuccess(model);
                }else {
                    view.excuteFailed(model.getMsg());
                }

            }
        });
    }
    public void getVerifiCode(ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().getVerifiCode(gson.toJson(view.getVerifiCodeParam())
                ,composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.verifiCodeFailed(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResult model) {
                view.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    view.verifiCodeSuccess(model);
                }else {
                    view.verifiCodeFailed(model.getMsg());
                }
            }
        });
    }
}