package com.jelly.jellybase.activity

import androidx.lifecycle.Observer
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.base.daemon.DaemonEnv
import com.base.eventBus.NetEvent
import com.base.liveDataBus.LiveDataBus
import com.base.log.DebugLog
import com.base.view.BaseActivity
import com.jelly.jellybase.R
import com.jelly.jellybase.server.TraceServiceImpl
import kotlinx.android.synthetic.main.pip_mode_activity.*

/**
 * PIP模式
 */
class PIPActivity :BaseActivity(){
    private lateinit var mObserver: Observer<Any>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iniView()
    }
    override fun getLayoutId(): Int {
        return R.layout.pip_mode_activity
    }
    override fun onDestroy() {
        super.onDestroy()
        LiveDataBus.get("PIPActivity").removeObserver(mObserver)
    }
    private fun iniView(){
        mObserver=object :Observer<Any>{
            override fun onChanged(netEvent:Any?) {
                netEvent?.let {
                    netEvent as NetEvent<Any>
                    msgTv.text="${netEvent.event}"
                }
            }
        }
        LiveDataBus.get("PIPActivity")
                .observeForever(mObserver)
    }
    @OnClick(R.id.back_layout,R.id.btn)
    fun onClick(view:View){
        when(view.id){
            R.id.back_layout->{
                //启动或停止守护服务，运行在:watch子进程中
                TraceServiceImpl.sShouldStopService = true //true  表示停止服务，false  表示启动服务
                DaemonEnv.startServiceMayBind(TraceServiceImpl::class.java)
                finish()
            }
            R.id.btn->{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    enterPictureInPictureMode()
                    //启动或停止守护服务，运行在:watch子进程中
                    TraceServiceImpl.sShouldStopService = false //true  表示停止服务，false  表示启动服务
                    DaemonEnv.startServiceMayBind(TraceServiceImpl::class.java)
                }
            }
        }
    }

    /**
     * 监听pip模式变化 显示隐藏部分控件
     * @param isInPictureInPictureMode true 是 false 否
     */
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        DebugLog.i("是否PIP=${isInPictureInPictureMode}}")
    }
}