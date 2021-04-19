
package com.jelly.baselibrary

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * @author Jelly
 */
abstract class BaseAdapter<VH : RecyclerView.ViewHolder?>(val context: Context)
    : RecyclerView.Adapter<VH>(), CoroutineScope by MainScope() {
    val inflater: LayoutInflater = LayoutInflater.from(context)
    abstract fun notifyDataSetChanged(dataList: List<*>)

}