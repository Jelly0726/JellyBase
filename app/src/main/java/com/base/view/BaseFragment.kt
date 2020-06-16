package com.base.view

import android.app.Activity
import android.content.Context
import android.hardware.input.InputManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.base.SystemBar.StatusBarUtil
import com.base.appManager.ExecutorManager
import hugo.weaving.DebugLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 * Created by Jelly on 2017/9/21.
 * * Fragment基类，封装了懒加载的实现
 * 1、Viewpager + Fragment情况下，fragment的生命周期因Viewpager的缓存机制而失去了具体意义
 * 该抽象类自定义新的回调方法，当fragment可见状态改变时会触发的回调方法，和 Fragment 第一次可见时会回调的方法
 * @see .onFragmentVisibleChange
 * @see .onFragmentFirstVisible
 */
@DebugLog
abstract class BaseFragment : Fragment(), CoroutineScope by MainScope() {
    @JvmField
    protected var rootView: View? = null
    //是否可见
    var isFragmentVisible = false
        private set
    //是否复用view
    var isReuseView = true
        private set
    //是否第一次可见
    private var isFirstVisible = false
    //是否连接过服务器
    var isConnected = false
    private var isKeyboard = false //是否有外接键盘
    private var isDisable = true //是否屏蔽软键盘
    abstract fun setData(json: String?)
    var mBackInterface: BackInterface? = null
    /**
     * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
     * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
     * 如果没有Fragment消息时FragmentActivity自己才会消费该事件
     */
    abstract fun onBackPressed(): Boolean

