package com.base.view

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.TypedArray
import android.graphics.Color
import android.hardware.input.InputManager
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import cn.jpush.android.api.JPushInterface
import com.base.SystemBar.StatusBarUtil
import com.base.appManager.AppSubject
import com.base.appManager.BaseApplication
import com.base.appManager.Observable
import com.base.appManager.Observer
import com.base.applicationUtil.AppPrefs
import com.base.circledialog.CircleDialog
import com.base.circledialog.callback.ConfigDialog
import com.base.circledialog.callback.ConfigText
import com.base.circledialog.params.DialogParams
import com.base.circledialog.params.TextParams
import com.base.config.ConfigKey
import com.base.config.IntentAction
import com.base.httpmvp.retrofitapi.token.GlobalToken
import com.base.permission.PermissionUtils
import com.base.toast.ToastUtils
import com.jelly.jellybase.R
import hugo.weaving.DebugLog
import kotlinx.coroutines.*

/**
 * Created by Jelly on 2017/12/5.
 */
@DebugLog
open class BaseActivity : AppCompatActivity(), Observer<Any> , CoroutineScope by MainScope(){
    private var mRecevier: InnerRecevier? = null
    private var mFilter: IntentFilter? = null
    private var isResume = false
    private var isKeyboard = false //是否有外接键盘
    private var isDisable = true //是否屏蔽软键盘

    companion object {
        //    public DisplayManager mDisplayManager;//双屏客显
//    public Presentation mPresentation;//双屏客显
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating) {
            val result = fixOrientation()
            com.base.log.DebugLog.i("onCreate fixOrientation when Oreo, result = $result")
        }
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 全屏
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating) {
            val result = fixOrientation()
            com.base.log.DebugLog.i("onCreate fixOrientation when Oreo, result = $result")
        }
        detectUsbAudioDevice()
        super.onCreate(savedInstanceState)
        //====解决java.net.SocketException：sendto failed：ECONNRESET（由对等方重置连接）
        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder()
                    .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        //====
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
            async {
                isKeyboard = false
                //第二种 通过InputManager获取
                val inputManager =
                        getSystemService(Context.INPUT_SERVICE) as InputManager
                //我们可以通过InputManager获取到当前的所有设备的DeviceId
                val inputDeviceIds = inputManager.inputDeviceIds
                for (inputDeviceId in inputDeviceIds) {
                    val inputDevice =
                            inputManager.getInputDevice(inputDeviceId) ?: continue
                    com.base.log.DebugLog.i("name=" + inputDevice.name)
                    com.base.log.DebugLog.i("getSources=" + (inputDevice.sources and InputDevice.SOURCE_KEYBOARD))
                    com.base.log.DebugLog.i("getKeyboardType=" + inputDevice.keyboardType)
                    com.base.log.DebugLog.i("isVirtual=" + inputDevice.isVirtual)
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
    open fun getExtra(){

    }

    override fun setContentView(view: View) {
        val frameLayout = FrameLayout(this)
        frameLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        frameLayout.addView(view)
        super.setContentView(frameLayout)
        iniBar()
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        if (!BaseApplication.isVampix()) { //不进行黑白化
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

    private fun iniBar() { //// ↓↓↓↓↓内容入侵状态栏。↓↓↓↓↓
//这里注意下 因为在评论区发现有网友调用setRootViewFitsSystemWindows 里面 winContent.getChildCount()=0 导致代码无法继续
//是因为你需要在setContentView之后才可以调用 setRootViewFitsSystemWindows
//当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, false)
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this)
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
//所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) { //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
//这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000)
        }
        //// ↑↑↑↑↑内容入侵状态栏。↑↑↑↑↑
    }

    private val isTranslucentOrFloating: Boolean
        private get() {
            var isTranslucentOrFloating = false
            try {
                val styleableRes = Class.forName("com.android.internal.R\$styleable").getField("Window")[null] as IntArray
                val ta = obtainStyledAttributes(styleableRes)
                val m = ActivityInfo::class.java.getMethod("isTranslucentOrFloating", TypedArray::class.java)
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
            val field = Activity::class.java.getDeclaredField("mActivityInfo")
            field.isAccessible = true
            val o = field[this] as ActivityInfo
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
        super.onDestroy()
        if (circleDialog != null) {
            circleDialog!!.onDismiss()
            circleDialog = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PermissionUtils.REQUEST_CODE_SETTING -> {
                ToastUtils.showShort(this, R.string.message_setting_comeback)
            }
        }
    }

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

    /**
     * 广播接收者
     */
    internal inner class InnerRecevier : BroadcastReceiver() {
        val SYSTEM_DIALOG_REASON_KEY = "reason"
        val SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions"
        val SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps"
        val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
                if (reason != null) { //Log.i("msg", "action:" + action + ",reason:" + reason);
                    if (reason == SYSTEM_DIALOG_REASON_HOME_KEY) { // 短按home键
                        AppPrefs.putBoolean(BaseApplication.getInstance(), ConfigKey.ISHOME, true)
                    } else if (reason
                            == SYSTEM_DIALOG_REASON_RECENT_APPS) { // 长按home键
                    }
                }
            } else if (action == IntentAction.TOKEN_NOT_EXIST) { //token不存在
                if (isResume) {
                    val message = Message.obtain()
                    message.arg1 = 0
                    handler.sendMessage(message)
                }
            }
        }
    }

    private var circleDialog: CircleDialog.Builder? = null
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.arg1) {
                0 -> if (circleDialog == null) {
                    synchronized(BaseApplication.getInstance()) {
                        if (circleDialog == null) {
                            circleDialog = CircleDialog.Builder(this@BaseActivity)
                                    .configDialog(object : ConfigDialog() {
                                        override fun onConfig(params: DialogParams) {
                                            params.width = 0.6f
                                        }
                                    })
                                    .setCanceledOnTouchOutside(false)
                                    .setCancelable(false)
                                    .setTitle("登录过期！")
                                    .configText(object : ConfigText() {
                                        override fun onConfig(params: TextParams) {
                                            params.gravity = Gravity.LEFT
                                            params.textColor = Color.parseColor("#FF1F50F1")
                                            params.padding = intArrayOf(20, 0, 20, 0)
                                        }
                                    })
                                    .setText("登录过期或异地登录，请重新登录!")
                                    .setPositive("确定") {
                                        circleDialog = null
                                        GlobalToken.removeToken()
                                        AppSubject.getInstance().detachAll()
                                        val intent1 = Intent()
                                        //intent.setClass(this, LoginActivity.class);
                                        intent1.action = IntentAction.ACTION_LOGIN
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        BaseApplication.getInstance().startActivity(intent1)
                                    }
                            circleDialog!!.show()
                        }
                    }
                }
            }
        }
    }

    override fun finish() { // TODO Auto-generated method stub
        AppSubject.getInstance().detach(this)
        super.finish()
    }

    override fun onUpdate(observable: Observable<Any>, data: Any) { // TODO Auto-generated method stub
        this.finish()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean { //设置接收到回车事件时隐藏软键盘
        if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            val manager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            val focus = currentFocus
            manager.hideSoftInputFromWindow(
                    focus?.windowToken,  //                            editText == null ? null : editText.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }
        return super.dispatchKeyEvent(event)
    }
}