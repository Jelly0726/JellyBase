package com.base.httpmvp.mvpView;

import android.os.Bundle;

import androidx.viewbinding.ViewBinding;

import com.base.BaseApplication;
import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.mprogressdialog.MProgressUtil;
import com.maning.mndialoglibrary.listeners.OnDialogDismissListener;
import com.trello.rxlifecycle3.android.ActivityEvent;

import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Administrator on 2016/3/14.
 */
public abstract class BaseActivityImpl<V extends IBaseView, P extends BasePresenter
        ,VB extends ViewBinding> extends BaseActivity<VB>
        implements  IBaseView {
    public P presenter;
    private V mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        if (presenter == null) {
            presenter = initPresenter();
        }
        if (mView == null) {
            mView = initIBView();
        }
        if (presenter != null && mView != null) {
            presenter.attachView(mView);
        }
        MProgressUtil.getInstance().initialize(BaseApplication.getInstance());
        MProgressUtil.getInstance().setDismissListener(new OnDialogDismissListener() {
            @Override
            public void onDismiss() {
                //此处是统一的加载动画弹窗 在这边取消会导致本页面所有请求都取消
//                if (presenter!=null) {
//                    presenter.unDisposable();
//                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        if (presenter != null) {
            presenter.detachView();//在presenter中解绑释放view
            presenter = null;
        }
        MProgressUtil.getInstance().dismiss();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();



    /**
     * 在子类中初始化对应的presenter
     *
     * @return 相应的presenter
     */
    public abstract P initPresenter();

    /**
     * 在子类中初始化对应的View
     *
     * @return 相应的View
     */
    public abstract V initIBView();

    @Override
    public void showProgress() {
        MProgressUtil.getInstance().show(this);
    }

    @Override
    public void closeProgress() {
        MProgressUtil.getInstance().dismiss();
    }
}
