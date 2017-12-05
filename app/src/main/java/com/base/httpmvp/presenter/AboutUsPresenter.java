package com.base.httpmvp.presenter;

import com.base.httpmvp.contact.AboutContact;
import com.base.httpmvp.databean.AboutUs;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResultData;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：关于我们View(activityview)对应的Presenter
 */
public class AboutUsPresenter extends BasePresenterImpl<AboutContact.View> implements AboutContact.Presenter {


    public AboutUsPresenter(AboutContact.View interfaceView) {
        super(interfaceView);
    }

    public void aboutUs(final boolean isRefresh, ObservableTransformer composer) {
        view.showProgress();
        HttpMethods.getInstance().aboutUs(composer,new Observer<HttpResultData<AboutUs>>() {

            @Override
            public void onError(Throwable e) {
                view.closeProgress();
                view.aboutUsFailed(isRefresh,e.getMessage());
            }

            @Override
            public void onComplete() {
                view.closeProgress();
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(HttpResultData<AboutUs> model) {
                view.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    view.aboutUsSuccess(isRefresh,model.getData());
                }else {
                    view.aboutUsFailed(isRefresh,model.getMsg());
                }
            }
        });
    }
}