package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.SetPwdContact;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResult;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：设置密码View(activityview)对应的Presenter
 */
public class SetPassWordActivityPresenter extends BasePresenterImpl<SetPwdContact.View>
implements SetPwdContact.Presenter{


    public SetPassWordActivityPresenter(SetPwdContact.View interfaceView) {
        super(interfaceView);
    }

    @Override
    public void setPassword(final boolean isRefresh, ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().setPassWord(gson.toJson(view.getSetPassWordParam()),
                composer,new Observer<HttpResult>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.excuteFailed(isRefresh,e.getMessage());
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
                    view.excuteSuccess(isRefresh,model.getMsg());
                }else {
                    view.excuteFailed(isRefresh, model.getMsg());
                }
            }
        });
    }
}