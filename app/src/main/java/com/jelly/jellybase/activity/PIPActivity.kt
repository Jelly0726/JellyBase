package com.jelly.jellybase.activity

import android.arch.lifecycle.Observer
import android.os.Build
import android.os.Bundle
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.base.eventBus.NetEvent
import com.base.liveDataBus.LiveDataBus
import com.base.view.BaseActivity
import com.jelly.jellybase.R
import kotlinx.android.synthetic.main.pip_mode_activity.*

/**
 * PIP模式
 */
class PIPActivity :BaseActivity(){
    private lateinit var mUnbinder: Unbinder
    private lateinit var mObserver: Observer<Any>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pip_mode_activity)
        mUnbinder=ButterKnife.bind(this)
        iniView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mUnbinder?.let { it.unbind() }
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
                finish()
            }
            R.id.btn->{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    enterPictureInPictureMode()
                }
            }
        }
    }
}