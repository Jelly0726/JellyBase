
package com.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * @author Jelly
 */
abstract class BaseAdapter<VH : RecyclerView.ViewHolder?>(val context: Context)
    : RecyclerView.Adapter<VH>(), CoroutineScope by MainScope() {
    val inflater: LayoutInflater

    abstract fun notifyDataSetChanged(dataList: List<*>?)

    init {
        inflater = LayoutInflater.from(context)
    }
}