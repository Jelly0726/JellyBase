package com.base.view

import android.app.Activity
import android.app.LauncherActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Rect
import android.hardware.input.InputManager
import android.os.*
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.Unbinder
import cn.jpush.android.api.JPushInterface
import com.base.SystemBar.StatusBarUtil
import com.base.appManager.*
import com.base.applicationUtil.AppPrefs
import com.base.config.ConfigKey
import com.base.config.IntentAction
import com.base.httpmvp.retrofitapi.token.GlobalToken
import com.mylhyl.circledialog.CircleDialog
import hugo.weaving.DebugLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * Created by Administrator on 2017/12/5.
 */
@DebugLog
abstract class BaseActivity : AppCompatActivity(), Observer<Any>, CoroutineScope by MainScope(), SoftKeyboardManager.SoftKeyboardStateListener {
    private var softKeyboardManager: SoftKeyboardManager? = null
    private var frameLayout: FrameLayout? = null//最外层布局
    private lateinit var mUnbinder: Unbinder
    private var mRecevier: InnerRecevier? = null
    private var mFilter: IntentFilter? = null
    private var isResume = false
    private var isKeyboard = false //是否有外接键盘
    private var isDisable = true //是否屏蔽软键盘
    private var handler: Handler? = null

    companion object {
        //    public DisplayManager mDisplayManager;//双屏客显
//    public Presentation mPresentation;//双屏客显
    }

    /**
     * 当前Activity的布局
     */
    abstract fun getLayoutId(): Int
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating) {
            val result = fixOrientation()
            com.base.log.DebugLog.i("onCreate fixOrientation when Oreo, result = $result")
        }
        //        //无title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // 全屏
