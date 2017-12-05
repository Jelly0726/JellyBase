package com.base.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.trello.rxlifecycle2.LifecycleProvider;

import io.reactivex.annotations.Nullable;

/**
 * Created by Administrator on 2017/9/21.
 */

public abstract class BaseFragment extends Fragment{
    public abstract void setData(String json);
    public LifecycleProvider lifecycleProvider;

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
