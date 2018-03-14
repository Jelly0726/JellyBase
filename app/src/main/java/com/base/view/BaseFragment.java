package com.base.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.annotations.Nullable;

/**
 * Created by Administrator on 2017/9/21.
 *  * Fragment基类，封装了懒加载的实现
 * 1、Viewpager + Fragment情况下，fragment的生命周期因Viewpager的缓存机制而失去了具体意义
 * 该抽象类自定义新的回调方法，当fragment可见状态改变时会触发的回调方法，和 Fragment 第一次可见时会回调的方法
 * @see #onFragmentVisibleChange(boolean)
 * @see #onFragmentFirstVisible()
 */

public abstract class BaseFragment extends Fragment{
    protected View rootView;
    //是否可见
    private boolean isFragmentVisible;
    //是否复用view
    private boolean isReuseView=true;
    //是否第一次可见
    private boolean isFirstVisible;
    //是否连接过服务器
    private boolean isConnected=false;
    public abstract void setData(String json);
    public BackInterface mBackInterface;

    /**
     * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
     * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
     * 如果没有Fragment消息时FragmentActivity自己才会消费该事件
     */
    public abstract boolean onBackPressed();
    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        Log.i("SSSS","onAttach====="+this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
        Log.i("SSSS","onCreate====="+this);
    }
    //setUserVisibleHint()在Fragment创建时会先被调用一次，传入isVisibleToUser = false
    //如果当前Fragment可见，那么setUserVisibleHint()会再次被调用一次，传入isVisibleToUser = true
    //如果Fragment从可见->不可见，那么setUserVisibleHint()也会被调用，传入isVisibleToUser = false
    //总结：setUserVisibleHint()除了Fragment的可见状态发生变化时会被回调外，在new Fragment()时也会被回调
    //如果我们需要在 Fragment 可见与不可见时干点事，用这个的话就会有多余的回调了，那么就需要重新封装一个
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //setUserVisibleHint()有可能在fragment的生命周期外被调用
        Log.i("SSSS","setUserVisibleHint====="+isVisibleToUser+"  "+this);
        Log.i("SSSS","setUserVisibleHint rootView====="+rootView+"  "+this);
        if (rootView == null) {
            return;
        }
        Log.i("SSSS","setUserVisibleHint isFirstVisible====="+isFirstVisible+"  "+this);
        if (isFirstVisible && isVisibleToUser) {
            this.onFragmentFirstVisible();
            isFirstVisible = false;
            Log.i("SSSS","setUserVisibleHint isFirstVisible && isVisibleToUser=="+(isFirstVisible && isVisibleToUser)+"  "+this);
        }
        if (isVisibleToUser) {
            isFragmentVisible = true;
            this.onFragmentVisibleChange(true);
            Log.i("SSSS","setUserVisibleHint isFragmentVisible====="+isFragmentVisible+"  "+this);
            return;
        }
        if (isFragmentVisible) {
            isFragmentVisible = false;
            this.onFragmentVisibleChange(false);
            Log.i("SSSS","setUserVisibleHint isFragmentVisible="+isFragmentVisible+"  "+this);
        }
    }
    private void initVariable() {
        isFirstVisible = true;
        isFragmentVisible = false;
        isReuseView = true;
        rootView=null;
    }

    /**
     * 设置是否使用 view 的复用，默认开启
     * view 的复用是指，ViewPager 在销毁和重建 Fragment 时会不断调用 onCreateView() -> onDestroyView()
     * 之间的生命函数，这样可能会出现重复创建 view 的情况，导致界面上显示多个相同的 Fragment
     * view 的复用其实就是指保存第一次创建的 view，后面再 onCreateView() 时直接返回第一次创建的 view
     *
     * @param isReuse
     */
    protected void reuseView(boolean isReuse) {
        isReuseView = isReuse;
    }

    /**
     * 去除setUserVisibleHint()多余的回调场景，保证只有当fragment可见状态发生变化时才回调
     * 回调时机在view创建完后，所以支持ui操作，解决在setUserVisibleHint()里进行ui操作有可能报null异常的问题
     *
     * 可在该回调方法里进行一些ui显示与隐藏，比如加载框的显示和隐藏
     *
     * @param isVisible true  不可见 -> 可见
     *                  false 可见  -> 不可见
     */
    public abstract void onFragmentVisibleChange(boolean isVisible);
    /**
     * 在fragment首次可见时回调，可在这里进行加载数据，保证只在第一次打开Fragment时才会加载数据，
     * 这样就可以防止每次进入都重复加载数据
     * 该方法会在 onFragmentVisibleChange() 之前调用，所以第一次打开时，可以用一个全局变量表示数据下载状态，
     * 然后在该方法内将状态设置为下载状态，接着去执行下载的任务
     * 最后在 onFragmentVisibleChange() 里根据数据下载状态来控制下载进度ui控件的显示与隐藏
     */
    public abstract void onFragmentFirstVisible();
    public boolean isFragmentVisible() {
        return isFragmentVisible;
    }
    public boolean isReuseView() {
        return isReuseView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //如果setUserVisibleHint()在rootView创建前调用时，那么
        //就等到rootView创建完后才回调onFragmentVisibleChange(true)
        //保证onFragmentVisibleChange()的回调发生在rootView创建完成之后，以便支持ui操作
        if (rootView == null) {
            rootView = view;
            if (getUserVisibleHint()) {
                if (isFirstVisible) {
                    this.onFragmentFirstVisible();
                    isFirstVisible = false;
                }
                isFragmentVisible = true;
                this.onFragmentVisibleChange(true);
            }
        }
        Log.i("SSSS","onViewCreated====="+this+"  rootView="+rootView);
        super.onViewCreated(isReuseView && rootView != null ? rootView : view, savedInstanceState);
    }

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @android.support.annotation.Nullable Bundle savedInstanceState) {
        Log.i("SSSS","onCreateView====="+this+"  rootView="+rootView);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("SSSS","onActivityCreated====="+this+"  rootView="+rootView);
    }

    @Override
    public void onStart() {
        super.onStart();
        //告诉FragmentActivity，当前Fragment在栈顶
        if (mBackInterface!=null)
            mBackInterface.setSelectedFragment(this);
        Log.i("SSSS","onStart====="+this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("SSSS","onResume====="+this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("SSSS","onPause====="+this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("SSSS","onStop====="+this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("SSSS","onDestroyView====="+this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        initVariable();
        Log.i("SSSS","onDestroy====="+this);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("SSSS","onDetach====="+this);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