//        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
//                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating) {
            val result = fixOrientation()
            com.base.log.DebugLog.i("onCreate fixOrientation when Oreo, result = $result")
        }
        detectUsbAudioDevice()
        super.onCreate(savedInstanceState)
        //====解决java.net.SocketException：sendto failed：ECONNRESET（由对等方重置连接）
        if (Build.VERSION.SDK_INT > 9) {
            val policy =
                    StrictMode.ThreadPolicy.Builder()
                            .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        //====
        if (getLayoutId() > 0)
            setContentView(getLayoutId())
        frameLayout?.let {
            //监听SoftKeyboard的弹出与隐藏状态
            softKeyboardManager = SoftKeyboardManager(it);
            softKeyboardManager!!.addSoftKeyboardStateListener(this);
        }
        handler = MyHandler(this)
        mUnbinder = ButterKnife.bind(this)
        getExtra()
        mRecevier = InnerRecevier()
        mFilter = IntentFilter()
        mFilter!!.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        mFilter!!.addAction(IntentAction.TOKEN_NOT_EXIST)
        AppSubject.getInstance().attach(this)
        /**
         * 开始监听，注册广播
         */
        if (mRecevier != null) {
            registerReceiver(mRecevier, mFilter)
        }
    }

    /**
     * UsbManager检测是否为键盘
     */
    private fun detectUsbAudioDevice() {
        launch {
            isKeyboard = false
            //第二种 通过InputManager获取
            val inputManager =
                    getSystemService(Context.INPUT_SERVICE) as InputManager
            //我们可以通过InputManager获取到当前的所有设备的DeviceId
            val inputDeviceIds = inputManager.inputDeviceIds
            for (inputDeviceId in inputDeviceIds) {
                val inputDevice =
                        inputManager.getInputDevice(inputDeviceId) ?: continue
                val sources = inputDevice.sources
                if (!inputDevice.isVirtual
                        && sources and InputDevice.SOURCE_KEYBOARD == InputDevice.SOURCE_KEYBOARD
                ) { //KEYBOARD_TYPE_ALPHABETIC 有字母的键盘  KEYBOARD_TYPE_NONE 没有键盘  KEYBOARD_TYPE_NON_ALPHABETIC 没有字母的键盘
                    if (inputDevice.keyboardType == InputDevice.KEYBOARD_TYPE_ALPHABETIC) {
                        isKeyboard = true
                        break
                    }
                }
            }
            if (isKeyboard && isDisable) { //在BaseActivity里禁用软键盘
                window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            } else { //在需要打开的Activity取消禁用软键盘
                window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
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
            window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        } else { //在需要打开的Activity取消禁用软键盘
            window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) //must store the new intent unless getIntent() will return the old one
        getExtra()
    }

    /**
     * 获取Intent传值
     */
    open fun getExtra() {

    }


    override fun setContentView(view: View) {
        frameLayout = FrameLayout(this)
        frameLayout!!.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )
        frameLayout!!.addView(view)
        super.setContentView(frameLayout)
        iniBar()
    }

    override fun onCreateView(
            name: String,
            context: Context,
            attrs: AttributeSet
    ): View? {
        if (!BaseApplication.isVampix) { //不进行黑白化
            return super.onCreateView(name, context, attrs)
        }
        if ("FrameLayout" == name) {
            val count = attrs.attributeCount
            for (i in 0 until count) {
                val attributeName = attrs.getAttributeName(i)
                val attributeValue = attrs.getAttributeValue(i)
                if (attributeName == "id") {
                    val id = attributeValue.substring(1).toInt()
                    val idVal = resources.getResourceName(id)
                    if ("android:id/content" == idVal) {
                        val grayFrameLayout = GrayFrameLayout(context, attrs)
                        //设置window 的 backgroud。
                        grayFrameLayout.setBackgroundDrawable(window.decorView.background)
                        //如果你是theme 中设置的 windowBackground，那么需要从 theme 里面提取 drawable，参考代码如下：
//                        TypedValue a = new TypedValue();
//                        getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
//                        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
//                            // windowBackground is a color
//                            int color = a.data;
//                        } else {
//                            // windowBackground is not a color, probably a drawable
//                            Drawable c = getResources().getDrawable(a.resourceId);
//                        }
                        return grayFrameLayout
                    }
                }
            }
        }
        return super.onCreateView(name, context, attrs)
    }

    override fun setContentView(view: Int) {
        val views = layoutInflater.inflate(view, null)
        this.setContentView(views)
    }

    private fun iniBar() {
        //// ↓↓↓↓↓内容入侵状态栏。↓↓↓↓↓
//这里注意下 因为在评论区发现有网友调用setRootViewFitsSystemWindows 里面 winContent.getChildCount()=0 导致代码无法继续
//是因为你需要在setContentView之后才可以调用 setRootViewFitsSystemWindows
//当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, false)
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this)
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
//所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(
                        this,
                        true
                )
        ) { //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
//这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000)
        }
        //// ↑↑↑↑↑内容入侵状态栏。↑↑↑↑↑
    }

    private val isTranslucentOrFloating: Boolean
        private get() {
            var isTranslucentOrFloating = false
            try {
                val styleableRes =
                        Class.forName("com.android.internal.R\$styleable")
                                .getField("Window")[null] as IntArray
                val ta = obtainStyledAttributes(styleableRes)
                val m = ActivityInfo::class.java.getMethod(
                        "isTranslucentOrFloating",
                        TypedArray::class.java
                )
                m.isAccessible = true
                isTranslucentOrFloating = m.invoke(null, ta) as Boolean
                m.isAccessible = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return isTranslucentOrFloating
        }

    private fun fixOrientation(): Boolean {
        try {
            val field =
                    Activity::class.java.getDeclaredField("mActivityInfo")
            field.isAccessible = true
            val o =
                    field[this] as ActivityInfo
            o.screenOrientation = -1
            field.isAccessible = false
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun setRequestedOrientation(requestedOrientation: Int) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating) {
            com.base.log.DebugLog.i("avoid calling setRequestedOrientation when Oreo.")
            return
        }
        super.setRequestedOrientation(requestedOrientation)
    }

    /**
     * 延迟关闭
     * @param time 延迟时间
     */
    fun finish(time: Int) {
        Handler().postDelayed({ finish() }, time.toLong())
    }

    override fun onDestroy() {
        //结束协程
        cancel()
        /**
         * 停止监听，注销广播
         */
        if (mRecevier != null) {
            unregisterReceiver(mRecevier)
        }
        AppSubject.getInstance().detach(this)
        mUnbinder?.let { it.unbind() }
        softKeyboardManager?.let {
            it.removeSoftKeyboardStateListener(this);
            it.dispose();
        }
        super.onDestroy()
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode,resultCode,data);
//        switch (requestCode) {
//            case REQUEST_CODE_SETTING: {
//                ToastUtils.showShort(this, R.string.message_setting_comeback);
//                break;
//            }
//        }
//    }
    override fun onResume() {
        super.onResume()
        JPushInterface.onResume(this)
        isResume = true
    }

    override fun onPause() {
        super.onPause()
        JPushInterface.onPause(this)
        isResume = false
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
//        if (SoftKeyboardManager.DEBUG) {
//            Log.d(
//                    "BaseActivity",
//                    "keyboardOpened, keyboardHeightInPx = $keyboardHeightInPx"
//            )
//        }
        val rect = Rect()
        //获取root在窗体的可视区域
        frameLayout!!.getWindowVisibleDisplayFrame(rect)
        //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
        val rootInvisibleHeight: Int = frameLayout!!.getRootView().getHeight() - rect.bottom
        //若不可视区域高度大于100，则键盘显示
        if (rootInvisibleHeight > 100) {
            val location = IntArray(2)
            //获取focusedView在窗体的坐标
            val focusedView = currentFocus
            if (focusedView is EditText) {
                focusedView!!.getLocationInWindow(location)
                val focusedViewPosY = location[1] + focusedView.height
//                if (SoftKeyboardManager.DEBUG) {
//                    Log.d(
//                            "BaseActivity",
//                            "rect.bottom= " + rect.bottom + ", focusedViewPosY = " + focusedViewPosY
//                    )
//                    Log.i(
//                            "BaseActivity",
//                            "focused view need scroll up or down"
//                    )
//                }
                val srollHeight = focusedViewPosY - rect.bottom
//                if (SoftKeyboardManager.DEBUG) {
//                    Log.i(
//                            "BaseActivity",
//                            "srollHeight = $srollHeight"
//                    )
//                }
                if (srollHeight > 0) { //焦点被输入法遮挡,View向上滚动
                    frameLayout!!.scrollTo(0, srollHeight)
                }
            }
        }
    }

    override fun onSoftKeyboardClosed() {
//        if (SoftKeyboardManager.DEBUG) {
//            Log.d("BaseActivity", "keyboardClosed ")
//        }
        //输入法退出，root滚动到初始位置
        frameLayout!!.scrollTo(0, 0)
    }

    /**
     * 广播接收者
     */
    internal inner class InnerRecevier : BroadcastReceiver() {
        val SYSTEM_DIALOG_REASON_KEY = "reason"
        val SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions"
        val SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps"
        val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"
        override fun onReceive(
                context: Context,
                intent: Intent
        ) {
            val action = intent.action
            if (action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
                if (reason != null) { //Log.i("msg", "action:" + action + ",reason:" + reason);
                    if (reason == SYSTEM_DIALOG_REASON_HOME_KEY) { // 短按home键
                        AppPrefs.putBoolean(BaseApplication.instance, ConfigKey.ISHOME, true)
                    } else if (reason
                            == SYSTEM_DIALOG_REASON_RECENT_APPS
                    ) { // 长按home键
                    }
                }
            } else if (action == IntentAction.TOKEN_NOT_EXIST) { //token不存在
                if (isResume) {
                    val message = Message.obtain()
                    message.arg1 = 0
                    handler!!.sendMessage(message)
                }
            }
        }
    }

    private var circleDialog: CircleDialog.Builder? = null

    private class MyHandler(activity: BaseActivity) : Handler() {
        private val activityWeakReference = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            val activity = activityWeakReference.get()
            if (activity != null) {
                when (msg.arg1) {
                    0 -> if (activity.circleDialog == null) {
                        synchronized(BaseApplication.instance) {
                            if (activity.circleDialog == null) {
                                activity.circleDialog = CircleDialog.Builder()
                                        .configDialog { params -> params.width = 0.6f }
                                        .setCanceledOnTouchOutside(false)
                                        .setCancelable(false)
                                        .setTitle("登录过期！")
                                        .configText { params ->
                                            params.gravity = Gravity.LEFT
                                            params.textColor =
                                                    Color.parseColor("#FF1F50F1")
                                            params.padding = intArrayOf(20, 0, 20, 0)
                                        }
                                        .setText("登录过期或异地登录，请重新登录!")
                                        .setPositive("确定") {
                                            activity.circleDialog = null
                                            GlobalToken.removeToken()
                                            AppSubject.getInstance().detachAll()
                                            val intent1 =
                                                    Intent()
                                            //intent.setClass(this, LoginActivity.class);
                                            intent1.action = IntentAction.ACTION_LOGIN
                                            intent1.addFlags(
                                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                                            or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            )
                                            BaseApplication.instance.startActivity(intent1)
                                        }
                                activity.circleDialog!!.show(activity.supportFragmentManager)
                            }
                        }
                    }
                }
            }
        }

    }

    override fun finish() { // TODO Auto-generated method stub
        if (this !is LauncherActivity)
            circleDialog?.let {
                it.dismiss()
                circleDialog = null
            }
        AppSubject.getInstance().detach(this)
        FixMemLeak.fixLeak(this)
        super.finish()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean { //设置接收到回车事件时隐藏软键盘
        if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            val manager =
                    getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            val focus = currentFocus
            manager.hideSoftInputFromWindow(
                    focus?.windowToken,  //                            editText == null ? null : editText.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onUpdate(observable: Observable<Any>, data: Any) {
        this.finish()
    }
}