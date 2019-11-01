package com.base.view;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.base.SystemBar.StatusBarUtil;
import com.base.appManager.ExecutorManager;
import com.base.log.DebugLog;

import io.reactivex.annotations.Nullable;

/**
 * Created by Administrator on 2017/9/21.
 *  * Fragment基类，封装了懒加载的实现
 * 1、Viewpager + Fragment情况下，fragment的生命周期因Viewpager的缓存机制而失去了具体意义
 * 该抽象类自定义新的回调方法，当fragment可见状态改变时会触发的回调方法，和 Fragment 第一次可见时会回调的方法
 * @see #onFragmentVisibleChange(boolean)
 * @see #onFragmentFirstVisible()
 *
 * Fragment生命周期的执行情况：
 *
 * 启动Fragment：onAttach()-->onCreate()-->onCreateView()-->onViewCreated-->onActivityCreated()-->onStart()-->onResume()
 * 息屏状态：onPause()-->onStop()
 * 重新点亮屏幕：onStart()-->onResume()
 * 退出：onPause()-->onStop()-->onDestroyView()-->onDestroy()-->onDetach()
 * Fragment被回收又重新创建(横竖屏切换)：被回收执行onPause()-->onSaveInstanceState()-->onStop()
 * -->onDestroyView()-->onDestroy()-->onDetach()，
 * 重新创建执行onAttach()-->onCreate()-->onCreateView()-->onViewCreated-->onActivityCreated()-->onStart()-->onResume()；
 */
