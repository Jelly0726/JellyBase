package com.jelly.mvvmdemo

import android.content.Context
import com.jelly.mvvmdemo.databinding.MainItemBinding

class MainAdapter(context:Context):BaseAdapterB<MainItemBinding>(context) {
    private val mList=ArrayList<DemoVo>()
    override fun onBindViewHolder(holder: VBindHolder<MainItemBinding>, position: Int, binding: MainItemBinding) {
        val item=mList[position]
        binding.demo=item
    }

    override fun notifyDataSetChanged(dataList: List<*>) {
        mList.clear()
        mList.addAll(dataList as ArrayList<DemoVo>)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}