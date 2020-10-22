package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.SettingContact;
import com.base.model.AppVersion;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.methods.HttpResultData;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/12/14.
 */

public class SettingPresenter extends BasePresenterImpl<SettingContact.View>
implements SettingContact.Presenter{
    public SettingPresenter(SettingContact.View view){
        super(view);
    }
    @Override
    public void getAppversion(ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().getAppversionList(composer,new Observer<HttpResultData<AppVersion>>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.getAppversionFailed(e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(HttpResultData<AppVersion> model) {
                view.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    view.getAppversionSuccess(model.getMsg());
                }else {
                    view.getAppversionFailed(model.getMsg());
                }
            }
        });
    }
}
