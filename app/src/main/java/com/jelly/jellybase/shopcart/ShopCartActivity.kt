package com.luolifen.easybio.shopcart

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.jelly.baselibrary.Utils.RMBUtils
import com.base.BaseApplication.Companion.instance
import com.jelly.baselibrary.applicationUtil.AppUtils
import com.jelly.baselibrary.eventBus.NetEvent
import com.jelly.baselibrary.toast.ToastUtils
import com.jelly.baselibrary.BaseActivity
import com.jelly.jellybase.R
import com.jelly.jellybase.databinding.ShopcartActivityBinding
import com.jelly.jellybase.shopcart.CartInfo
import com.jelly.jellybase.shopcart.ShopCartAdapter
import com.jeremyliao.liveeventbus.LiveEventBus
import com.yanzhenjie.recyclerview.*
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration
import java.util.*

class ShopCartActivity : BaseActivity<ShopcartActivityBinding>(), OnItemClickListener,View.OnClickListener
    {
    protected lateinit var mLayoutManager: RecyclerView.LayoutManager
    protected lateinit var mItemDecoration: RecyclerView.ItemDecoration
    protected lateinit var mAdapter: ShopCartAdapter
    protected lateinit var mDataList: MutableList<CartInfo>
    protected var selectList: MutableList<CartInfo> = ArrayList()
    private var mtotalPrice = 0.00
    private var mMaxToal: Long = 10//
    private var page = 1
    private val size = 10
    private val goodsType: String? = null//筛选条件 homeProducts  beautyProject  beautyPackage
    private var cartInfo: CartInfo? = null
    private var mPosition: Int = 0//操作的下标
    private var textview: View? = null//数量变化的textview
    /**
     * 刷新。
     */
    private val mRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        mPosition = -1
        page = 1
//        presenter.getShoppingCart(true, lifecycleProvider.bindUntilEvent<Any>(ActivityEvent.DESTROY))
    }
    /**
     * 加载更多。
     */
    private val mLoadMoreListener = SwipeRecyclerView.LoadMoreListener {
        if (mMaxToal > page * size) {
            page++
//            presenter.getShoppingCart(false, lifecycleProvider.bindUntilEvent<Any>(ActivityEvent.DESTROY))
        }
    }
    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private val swipeMenuCreator = object : SwipeMenuCreator {
        override fun onCreateMenu(
                swipeLeftMenu: SwipeMenu,
                swipeRightMenu: SwipeMenu,
                viewType: Int
        ) {
            val width = AppUtils.dipTopx(this@ShopCartActivity, 70f)
            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            // 添加左侧的，如果不添加，则左侧不会出现菜单。
            run { }
            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            run {
                val deleteItem = SwipeMenuItem(this@ShopCartActivity)
                    .setBackground(android.R.color.white)
                    .setImage(R.drawable.ic_delete_red)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height)
                swipeRightMenu.addMenuItem(deleteItem)// 添加菜单到右侧。
            }
        }
    }

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private val mMenuItemClickListener = OnItemMenuClickListener{ swipeMenuBridge: SwipeMenuBridge, menuPosition: Int ->
        swipeMenuBridge.closeMenu()

        val direction = swipeMenuBridge.direction // 左侧还是右侧菜单。
        if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
            cartInfo = mDataList[menuPosition]
            mPosition = menuPosition
//            presenter.deleteShoppingCart(true, lifecycleProvider.bindUntilEvent<Any>(ActivityEvent.DESTROY))
        } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
            //                Toast.makeText(ShopCartActivity.this, "list第" + position + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
        }
    }
    private val countInterface = object : ShopCartAdapter.ModifyCountInterface {
        override fun doIncrease(childPosition: Int, showCountView: View, isChecked: Boolean) {
            cartInfo = mDataList[childPosition].deepClone() as CartInfo
            var count = cartInfo!!.goodsNum
            if (count == cartInfo!!.stock) {
                return
            }
            count++
            mPosition = childPosition
            cartInfo!!.goodsNum = count
            textview = showCountView
//            presenter.updateShoppingCart(true, lifecycleProvider.bindUntilEvent<Any>(ActivityEvent.DESTROY))
        }

        override fun doDecrease(childPosition: Int, showCountView: View, isChecked: Boolean) {
            cartInfo = mDataList[childPosition].deepClone() as CartInfo
            var count = cartInfo!!.goodsNum
            if (count == 1) {
                return
            }
            count--
            mPosition = childPosition
            cartInfo!!.goodsNum = count
            textview = showCountView
//            presenter.updateShoppingCart(true, lifecycleProvider.bindUntilEvent<Any>(ActivityEvent.DESTROY))
        }

        override fun doUpdate(
            childPosition: Int,
            count: Int,
            showCountView: View,
            isChecked: Boolean
        ) {
            var count = count
            cartInfo = mDataList[childPosition].deepClone() as CartInfo
            if (count >= cartInfo!!.stock) {
                count = cartInfo!!.stock
            }
            if (count == 0) count = 1
            mPosition = childPosition
            cartInfo!!.goodsNum = count
            textview = showCountView
//            presenter.updateShoppingCart(true, lifecycleProvider.bindUntilEvent<Any>(ActivityEvent.DESTROY))
        }

        override fun childDelete(childPosition: Int) {
            mPosition = childPosition
//            presenter.deleteShoppingCart(true, lifecycleProvider.bindUntilEvent<Any>(ActivityEvent.DESTROY))

        }
    }
    private val checkInterface =object: ShopCartAdapter.CheckInterface {
        override fun checkChild(childPosition: Int, isChecked: Boolean) {
            binding.allSelectBox!!.isChecked = isCheckAll//全选
            mAdapter.notifyDataSetChanged(mDataList)
            calulate()
        }
    }
    /**
     * @return 判断组元素是否全选
     */
    private val isCheckAll: Boolean
        get() {
            for (group in mDataList) {
                if (!group.isSelect) {
                    return false
                }
            }
            return true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iniSofia()
        iniView()
        iniRecyclerView()
        page = 1
//        presenter.getShoppingCart(true, lifecycleProvider.bindUntilEvent<Any>(ActivityEvent.DESTROY))
    }
    override fun onDestroy() {
        super.onDestroy()
        val netEvent= NetEvent<Any>()
        netEvent.eventType="RefreshShopcart"
        LiveEventBus.get("MainActivity").postDelay(netEvent,50)
    }
