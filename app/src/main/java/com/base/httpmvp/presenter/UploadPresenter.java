package com.base.httpmvp.presenter;

import com.base.httpmvp.databean.UploadBean;
import com.base.httpmvp.databean.UploadData;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpMethods;
import com.base.httpmvp.retrofitapi.HttpResultData;
import com.base.httpmvp.view.IUploadView;

import java.io.File;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：上传文件View(activityview)对应的Presenter
 */
public class UploadPresenter implements IBasePresenter {

    private IUploadView interfaceView;

    public UploadPresenter(IUploadView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void upload(ObservableTransformer composer) {
        interfaceView.showProgress();
        UploadBean uploadBean= (UploadBean) interfaceView.getUpParam();
        File file= new File(uploadBean.getFilePath());
        if (file.exists()) {
            HttpMethods.getInstance().upload(file,uploadBean,composer,new Observer<HttpResultData<UploadData>>() {

                @Override
                public void onError(Throwable e) {
                    interfaceView.closeProgress();
                    interfaceView.uploadFailed(true,e.getMessage());
                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onNext(HttpResultData<UploadData> model) {
                    interfaceView.closeProgress();
                    if (model.getStatus()== HttpCode.SUCCEED){
                        interfaceView.uploadSuccess(true,model.getData());
                    }else {
                        interfaceView.uploadFailed(true,model.getMsg());
                    }
                }
            });
        }else {
            interfaceView.uploadFailed(true,"文件不存在");
        }
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