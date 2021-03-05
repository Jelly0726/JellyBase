package com.base.httpmvp.mvpView;

import android.os.Bundle;

import androidx.annotation.CheckResult;

import com.base.appManager.BaseApplication;
import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;
import com.base.mprogressdialog.MProgressUtil;
import com.base.view.BaseActivity;
import com.maning.mndialoglibrary.listeners.OnDialogDismissListener;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.RxLifecycle;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Administrator on 2016/3/14.
 */
public abstract class BaseActivityImpl<V extends IBaseView, P extends BasePresenter> extends BaseActivity
        implements LifecycleProvider<ActivityEvent>, IBaseView {
    public LifecycleProvider lifecycleProvider;
    public P presenter;
    private V mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        lifecycleProvider = this;
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

    @Override
    @NonNull
    @CheckResult
    public final Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }

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