//    override fun initPresenter(): ShoppingCartContact.Presenter {
//        return ShoppingCartPresenter(this)
//    }

    private fun iniView() {
        binding!!.backLayout.setOnClickListener(this)
        binding!!.allSelectBox.setOnClickListener(this)
        binding!!.settlementTv.setOnClickListener(this)
        binding!!.emptyShopcart.goShop.setOnClickListener(this)
    }
    override fun onClick(view: View) {
        val intent: Intent
        when (view.id) {
            R.id.back_layout -> finish()
            R.id.allSelect_box -> doCheckAll()
            R.id.settlement_tv -> {
                if (selectList.size <= 0) {
                    ToastUtils.showShort(this, "请先选择商品！")
                    return
                }
//                val list = ArrayList<OrderParam>()
//                for (item in selectList) {
//                    val map = OrderParam()
//                    map.id = item.goodsId
//                    map.curOrgId = login.lastStoreId
//                    map.goodsNum = item.goodsNum
//                    map.shoppingCatId = item.id
//                    list.add(map)
//                }
//                intent= Intent(BaseApplication.getInstance(), ConfirmOrderActivity::class.java);
//                intent.putExtra("param", JSON.toJSONString(list));
//                startActivity(intent);
            }
            R.id.goShop -> {
                LiveEventBus.get("MainActivity")
                    .post(NetEvent<Any>().setEventType("CurrentItem").setArg(1))
                finish()
            }
        }
    }

    private fun iniSofia() {}
    private fun iniRecyclerView() {
        binding.refreshLayout.setOnRefreshListener(mRefreshListener)
        mAdapter = ShopCartAdapter(this)
        mLayoutManager = createLayoutManager()
        mItemDecoration = createItemDecoration()
        binding.mRecyclerView.layoutManager = mLayoutManager
        binding. mRecyclerView.addItemDecoration(mItemDecoration)
        binding.mRecyclerView.setOnItemClickListener(this)
        binding. mRecyclerView.useDefaultLoadMore() // 使用默认的加载更多的View。
        binding. mRecyclerView.setLoadMoreListener(mLoadMoreListener) // 加载更多的监听。
        binding.mRecyclerView.isLongPressDragEnabled = false // 长按拖拽，默认关闭。
        binding.mRecyclerView.isItemViewSwipeEnabled = false // 滑动删除，默认关闭。
        binding.mRecyclerView.setSwipeMenuCreator(swipeMenuCreator)
        binding. mRecyclerView.setOnItemMenuClickListener(mMenuItemClickListener)
        binding.mRecyclerView.adapter = mAdapter
        mDataList = ArrayList()
        mAdapter.checkInterface = checkInterface
        mAdapter.modifyCountInterface = countInterface
        binding.emptyShopcart.root.visibility = View.VISIBLE
    }

    protected fun createLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(this)
    }

    protected fun createItemDecoration(): RecyclerView.ItemDecoration {
        //        return StickItemDecoration.builder().adapterProvider(mAdapter)
        //                .build(ContextCompat.getColor(this, R.color.transparent),
        //                AppUtils.dipTopx(this,10), AppUtils.dipTopx(this,10),-1);
        return DefaultItemDecoration(
            ContextCompat.getColor(instance, R.color.transparent),
            AppUtils.dipTopx(instance, 10f),
            AppUtils.dipTopx(instance, 10f)
        )
    }

    private fun clearCart() {
        binding.settlementTv!!.text = "结算(0)"
        binding.emptyShopcart!!.root.visibility = View.VISIBLE
    }

    /**
     * 删除操作
     * 1.不要边遍历边删除,容易出现数组越界的情况
     * 2.把将要删除的对象放进相应的容器中，待遍历完，用removeAll的方式进行删除
     */
    private fun doDelete() {
        val toBeDeleteGroups = ArrayList<CartInfo>() //待删除的组元素
        for (j in mDataList.indices) {
            if (mDataList[j].isSelect) {
                toBeDeleteGroups.add(mDataList[j])
            }
        }
        mDataList.removeAll(toBeDeleteGroups)
        mAdapter.notifyDataSetChanged(mDataList)
        if (mDataList.size == 0) {
            clearCart()
        } else {
            binding.emptyShopcart!!.root.visibility = View.GONE
        }
    }

    /**
     * 全选和反选
     * 错误标记：在这里出现过错误
     */
    private fun doCheckAll() {
        for (goods in mDataList) {
            goods.isSelect =  binding.allSelectBox!!.isChecked
        }
        mAdapter.notifyDataSetChanged(mDataList)
        calulate()
    }

    /**
     * 计算商品总价格，操作步骤
     * 1.先清空全局计价,计数
     * 2.遍历所有的子元素，只要是被选中的，就进行相关的计算操作
     * 3.给textView填充数据
     */
    private fun calulate() {
        mtotalPrice = 0.00
//        shopingToal = 0.00
//        shopingCount=0
        selectList.clear()
        for (goods in mDataList) {
            if (goods.isSelect) {
                var isExist = false
                for (item in selectList) {
                    if (item.id == goods.id) {
                        isExist = true
                        break
                    }
                }
                if (!isExist)
                    selectList.add(goods)
                mtotalPrice += java.lang.Double.parseDouble(goods.price) * goods.goodsNum
            }
//            shopingToal+=java.lang.Double.parseDouble(goods.price) * goods.goodsNum
//            shopingCount+=goods.goodsNum
        }
        binding.totalPrice!!.text = "合计：¥" + RMBUtils.getTwoDecimal1(mtotalPrice) + ""
        binding.settlementTv!!.text = "结算(" + selectList.size + ")"
        if (mDataList.size == 0) {
            clearCart()
        } else {
            binding.emptyShopcart!!.root.visibility = View.GONE
        }
    }

    override fun onItemClick(itemView: View, position: Int) {
        mDataList[position].isSelect = !mDataList[position].isSelect
        checkInterface.checkChild(position, true)
    }

