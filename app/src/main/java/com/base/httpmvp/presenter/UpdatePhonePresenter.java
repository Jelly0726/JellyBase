package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.UpdatePhoneContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResult;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：修改手机号View(activityview)对应的Presenter
 */
public class UpdatePhonePresenter extends BasePresenterImpl<UpdatePhoneContact.View>
implements UpdatePhoneContact.Presenter{


    public UpdatePhonePresenter(UpdatePhoneContact.View interfaceView) {
        super(interfaceView);
    }

    public void updatePhone( ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().updatePhone(gson.toJson(view.getUpdatePhoneParam())
                ,composer,new Observer<HttpResult>() {

                    @Override
                    public void onError(Throwable e) {
                        view.closeProgress();
                        view.updatePhoneFailed(e.getMessage());
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
                            view.updatePhoneSuccess(model);
                        }else {
                            view.updatePhoneFailed(model.getMsg());
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