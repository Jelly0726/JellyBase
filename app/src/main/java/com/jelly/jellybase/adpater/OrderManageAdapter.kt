package com.gm.trademanage.adapter

import android.content.Context
import android.graphics.Rect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.base.BaseApplication
import com.jelly.baselibrary.BaseAdapterB
import com.jelly.baselibrary.VBindHolder
import com.jelly.baselibrary.applicationUtil.AppUtils
import com.jelly.baselibrary.recyclerViewUtil.ItemDecoration
import com.jelly.baselibrary.recyclerViewUtil.OnItemClickListener
import com.jelly.jellybase.R
import com.jelly.jellybase.databinding.OrderManageItemBinding

/**
 * 订单管理
 */
class OrderManageAdapter(context: Context) : BaseAdapterB<OrderManageItemBinding>(context) {
    private val mList = ArrayList<Any>()
    var onItemClickListener: OnItemClickListener? = null
    override fun onBindViewHolder(
        holder: VBindHolder<OrderManageItemBinding>,
        position: Int,
        binding: OrderManageItemBinding
    ) {
        val item = mList[position]
        //=====优化嵌套卡顿=======
        binding.mRecyclerView.setHasFixedSize(true)
        binding.mRecyclerView.isNestedScrollingEnabled=false
        binding.mRecyclerView.setItemViewCacheSize(600)
        binding.mRecyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool())
        //=====优化嵌套卡顿=======

        val mLayoutManager=LinearLayoutManager(context)
        if (binding.mRecyclerView.itemDecorationCount==0){
            val mRect=Rect()
            mRect.top=AppUtils.dipTopx(BaseApplication.instance,1f)
            mRect.bottom=AppUtils.dipTopx(BaseApplication.instance,0f)
            mRect.left=AppUtils.dipTopx(BaseApplication.instance,0f)
            mRect.right=AppUtils.dipTopx(BaseApplication.instance,0f)
            val mItemDecoration=ItemDecoration(mRect,1,ItemDecoration.NONE
                ,context.resources.getColor(R.color.transparent))
            binding.mRecyclerView.addItemDecoration(mItemDecoration)
        }
        binding.mRecyclerView.layoutManager=mLayoutManager
        var mAdapter=binding.mRecyclerView.adapter as OrderManageItemAdapter?
        if (mAdapter==null){
            mAdapter=OrderManageItemAdapter(context)
        }
        mAdapter.parentPos=position
        onItemClickListener?.let {
            mAdapter.onItemClickListener= it
            binding.root.setOnClickListener { view ->
                it.onItemClick(view, this, position)
            }
        }
        binding.mRecyclerView.adapter=mAdapter
        //模拟数据
        val da=ArrayList<Any>()
        da.add("")
        da.add("")
        mAdapter.notifyDataSetChanged(da)

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