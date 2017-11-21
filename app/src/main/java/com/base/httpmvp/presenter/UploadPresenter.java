package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.ICallBackListener;
import com.base.httpmvp.mode.databean.UploadData;
import com.base.httpmvp.retrofitapi.HttpCode;
import com.base.httpmvp.retrofitapi.HttpResultData;
import com.base.httpmvp.view.IUploadView;

import java.util.List;

/**
 * Created by Administrator on 2017/11/8.
 * 说明：上传文件View(activityview)对应的Presenter
 */
public class UploadPresenter implements IBasePresenter {

    private IUploadView interfaceView;

    public UploadPresenter(IUploadView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void upload() {
        interfaceView.showProgress();
        mIBusiness.upload(interfaceView.getUpParam(), new ICallBackListener() {
            @Override
            public void onSuccess(final Object mCallBackVo) {
                interfaceView.closeProgress();
                HttpResultData httpResultAll= (HttpResultData)((List)mCallBackVo).get(0);
                if (httpResultAll.getStatus()== HttpCode.SUCCEED){
                    UploadData model= (UploadData) httpResultAll.getData();
                    interfaceView.uploadSuccess(true,model);
                }else {
                    interfaceView.uploadFailed(true,httpResultAll.getMsg());
                }

            }

            @Override
            public void onFaild(final String message) {
                interfaceView.closeProgress();
                interfaceView.uploadFailed(true,message);
            }
        });
    }
}