    /**
     * Fragment已经关联到Activity，这个时候 Activity已经传进来了， 获得Activity的传递的值就可以进行与activity的通信，
     * 当然也可以使用getActivity(),前提是Fragment已经和宿主Activity关联，并且没有脱离，有且只调用一次。
     * @param activity
     */
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        com.base.log.DebugLog.i("SSSS", "onAttach=====$this")
    }

    /**
     * 初始化Fragment。可通过参数savedInstanceState获取之前保存的值。
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVariable()
        com.base.log.DebugLog.i("SSSS", "onCreate=====$this")
    }

    /**
     * 初始化Fragment的布局。加载布局和findViewById的操作通常在此函数内完成，但是不建议执行耗时的操作
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    //setUserVisibleHint()在Fragment创建时会先被调用一次，传入isVisibleToUser = false
//如果当前Fragment可见，那么setUserVisibleHint()会再次被调用一次，传入isVisibleToUser = true
//如果Fragment从可见->不可见，那么setUserVisibleHint()也会被调用，传入isVisibleToUser = false
//总结：setUserVisibleHint()除了Fragment的可见状态发生变化时会被回调外，在new Fragment()时也会被回调
//如果我们需要在 Fragment 可见与不可见时干点事，用这个的话就会有多余的回调了，那么就需要重新封装一个
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        //setUserVisibleHint()有可能在fragment的生命周期外被调用
        com.base.log.DebugLog.i("SSSS", "setUserVisibleHint=====$isVisibleToUser  $this")
        com.base.log.DebugLog.i("SSSS", "setUserVisibleHint rootView=====$rootView  $this")
        if (rootView == null) {
            return
        }
        iniSofia()
        com.base.log.DebugLog.i("SSSS", "setUserVisibleHint isFirstVisible=====$isFirstVisible  $this")
        if (isFirstVisible && isVisibleToUser) {
            onFragmentFirstVisible()
            isFirstVisible = false
            com.base.log.DebugLog.i("SSSS", "setUserVisibleHint isFirstVisible && isVisibleToUser==" + (isFirstVisible && isVisibleToUser) + "  " + this)
        }
        if (isVisibleToUser) {
            isFragmentVisible = true
            onFragmentVisibleChange(true)
            com.base.log.DebugLog.i("SSSS", "setUserVisibleHint isFragmentVisible=====$isFragmentVisible  $this")
            return
        }
        if (isFragmentVisible) {
            isFragmentVisible = false
            onFragmentVisibleChange(false)
            com.base.log.DebugLog.i("SSSS", "setUserVisibleHint isFragmentVisible=$isFragmentVisible  $this")
        }
    }

    private fun initVariable() {
        isFirstVisible = true
        isFragmentVisible = false
        isReuseView = true
        rootView = null
    }

    /**
     * 设置是否使用 view 的复用，默认开启
     * view 的复用是指，ViewPager 在销毁和重建 Fragment 时会不断调用 onCreateView() -> onDestroyView()
     * 之间的生命函数，这样可能会出现重复创建 view 的情况，导致界面上显示多个相同的 Fragment
     * view 的复用其实就是指保存第一次创建的 view，后面再 onCreateView() 时直接返回第一次创建的 view
     *
     * @param isReuse
     */
    protected fun reuseView(isReuse: Boolean) {
        isReuseView = isReuse
    }

    /**
     * 去除setUserVisibleHint()多余的回调场景，保证只有当fragment可见状态发生变化时才回调
     * 回调时机在view创建完后，所以支持ui操作，解决在setUserVisibleHint()里进行ui操作有可能报null异常的问题
     *
     * 可在该回调方法里进行一些ui显示与隐藏，比如加载框的显示和隐藏
     *
     * @param isVisible true  不可见 -> 可见
     * false 可见  -> 不可见
     */
    abstract fun onFragmentVisibleChange(isVisible: Boolean)

    /**
     * 在fragment首次可见时回调，可在这里进行加载数据，保证只在第一次打开Fragment时才会加载数据，
     * 这样就可以防止每次进入都重复加载数据
     * 该方法会在 onFragmentVisibleChange() 之前调用，所以第一次打开时，可以用一个全局变量表示数据下载状态，
     * 然后在该方法内将状态设置为下载状态，接着去执行下载的任务
     * 最后在 onFragmentVisibleChange() 里根据数据下载状态来控制下载进度ui控件的显示与隐藏
     */
    abstract fun onFragmentFirstVisible()

    /**
     * UsbManager检测是否为键盘
     */
    private fun detectUsbAudioDevice() {
        ExecutorManager.getInstance().singleThread.execute {
            isKeyboard = false
            //第二种 通过InputManager获取
            val inputManager = activity!!.getSystemService(Context.INPUT_SERVICE) as InputManager
            //我们可以通过InputManager获取到当前的所有设备的DeviceId
            val inputDeviceIds = inputManager.inputDeviceIds
            for (inputDeviceId in inputDeviceIds) {
                val inputDevice = inputManager.getInputDevice(inputDeviceId) ?: continue
                com.base.log.DebugLog.i("name=" + inputDevice.name)
                com.base.log.DebugLog.i("getSources=" + (inputDevice.sources and InputDevice.SOURCE_KEYBOARD))
                com.base.log.DebugLog.i("getKeyboardType=" + inputDevice.keyboardType)
                com.base.log.DebugLog.i("isVirtual=" + inputDevice.isVirtual)
                val sources = inputDevice.sources
                if (!inputDevice.isVirtual
                        && sources and InputDevice.SOURCE_KEYBOARD == InputDevice.SOURCE_KEYBOARD) { //KEYBOARD_TYPE_ALPHABETIC 有字母的键盘  KEYBOARD_TYPE_NONE 没有键盘  KEYBOARD_TYPE_NON_ALPHABETIC 没有字母的键盘
                    if (inputDevice.keyboardType == InputDevice.KEYBOARD_TYPE_ALPHABETIC) {
                        isKeyboard = true
                        break
                    }
                }
            }
            activity!!.runOnUiThread {
                if (isKeyboard && isDisable) { //在BaseActivity里禁用软键盘
                    activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
                } else { //在需要打开的Activity取消禁用软键盘
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
                }
            }
        }
    }

    /**
     * 是否禁用软键盘
     * @param disable
     */
    fun setDisable(disable: Boolean) {
        isDisable = disable
        if (disable) { //在BaseActivity里禁用软键盘
            activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        } else { //在需要打开的Activity取消禁用软键盘
            activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        }
    }

    /**
     * onViewCreated是在onCreateView后被触发的事件
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //如果setUserVisibleHint()在rootView创建前调用时，那么
//就等到rootView创建完后才回调onFragmentVisibleChange(true)
//保证onFragmentVisibleChange()的回调发生在rootView创建完成之后，以便支持ui操作
        com.base.log.DebugLog.i("SSSS", "onViewCreated=====$this  rootView=$rootView")
        if (rootView == null) {
            rootView = view
        }
        super.onViewCreated((if (isReuseView && rootView != null) rootView!! else view), savedInstanceState)
    }

    /**
     * 执行该方法时，与Fragment绑定的Activity的onCreate方法已经执行完成并返回，在该方法内可以进行与Activity交互的UI操作，
     * 所以在该方法之前Activity的onCreate方法并未执行完成，如果提前进行交互操作，会引发空指针异常。
     * @param savedInstanceState
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        com.base.log.DebugLog.i("SSSS", "onActivityCreated=====$this  rootView=$rootView")
        detectUsbAudioDevice()
    }

    /**
     * 启动Fragement 启动时回调,，此时Fragement可见
     */
    override fun onStart() {
        super.onStart()
        com.base.log.DebugLog.i("SSSS", "onStart=====$this  getUserVisibleHint()=$userVisibleHint")
    }

    /**
     * Fragment处于活动状态，用户可与之交互。
     */
    override fun onResume() {
        super.onResume()
        //告诉FragmentActivity，当前Fragment在栈顶
        if (mBackInterface != null) mBackInterface!!.setSelectedFragment(this)
        com.base.log.DebugLog.i("SSSS", "onResume=====$this  getUserVisibleHint()=$userVisibleHint")
        if (userVisibleHint) {
            com.base.log.DebugLog.i("SSSS", "onResume=====$this  isFirstVisible()=$isFirstVisible")
            if (isFirstVisible) {
                onFragmentFirstVisible()
                isFirstVisible = false
                isFragmentVisible = true
            } else {
                isFragmentVisible = true
                onFragmentVisibleChange(true)
            }
        }
    }

    /**
     * Fragment处于暂停状态，但依然可见，用户不能与之交互。
     */
    override fun onPause() {
        super.onPause()
        com.base.log.DebugLog.i("SSSS", "onPause=====$this")
    }

    /**
     * Fragment完全不可见。
     */
    override fun onStop() {
        super.onStop()
        com.base.log.DebugLog.i("SSSS", "onStop=====$this")
    }

    /**
     * 销毁与Fragment有关的视图，但未与Activity解除绑定，依然可以通过onCreateView方法重新创建视图
     */
    override fun onDestroyView() {
        //结束协程
        cancel()
        super.onDestroyView()
        com.base.log.DebugLog.i("SSSS", "onDestroyView=====$this")
    }

    /**
     * 销毁Fragment对象。
     */
    override fun onDestroy() {
        super.onDestroy()
        initVariable()
        com.base.log.DebugLog.i("SSSS", "onDestroy=====$this")
    }

    /**
     * Fragment和Activity解除关联的时候调用。
     */
    override fun onDetach() {
        super.onDetach()
        com.base.log.DebugLog.i("SSSS", "onDetach=====$this")
    }

    private fun iniSofia() {
        if (!StatusBarUtil.setStatusBarDarkTheme(activity, true)) { //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
//这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(activity, 0x55000000)
        }
    }
}