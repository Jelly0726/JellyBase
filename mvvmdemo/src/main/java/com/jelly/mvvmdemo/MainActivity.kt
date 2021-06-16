package com.jelly.mvvmdemo

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.databinding.Observable
import androidx.recyclerview.widget.LinearLayoutManager
import com.jelly.BaseApplication
import com.jelly.applicationUtil.AppUtils
import com.jelly.mprogressdialog.MProgressUtil
import com.jelly.mvvmdemo.databinding.MainActivityBinding
import com.jelly.mvvmdemo.viewmodel.MainViewModel

class MainActivity : BaseActivity<MainActivityBinding>() {
   lateinit var demoVo:MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iniView()
        demoVo = MainViewModel()
        binding.demoVo = demoVo
        demoVo.isShow.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (demoVo.isShow.get()){//弹出进度条
                    MProgressUtil.getInstance().show(this@MainActivity)
                }else{//关闭进度条
                    MProgressUtil.getInstance().dismiss()
                }
            }
        })
    }

    fun onClick(view: View) {
        println("view=${view.id}")
        demoVo.loadData()
    }
    private fun iniView(){
        val mLayoutManager= LinearLayoutManager(this)
        binding.mRecyclerView.layoutManager=mLayoutManager
        if ( binding.mRecyclerView.itemDecorationCount==0) {
            val mRect = Rect()
            mRect.top = AppUtils.dipTopx(BaseApplication.instance, 10f)
            mRect.bottom = AppUtils.dipTopx(BaseApplication.instance, 10f)
            mRect.left = AppUtils.dipTopx(BaseApplication.instance, 16f)
            mRect.right = AppUtils.dipTopx(BaseApplication.instance, 16f)
            val mItemDecoration = ItemDecoration(
                mRect,
                1,
                ItemDecoration.NONE,
                resources.getColor(R.color.transparent)
            )
            binding.mRecyclerView.addItemDecoration(mItemDecoration)
        }
        var mAdapter= MainAdapter(this)
        binding.mRecyclerView.adapter=mAdapter
//        mAdapter.notifyDataSetChanged(mList)
    }
}