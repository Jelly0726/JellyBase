package com.base.httpmvp.view;

import android.os.Bundle;
import android.support.annotation.CheckResult;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.mprogressdialog.MProgressUtil;
import com.base.view.BaseActivity;
import com.maning.mndialoglibrary.MProgressDialog;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Administrator on 2016/3/14.
 */
public abstract class BaseActivityImpl<P extends IBasePresenter> extends BaseActivity
        implements LifecycleProvider<ActivityEvent>,IBaseView {
    public LifecycleProvider lifecycleProvider;
    public P presenter;
    private MProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        lifecycleProvider=this;
        progressDialog = MProgressUtil.getInstance().getMProgressDialog(this);
        presenter = initPresenter();
    }
    @Override
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        if (presenter != null) {
            presenter.detach();//在presenter中解绑释放view
            presenter = null;
        }
        if (progressDialog!=null){
            progressDialog.dismiss();
            progressDialog=null;
        }
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
    @Override
    public void showProgress() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    @Override
    public void closeProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
