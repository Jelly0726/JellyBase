package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.UpdataPwdContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResult;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：修改密码View(activityview)对应的Presenter
 */
public class UpdatePasswordPresenter extends BasePresenterImpl<UpdataPwdContact.View>
        implements UpdataPwdContact.Presenter {


    public UpdatePasswordPresenter(UpdataPwdContact.View interfaceView) {
        super(interfaceView);
    }

    public void updatePassword(ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().updatePassword(gson.toJson(view.getUpdatePasswordParam())
                ,composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.updatePasswordFailed(e.getMessage());
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
                    view.updatePasswordSuccess(model.getMsg());
                }else {
                    view.updatePasswordFailed(model.getMsg());
                }
            }
        });
    }
}