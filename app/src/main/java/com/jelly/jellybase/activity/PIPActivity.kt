package com.jelly.jellybase.activity

import androidx.lifecycle.Observer
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import com.base.daemon.DaemonEnv
import com.jelly.baselibrary.eventBus.NetEvent
import com.jelly.baselibrary.log.LogUtils
import com.jelly.baselibrary.BaseActivity
import com.jelly.baselibrary.multiClick.AntiShake
import com.jelly.jellybase.R
import com.jelly.jellybase.databinding.PipModeActivityBinding
import com.jelly.jellybase.server.TraceServiceImpl
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * PIP模式
 */
class PIPActivity : BaseActivity<PipModeActivityBinding>(),View.OnClickListener{
    private lateinit var mObserver: Observer<Any>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iniView()
    }
    override fun onDestroy() {
        super.onDestroy()
        LiveEventBus.get("PIPActivity").removeObserver(mObserver)
    }
    private fun iniView(){
        binding!!.backLayout.setOnClickListener(this)
        binding!!.btn.setOnClickListener(this)
        mObserver= Observer<Any> { netEvent ->
            netEvent?.let {
                netEvent as NetEvent<Any>
                binding.msgTv.text="${netEvent.event}"
            }
        }
        LiveEventBus.get("PIPActivity")
                .observeForever(mObserver)
    }
    override fun onClick(view:View){
        if (AntiShake.check(view))return
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
        LogUtils.i("是否PIP=${isInPictureInPictureMode}}")
    }
}