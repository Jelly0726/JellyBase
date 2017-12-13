package com.base.httpmvp.view;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.view.View;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.mprogressdialog.MProgressUtil;
import com.base.view.BaseFragment;
import com.maning.mndialoglibrary.MProgressDialog;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Administrator on 2017/9/21.
 */

public abstract class BaseFragmentImpl<P extends IBasePresenter> extends BaseFragment
        implements LifecycleProvider<FragmentEvent> ,IBaseView {

    protected P presenter;
    private MProgressDialog progressDialog;
    private boolean isViewCreate = false;//view是否创建
    private boolean isViewVisible = false;//view是否可见
    private boolean isFirst = true;//是否第一次加载

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
        presenter = initPresenter();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
        isViewCreate = true;
        progressDialog = MProgressUtil.getInstance().getMProgressDialog(getActivity());
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isViewVisible = isVisibleToUser;
        if (isVisibleToUser && isViewCreate) {
            visibleToUser();
        }
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
        if (isViewVisible) {
            visibleToUser();
        }
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
            presenter.detach();
        }
        isViewCreate = false;
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        if (progressDialog!=null){
            progressDialog.dismiss();
            progressDialog=null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
    }
    /**
     * 懒加载
     * 让用户可见
     * 第一次加载
     */
    protected void firstLoad() {

    }

    /**
     * 懒加载
     * 让用户可见
     */
    protected void visibleToUser() {
        if (isFirst) {
            firstLoad();
            isFirst = false;
        }
    }
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
