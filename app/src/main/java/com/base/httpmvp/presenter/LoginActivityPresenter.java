package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.LoginContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResult;
import com.base.httpmvp.retrofitapi.HttpResultData;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import systemdb.Login;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：登录View(activityview)对应的Presenter
 */
public class LoginActivityPresenter extends BasePresenterImpl<LoginContact.View>
implements LoginContact.Presenter{


    public LoginActivityPresenter(LoginContact.View interfaceView) {
        super(interfaceView);
    }

    public void userLogin(ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().userLogin(gson.toJson(view.getLoginParam()),composer,new Observer<HttpResultData<Login>>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.loginFailed(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResultData<Login> model) {
                view.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    view.loginSuccess(model.getData());
                }else {
                    view.loginFailed(model.getMsg());
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