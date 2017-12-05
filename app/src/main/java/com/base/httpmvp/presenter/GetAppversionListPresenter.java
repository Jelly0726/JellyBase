package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.mode.databean.AppVersion;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResultData;
import com.base.httpmvp.view.IGetAppversionListView;

import io.reactivex.ObservableTransformer;
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
        mIBusiness.getAppversionList(composer,new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                interfaceView.closeProgress();
                HttpResultData<AppVersion> httpResultAll= (HttpResultData)mCallBackVo;
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    interfaceView.getAppversionListSuccess(isRefresh,httpResultAll.getData());
                }else {
                    interfaceView.getAppversionListFailed(isRefresh,httpResultAll.getMsg());
                }
            }

            @Override
            public void onFaild(final String message) {
                interfaceView.closeProgress();
                interfaceView.getAppversionListFailed(isRefresh,message);
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