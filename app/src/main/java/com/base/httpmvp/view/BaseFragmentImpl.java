package com.base.httpmvp.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CheckResult;

import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;
import com.base.mprogressdialog.MProgressUtil;
import com.base.view.BaseFragment;
import com.maning.mndialoglibrary.listeners.OnDialogDismissListener;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.RxLifecycle;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.trello.rxlifecycle3.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Administrator on 2017/9/21.
 */

public abstract class BaseFragmentImpl<V extends IBaseView,P extends BasePresenter<V>> extends BaseFragment
        implements LifecycleProvider<FragmentEvent> ,IBaseView {

    protected P presenter;
    private V mView;
    public LifecycleProvider lifecycleProvider;
    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    @NonNull
    @CheckResult
    public final Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject);
    }

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
        lifecycleProvider=this;
        if (presenter == null) {
            presenter = initPresenter();
        }
        if (mView == null) {
            mView = initIBView();
        }
        if (presenter != null && mView != null) {
            presenter.attachView(mView);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
        MProgressUtil.getInstance().initialize(getActivity().getApplicationContext());
        MProgressUtil.getInstance().setDismissListener(new OnDialogDismissListener() {
            @Override
            public void onDismiss() {
                if (presenter != null) {
                    presenter.unDisposable();
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (presenter != null) {
            presenter.detachView();
        }
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        MProgressUtil.getInstance().dismiss();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        presenter=null;
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
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
        MProgressUtil.getInstance().show(getActivity());
    }

    @Override
    public void closeProgress() {
        MProgressUtil.getInstance().dismiss();
    }
}
