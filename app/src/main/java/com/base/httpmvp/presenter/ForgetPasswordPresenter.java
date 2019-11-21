package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.ForgetPwdContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：忘记密码View(activityview)对应的Presenter
 */
public class ForgetPasswordPresenter extends BasePresenterImpl<ForgetPwdContact.View>
implements ForgetPwdContact.Presenter{


    public ForgetPasswordPresenter(ForgetPwdContact.View interfaceView) {
        super(interfaceView);
    }

    public void forgetPwd(ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().forgetPassword(gson.toJson(view.forgetPasswordParam()),composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.forgetPasswordFailed(e.getMessage());
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
                    view.forgetPasswordSuccess(model.getMsg());
                }else {
                    view.forgetPasswordFailed(model.getMsg());
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