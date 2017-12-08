package com.base.httpmvp.presenter;

import com.base.httpmvp.databean.AppVersion;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResultData;
import com.base.httpmvp.view.IGetAppversionListView;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：检查版本View(activityview)对应的Presenter
 */
public class GetAppversionListPresenter implements IBasePresenter {

    private IGetAppversionListView interfaceView;

    public GetAppversionListPresenter(IGetAppversionListView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void execute(final boolean isRefresh, ObservableTransformer composer) {
        interfaceView.showProgress();
        HttpMethods.getInstance().getAppversionList(composer,new Observer<HttpResultData<AppVersion>>() {

            @Override
            public void onError(Throwable e) {
                interfaceView.closeProgress();
                interfaceView.getAppversionListFailed(isRefresh,e.getMessage());
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
                interfaceView.closeProgress();
                if (model.getStatus()== HttpCode.SUCCEED){
                    interfaceView.getAppversionListSuccess(isRefresh,model.getData());
                }else {
                    interfaceView.getAppversionListFailed(isRefresh,model.getMsg());
                }
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void detach() {

    }

    @Override
    public void addDisposable(Disposable subscription) {

    }

    @Override
    public void unDisposable() {

    }
}