//    override fun getShoppingCartParam(): Any {
//        val map = TreeMap<String,Any>()
//        val map1 = TreeMap<String,Any>()
//        map1.put(
//            "orgId",
//            if (login == null ) "" else login.lastStoreId
//        )
//        if (!TextUtils.isEmpty(goodsType))
//            map1.put("goodsType", goodsType!!)
//        map.put("page", page)
//        map.put("size", size)
//        map.put("needPage", false)
//        map.put("search", map1)
//        return map
//    }
//
//    override fun getShoppingCartSuccess(isRe: Boolean, mCallBackVo: Any) {
//        val resultData = mCallBackVo as ResultData<*>
//        if (isRe) {
//            mDataList.clear()
//            refresh_layout!!.isRefreshing = false
//        }
//        mMaxToal = resultData.total
//        mDataList.addAll(resultData.getRows() as MutableList<CartInfo>)
//        mAdapter.notifyDataSetChanged(mDataList)
//        // 数据完更多数据，一定要掉用这个方法。
//        // 第一个参数：表示此次数据是否为空。
//        // 第二个参数：表示是否还有更多数据。
//        mRecyclerView!!.loadMoreFinish(resultData.rows.size == 0, mMaxToal > page * size)
//        calulate()
//    }
//
//    override fun getShoppingCartFailed(isRe: Boolean, message: String) {
//        ToastUtils.showShort(this, message)
//        if (isRe) {
//            mDataList.clear()
//            refresh_layout!!.isRefreshing = false
//        } else
//            page--
//        mAdapter.notifyDataSetChanged(mDataList)
//        // 数据完更多数据，一定要掉用这个方法。
//        // 第一个参数：表示此次数据是否为空。
//        // 第二个参数：表示是否还有更多数据。
//        mRecyclerView!!.loadMoreFinish(true, mMaxToal > page * size)
//        calulate()
//    }
//
//    override fun updateShoppingCartParam(): Any {
//        val map = TreeMap<String,Any>()
//        map.put(
//            "orgId",
//            if (login == null) "" else login.lastStoreId
//        )
//        map.put("id", cartInfo!!.id)
//        map.put("goodsId", cartInfo!!.goodsId)
//        map.put("goodsNum", cartInfo!!.goodsNum)
//        return map
//    }
//
//    override fun updateShoppingCartSuccess(isRe: Boolean, mCallBackVo: Any) {
//        if (textview != null && cartInfo != null) {
//            mDataList.removeAt(mPosition)
//            mDataList.add(mPosition, cartInfo!!)
//            (textview as TextView).text = cartInfo!!.goodsNum.toString()
//            //            mAdapter.notifyDataSetChanged();
//            calulate()
//        }
//        cartInfo = null
//        textview = null
//        mPosition = -1
//    }
//
//    override fun updateShoppingCartFailed(isRe: Boolean, message: String) {
//        ToastUtils.showShort(this, message)
//        textview = null
//        cartInfo = null
//        mPosition = -1
//    }
//
//    override fun deleteShoppingCartParam(): Any {
//        val map = TreeMap<String,Any>()
//        map.put(
//            "orgId",
//            if (login == null ) "" else login.lastStoreId
//        )
//        map.put("id", cartInfo!!.id)
//        return map
//    }
//
//    override fun deleteShoppingCartSuccess(isRe: Boolean, mCallBackVo: Any) {
//        mDataList.removeAt(mPosition)
//        mAdapter.notifyDataSetChanged()
//        calulate()
//        cartInfo = null
//        mPosition = -1
//    }
//
//    override fun deleteShoppingCartFailed(isRe: Boolean, message: String) {
//        ToastUtils.showShort(this, message)
//        cartInfo = null
//        mPosition = -1
//    }
}