@hugo.weaving.DebugLog
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
    private boolean isKeyboard=false;//是否有外接键盘
    private boolean isDisable=true;//是否屏蔽软键盘
    public abstract void setData(String json);
    public BackInterface mBackInterface;

    /**
     * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
     * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
     * 如果没有Fragment消息时FragmentActivity自己才会消费该事件
     */
    public abstract boolean onBackPressed();

    /**
     * Fragment已经关联到Activity，这个时候 Activity已经传进来了， 获得Activity的传递的值就可以进行与activity的通信，
     * 当然也可以使用getActivity(),前提是Fragment已经和宿主Activity关联，并且没有脱离，有且只调用一次。
     * @param activity
     */
    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        DebugLog.i("SSSS","onAttach====="+this);
    }

    /**
     * 初始化Fragment。可通过参数savedInstanceState获取之前保存的值。
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
        DebugLog.i("SSSS","onCreate====="+this);
    }

    /**
     * 初始化Fragment的布局。加载布局和findViewById的操作通常在此函数内完成，但是不建议执行耗时的操作
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @android.support.annotation.Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
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
        DebugLog.i("SSSS","setUserVisibleHint====="+isVisibleToUser+"  "+this);
        DebugLog.i("SSSS","setUserVisibleHint rootView====="+rootView+"  "+this);
        if (rootView == null) {
            return;
        }
        iniSofia();
        DebugLog.i("SSSS","setUserVisibleHint isFirstVisible====="+isFirstVisible+"  "+this);
        if (isFirstVisible && isVisibleToUser) {
            this.onFragmentFirstVisible();
            isFirstVisible = false;
            DebugLog.i("SSSS","setUserVisibleHint isFirstVisible && isVisibleToUser=="+(isFirstVisible && isVisibleToUser)+"  "+this);
        }
        if (isVisibleToUser) {
            isFragmentVisible = true;
            this.onFragmentVisibleChange(true);
            DebugLog.i("SSSS","setUserVisibleHint isFragmentVisible====="+isFragmentVisible+"  "+this);
            return;
        }
        if (isFragmentVisible) {
            isFragmentVisible = false;
            this.onFragmentVisibleChange(false);
            DebugLog.i("SSSS","setUserVisibleHint isFragmentVisible="+isFragmentVisible+"  "+this);
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
    /**
     * UsbManager检测是否为键盘
     */
    private void detectUsbAudioDevice() {
        ExecutorManager.getInstance().getSingleThread().execute(new Runnable() {
            @Override
            public void run() {
                isKeyboard=false;
                //第二种 通过InputManager获取
                InputManager inputManager = (InputManager)getActivity().getSystemService(Context.INPUT_SERVICE);
                //我们可以通过InputManager获取到当前的所有设备的DeviceId
                int[] inputDeviceIds= inputManager.getInputDeviceIds();
                for (int inputDeviceId : inputDeviceIds) {
                    InputDevice inputDevice = inputManager.getInputDevice(inputDeviceId);
                    if (inputDevice==null)continue;
                    //KEYBOARD_TYPE_ALPHABETIC 有字母的键盘  KEYBOARD_TYPE_NONE 没有键盘  KEYBOARD_TYPE_NON_ALPHABETIC 没有字母的键盘
                    if (inputDevice.getKeyboardType()==InputDevice.KEYBOARD_TYPE_ALPHABETIC){
                        isKeyboard=true;
                        break;
                    }
                }
                if (isKeyboard&&isDisable) {
                    //在BaseActivity里禁用软键盘
                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                }else {
                    //在需要打开的Activity取消禁用软键盘
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                }
            }
        });
    }
    /**
     * 是否禁用软键盘
     * @param disable
     */
    public void setDisable(boolean disable) {
        this.isDisable=disable;
        if (disable) {
            //在BaseActivity里禁用软键盘
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }else {
            //在需要打开的Activity取消禁用软键盘
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
    }

    /**
     * onViewCreated是在onCreateView后被触发的事件
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //如果setUserVisibleHint()在rootView创建前调用时，那么
        //就等到rootView创建完后才回调onFragmentVisibleChange(true)
        //保证onFragmentVisibleChange()的回调发生在rootView创建完成之后，以便支持ui操作
        DebugLog.i("SSSS","onViewCreated====="+this+"  rootView="+rootView);
        if (rootView == null) {
            rootView = view;
        }
        super.onViewCreated(isReuseView && rootView != null ? rootView : view, savedInstanceState);
    }

    /**
     * 执行该方法时，与Fragment绑定的Activity的onCreate方法已经执行完成并返回，在该方法内可以进行与Activity交互的UI操作，
     * 所以在该方法之前Activity的onCreate方法并未执行完成，如果提前进行交互操作，会引发空指针异常。
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DebugLog.i("SSSS","onActivityCreated====="+this+"  rootView="+rootView);
        detectUsbAudioDevice();
    }

    /**
     * 启动Fragement 启动时回调,，此时Fragement可见
     */
    @Override
    public void onStart() {
        super.onStart();
        //告诉FragmentActivity，当前Fragment在栈顶
        if (mBackInterface!=null)
            mBackInterface.setSelectedFragment(this);
        DebugLog.i("SSSS","onStart====="+this+"  getUserVisibleHint()="+getUserVisibleHint());
        if (getUserVisibleHint()) {
            DebugLog.i("SSSS","onStart====="+this+"  isFirstVisible()="+isFirstVisible);
            if (isFirstVisible) {
                this.onFragmentFirstVisible();
                isFirstVisible = false;
                isFragmentVisible = true;
            }else {
                isFragmentVisible = true;
                this.onFragmentVisibleChange(true);
            }
        }
    }

    /**
     * Fragment处于活动状态，用户可与之交互。
     */
    @Override
    public void onResume() {
        super.onResume();
        DebugLog.i("SSSS","onResume====="+this);
    }

    /**
     * Fragment处于暂停状态，但依然可见，用户不能与之交互。
     */
    @Override
    public void onPause() {
        super.onPause();
        DebugLog.i("SSSS","onPause====="+this);
    }

    /**
     * Fragment完全不可见。
     */
    @Override
    public void onStop() {
        super.onStop();
        DebugLog.i("SSSS","onStop====="+this);
    }

    /**
     * 销毁与Fragment有关的视图，但未与Activity解除绑定，依然可以通过onCreateView方法重新创建视图
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DebugLog.i("SSSS","onDestroyView====="+this);
    }

    /**
     * 销毁Fragment对象。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        initVariable();
        DebugLog.i("SSSS","onDestroy====="+this);
    }

    /**
     * Fragment和Activity解除关联的时候调用。
     */
    @Override
    public void onDetach() {
        super.onDetach();
        DebugLog.i("SSSS","onDetach====="+this);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
    private void iniSofia(){
        if (!StatusBarUtil.setStatusBarDarkTheme(getActivity(), true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(getActivity(),0x55000000);
        }
    }
}
