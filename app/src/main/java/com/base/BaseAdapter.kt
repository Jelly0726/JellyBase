
package com.base

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import butterknife.ButterKnife
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * @author Jelly
 */
abstract class BaseAdapter<VH : BaseAdapter.ViewHolder?>(val context: Context)
    : RecyclerView.Adapter<VH>(), CoroutineScope by MainScope() {
    val inflater: LayoutInflater

    abstract fun notifyDataSetChanged(dataList: List<*>?)

    init {
        inflater = LayoutInflater.from(context)
    }
    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            ButterKnife.bind(this,itemView)
        }
    }
}