package com.base.httpmvp.mvpView;

import android.os.Bundle;
import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.base.httpmvp.mvpbase.BasePresenter;
import com.base.httpmvp.mvpbase.IBaseView;
import com.jelly.baselibrary.BaseFragment;
import com.jelly.baselibrary.mprogressdialog.MProgressUtil;
import com.maning.mndialoglibrary.listeners.OnDialogDismissListener;
import com.trello.rxlifecycle3.android.FragmentEvent;

import io.reactivex.annotations.Nullable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Administrator on 2017/9/21.
 */

public abstract class BaseFragmentImpl<V extends IBaseView,P extends BasePresenter ,VB extends ViewBinding> extends BaseFragment<VB>
        implements IBaseView {

    protected P presenter;
    private V mView;
    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
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
                //此处是统一的加载动画弹窗 在这边取消会导致本页面所有请求都取消
//                if (presenter!=null) {
//                    presenter.unDisposable();
//                }
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
