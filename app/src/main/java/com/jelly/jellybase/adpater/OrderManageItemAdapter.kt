package com.gm.trademanage.adapter

import android.content.Context
import com.jelly.baselibrary.BaseAdapterB
import com.jelly.baselibrary.VBindHolder
import com.jelly.baselibrary.recyclerViewUtil.OnItemClickListener
import com.jelly.jellybase.databinding.OrderManageItemItemBinding

/**
 * 订单管理
 */
class OrderManageItemAdapter(context: Context) : BaseAdapterB<OrderManageItemItemBinding>(context) {
    private val mList = ArrayList<Any>()
    var parentPos:Int=0
    var onItemClickListener: OnItemClickListener? = null
    override fun onBindViewHolder(
        holder: VBindHolder<OrderManageItemItemBinding>,
        position: Int,
        binding: OrderManageItemItemBinding
    ) {
        val item = mList[position]
        onItemClickListener?.let {
            binding.root.setOnClickListener { view ->
                it.onItemClick(view, this, parentPos,position)
            }
        }
    }

    override fun notifyDataSetChanged(dataList: List<*>) {
        mList.clear()
        mList.addAll(dataList as ArrayList<Any>)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}