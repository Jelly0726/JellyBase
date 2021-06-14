package com.jelly.mvvmdemo

import android.content.Context
import com.jelly.mvvmdemo.databinding.MainItemBinding

class MainAdapter(context:Context):BaseAdapterB<MainItemBinding>(context) {
    override fun onBindViewHolder(holder: VBindHolder<MainItemBinding>, position: Int, binding: MainItemBinding) {
        val item=
    }

    override fun notifyDataSetChanged(dataList: List<*>) {
    }

    override fun getItemCount(): Int {
    }
}