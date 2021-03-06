package com.jelly.jellybase.shopcar;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.listener.OnBottomLoadMoreTime;
import com.andview.refreshview.listener.OnTopRefreshTime;
import com.jelly.baselibrary.BaseFragment;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.ShopcarFragmentBinding;
import com.jelly.jellybase.shopcar.Utils.UtilTool;
import com.jelly.jellybase.shopcar.adapter.ShopcatAdapter;
import com.jelly.jellybase.shopcar.entity.GoodsInfo;
import com.jelly.jellybase.shopcar.entity.StoreInfo;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



/**
 * Created by Administrator on 2017/9/18.
 */

public class ShopCarFragment extends BaseFragment<ShopcarFragmentBinding> implements
        ShopcatAdapter.CheckInterface, ShopcatAdapter.ModifyCountInterface,
        ShopcatAdapter.GroupEditorListener, View.OnClickListener {
    private int mTotalItemCount;
    private Context mcontext;
    private double mtotalPrice = 0.00;
    private int mtotalCount = 0;
    //false就是编辑，ture就是完成
    private boolean flag = false;
    private ShopcatAdapter adapter;
    private List<StoreInfo> groups; //组元素的列表
    private Map<String, List<GoodsInfo>> childs; //子元素的列表
    //选中的商家
    private List<String> onlyStore = new ArrayList<>();
    /**
     * Fragment 的构造函数。
     */
    public ShopCarFragment() {
    }
    public static ShopCarFragment newInstance() {
        return new ShopCarFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniView();
        initData();
        initEvents();
    }

    @Override
    public void setData(String json) {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void iniView(){
        getBinding().shopcarAcitonBar.leftBack.setOnClickListener(this);
        getBinding().allCheckBox.setOnClickListener(this);
        getBinding().goPay.setOnClickListener(this);
        getBinding().shareGoods.setOnClickListener(this);
        getBinding().collectGoods.setOnClickListener(this);
        getBinding().delGoods.setOnClickListener(this);
        getBinding().shopcarAcitonBar.actionBarEdit.setOnClickListener(this);
    }
    private void initEvents() {
        getBinding().xrefreshview.setPullLoadEnable(false);
        getBinding().xrefreshview.setPinnedTime(1000);
        getBinding().xrefreshview.setMoveForHorizontal(false);
        getBinding().xrefreshview.setOnTopRefreshTime(new OnTopRefreshTime() {

            @Override
            public boolean isTop() {
                if (getBinding().listView.getFirstVisiblePosition() == 0) {
                    View firstVisibleChild = getBinding().listView.getChildAt(0);
                    return firstVisibleChild.getTop() >= 0;
                }
                //没有到达顶部则返回false
                return false;
            }
        });
        getBinding().xrefreshview.setOnBottomLoadMoreTime(new OnBottomLoadMoreTime() {

            @Override
            public boolean isBottom() {
                if (getBinding().listView.getLastVisiblePosition() == mTotalItemCount - 1) {
                    View lastChild = getBinding().listView.getChildAt(getBinding().listView.getChildCount() - 1);
                    return (lastChild.getBottom() + getBinding().listView.getPaddingBottom()) <= getBinding().listView.getMeasuredHeight();
                }
                //没有到达底部则返回false
                return false;
            }
        });
        getBinding().xrefreshview.setXRefreshViewListener(simpleXRefreshListener);

        adapter = new ShopcatAdapter(groups, childs, mcontext);
        adapter.setCheckInterface(this);//关键步骤1：设置复选框的接口
        adapter.setModifyCountInterface(this); //关键步骤2:设置增删减的接口
        adapter.setGroupEditorListener(this);//关键步骤3:监听组列表的编辑状态
        adapter.setOnGroupClickListener(onGroupClickListener);//设置组点击接口
        adapter.setOnChildClickListener(onChildClickListener);//设置组员点击接口
        getBinding().listView.setGroupIndicator(null); //设置属性 GroupIndicator 去掉向下箭头
        getBinding().listView.setAdapter(adapter);
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            getBinding().listView.expandGroup(i); //关键步骤4:初始化，将ExpandableListView以展开的方式显示
        }
        getBinding().listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mTotalItemCount = totalItemCount;
            }
        });
    }
    /**
     * 设置购物车的数量
     */
    private void setCartNum() {
        int count = 0;
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            group.setChoosed(getBinding().allCheckBox.isChecked());
            List<GoodsInfo> Childs = childs.get(group.getId());
            for (GoodsInfo childs : Childs) {
                count++;
            }
        }

        //购物车已经清空
        if (count == 0) {
            clearCart();
        } else {
            getBinding().shopcarAcitonBar.shoppingcatNum.setText("购物车(" + count + ")");
        }

    }

    private void clearCart() {
        getBinding().shopcarAcitonBar.shoppingcatNum.setText("购物车(0)");
        getBinding().shopcarAcitonBar.actionBarEdit.setVisibility(View.GONE);
        getBinding().llCart.setVisibility(View.GONE);
        getBinding().layoutEmptyShopcart.getRoot().setVisibility(View.VISIBLE);//这里发生过错误
    }

    /**
     * 模拟数据<br>
     * 遵循适配器的数据列表填充原则，组元素被放在一个list中，对应着组元素的下辖子元素被放在Map中
     * 其Key是组元素的Id
     */
    private void initData() {
        mcontext = this.getContext();
        groups = new ArrayList<StoreInfo>();
        childs = new HashMap<String, List<GoodsInfo>>();
        for (int i = 0; i < 5; i++) {
            groups.add(new StoreInfo(i + "", "小马的第" + (i + 1) + "号当铺"));
            List<GoodsInfo> goods = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                int[] img = {R.drawable.shopcar_cmaz, R.drawable.shopcar_cmaz, R.drawable.shopcar_cmaz, R.drawable.shopcar_cmaz, R.drawable.shopcar_cmaz, R.drawable.shopcar_cmaz};
                //i-j 就是商品的id， 对应着第几个店铺的第几个商品，1-1 就是第一个店铺的第一个商品
                goods.add(new GoodsInfo(i + "-" + j, "商品",
                        groups.get(i).getName() + "的第" + (j + 1) + "个商品",
                        255.00 + new Random().nextInt(1500),
                        1555 + new Random().nextInt(3000),
                        "第一排", "出头天者", img[j],
                        new Random().nextInt(100),100));
            }
            childs.put(groups.get(i).getId(), goods);
        }
    }

    /**
     * 删除操作
     * 1.不要边遍历边删除,容易出现数组越界的情况
     * 2.把将要删除的对象放进相应的容器中，待遍历完，用removeAll的方式进行删除
     */
    private void doDelete() {
        List<StoreInfo> toBeDeleteGroups = new ArrayList<StoreInfo>(); //待删除的组元素
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            if (group.isChoosed()) {
                toBeDeleteGroups.add(group);
            }
            List<GoodsInfo> toBeDeleteChilds = new ArrayList<GoodsInfo>();//待删除的子元素
            List<GoodsInfo> child = childs.get(group.getId());
            for (int j = 0; j < child.size(); j++) {
                if (child.get(j).isChoosed()) {
                    toBeDeleteChilds.add(child.get(j));
                }
            }
            child.removeAll(toBeDeleteChilds);
        }
        groups.removeAll(toBeDeleteGroups);
        //重新设置购物车
        setCartNum();
        adapter.notifyDataSetChanged();

    }
    /**
     * @param groupPosition 组元素的位置
     * @param isChecked     组元素的选中与否
     *                      思路:组元素被选中了，那么下辖全部的子元素也被选中
     */
    @Override
    public void checkGroup(int groupPosition, boolean isChecked) {
        StoreInfo group = groups.get(groupPosition);
        List<GoodsInfo> child = childs.get(group.getId());
        for (int i = 0; i < child.size(); i++) {
            child.get(i).setChoosed(isChecked);
        }
        if (isCheckAll()) {
            getBinding().allCheckBox.setChecked(true);//全选
        } else {
            getBinding().allCheckBox.setChecked(false);//反选
        }
        adapter.notifyDataSetChanged();
        calulate();
    }

    /**
     * @return 判断组元素是否全选
     */
    private boolean isCheckAll() {
        for (StoreInfo group : groups) {
            if (!group.isChoosed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param groupPosition 组元素的位置
     * @param childPosition 子元素的位置
     * @param isChecked     子元素的选中与否
     */
    @Override
    public void checkChild(int groupPosition, int childPosition, boolean isChecked) {
        boolean allChildSameState = true; //判断该组下面的所有子元素是否处于同一状态
        StoreInfo group = groups.get(groupPosition);
        List<GoodsInfo> child = childs.get(group.getId());
        for (int i = 0; i < child.size(); i++) {
            //不选全中
            if (child.get(i).isChoosed() != isChecked) {
                allChildSameState = false;
                break;
            }
        }

        if (allChildSameState) {
            group.setChoosed(isChecked);//如果子元素状态相同，那么对应的组元素也设置成这一种的同一状态
        } else {
            group.setChoosed(false);//否则一律视为未选中
        }

        if (isCheckAll()) {
            getBinding().allCheckBox.setChecked(true);//全选
        } else {
            getBinding().allCheckBox.setChecked(false);//反选
        }

        adapter.notifyDataSetChanged();
        calulate();

    }

    @Override
    public void doIncrease(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
        GoodsInfo good = (GoodsInfo) adapter.getChild(groupPosition, childPosition);
        int count = good.getCount();
        count++;
        if (count>good.getStockqty()) {
            count=good.getStockqty();
        }
        good.setCount(count);
        ((TextView) showCountView).setText(String.valueOf(count));
//        adapter.notifyDataSetChanged();
        calulate();
    }

    /**
     * @param groupPosition
     * @param childPosition
     * @param showCountView
     * @param isChecked
     */
    @Override
    public void doDecrease(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
        GoodsInfo good = (GoodsInfo) adapter.getChild(groupPosition, childPosition);
        int count = good.getCount();
        if (count == 1) {
            return;
        }
        count--;
        good.setCount(count);
        ((TextView) showCountView).setText("" + count);
//        adapter.notifyDataSetChanged();
        calulate();
    }

    /**
     * @param groupPosition
     * @param childPosition 思路:当子元素=0，那么组元素也要删除
     */
    @Override
    public void childDelete(int groupPosition, int childPosition) {
        StoreInfo group = groups.get(groupPosition);
        List<GoodsInfo> child = childs.get(group.getId());
        child.remove(childPosition);
        if (child.size() == 0) {
            groups.remove(groupPosition);
        }
        adapter.notifyDataSetChanged();
        calulate();


    }

    public void doUpdate(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
        GoodsInfo good = (GoodsInfo) adapter.getChild(groupPosition, childPosition);
        int count = good.getCount();
        ((TextView) showCountView).setText(String.valueOf(count));
//        adapter.notifyDataSetChanged();
        calulate();
    }

    @Override
    public void groupEditor(int groupPosition) {

    }
    public void onClick(View view) {
        AlertDialog dialog;
        switch (view.getId()) {
            case R.id.left_back:
                getActivity().finish();
            case R.id.all_checkBox:
                doCheckAll();
                break;
            case R.id.go_pay:
                if (mtotalCount == 0) {
                    UtilTool.toast(mcontext, "请选择要支付的商品");
                    return;
                }
                if(onlyStore.size()>1){
                    UtilTool.toast(mcontext, "多个商家不能同时支付");
                    return;
                }
                new CircleDialog.Builder()
                        .configDialog(new ConfigDialog() {
                            @Override
                            public void onConfig(DialogParams params) {
                                params.width=0.7f;
                            }
                        })
                        .setCanceledOnTouchOutside(false)
                        .setCancelable(false)
                        .setTitle("提示")
                        .setText("是否生成订单？")
                        .configNegative(new ConfigButton() {
                            @Override
                            public void onConfig(ButtonParams params) {
                                params.textColor= Color.parseColor("#fe1212");
                            }
                        })
                        .setNegative("取消", null)
                        .configPositive(new ConfigButton() {
                            @Override
                            public void onConfig(ButtonParams params) {
                                params.textColor= Color.parseColor("#4abc38");
                            }
                        })
                        .setPositive("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Intent intent=new Intent(getContext(), NConfirmAnOrderActivity.class);
//                                startActivity(intent);
                            }
                        })
                        .show(getActivity().getSupportFragmentManager());

                break;
            case R.id.share_goods:
                if (mtotalCount == 0) {
                    UtilTool.toast(mcontext, "请选择要分享的商品");
                    return;
                }
                UtilTool.toast(mcontext, "分享成功");
                break;
            case R.id.collect_goods:
                if (mtotalCount == 0) {
                    UtilTool.toast(mcontext, "请选择要收藏的商品");
                    return;
                }
                UtilTool.toast(mcontext, "收藏成功");
                break;
            case R.id.del_goods:
                if (mtotalCount == 0) {
                    UtilTool.toast(mcontext, "请选择要删除的商品");
                    return;
                }
                dialog = new AlertDialog.Builder(mcontext).create();
                dialog.setMessage("确认要删除该商品吗?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doDelete();
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                dialog.show();
                break;
            case R.id.actionBar_edit:
                flag = !flag;
                setActionBarEditor();
                setVisiable();
                break;
        }
    }

    /**
     * ActionBar标题上点编辑的时候，只显示每一个店铺的商品修改界面
     * ActionBar标题上点完成的时候，只显示每一个店铺的商品信息界面
     */
    private void setActionBarEditor() {
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            if (group.isActionBarEditor()) {
                group.setActionBarEditor(false);
            } else {
                group.setActionBarEditor(true);
            }
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * 全选和反选
     * 错误标记：在这里出现过错误
     */
    private void doCheckAll() {
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            group.setChoosed(getBinding().allCheckBox.isChecked());
            List<GoodsInfo> child = childs.get(group.getId());
            for (int j = 0; j < child.size(); j++) {
                child.get(j).setChoosed(getBinding().allCheckBox.isChecked());//这里出现过错误
            }
        }
        adapter.notifyDataSetChanged();
        calulate();
    }

    /**
     * 计算商品总价格，操作步骤
     * 1.先清空全局计价,计数
     * 2.遍历所有的子元素，只要是被选中的，就进行相关的计算操作
     * 3.给textView填充数据
     */
    private void calulate() {
        mtotalPrice = 0.00;
        mtotalCount = 0;
        onlyStore.clear();
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            List<GoodsInfo> child = childs.get(group.getId());
            for (int j = 0; j < child.size(); j++) {
                GoodsInfo good = child.get(j);
                if (good.isChoosed()) {
                    mtotalCount++;
                    if(!onlyStore.contains(group.getId())){
                        onlyStore.add(group.getId());
                    }
                    mtotalPrice += good.getPrice() * good.getCount();
                }
            }
        }
        getBinding().totalPrice.setText("¥" + mtotalPrice + "");
        getBinding().goPay.setText("去支付(" + mtotalCount + ")");
        if (mtotalCount == 0) {
            setCartNum();
        } else {
            getBinding().shopcarAcitonBar.shoppingcatNum.setText("购物车(" + mtotalCount + ")");
        }


    }

    private void setVisiable() {
        if (flag) {
            //getViewBinding().orderInfo.setVisibility(View.GONE);
            getBinding().goPay.setVisibility(View.GONE);
            getBinding().shareInfo.setVisibility(View.VISIBLE);
            getBinding().shopcarAcitonBar.actionBarEdit.setText("完成");
        } else {
            //getViewBinding().orderInfo.setVisibility(View.VISIBLE);
            getBinding().goPay.setVisibility(View.VISIBLE);
            getBinding().shareInfo.setVisibility(View.GONE);
            getBinding().shopcarAcitonBar.actionBarEdit.setText("编辑");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter = null;
        childs.clear();
        groups.clear();
        mtotalPrice = 0.00;
        mtotalCount = 0;
    }
    /**
     * 滑动刷新
     */
    private XRefreshView.SimpleXRefreshListener simpleXRefreshListener =new XRefreshView.SimpleXRefreshListener() {

        @Override
        public void onRefresh(boolean isPullDown) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getBinding().xrefreshview.stopRefresh();
                }
            }, 2000);
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //getViewBinding().xrefreshview.setLoadComplete(true);
                    // 刷新完成必须调用此方法停止加载
                    getBinding().xrefreshview.stopLoadMore();
                }
            }, 1000);
        }
    };
    private ShopcatAdapter.OnGroupClickListener onGroupClickListener=new ShopcatAdapter.OnGroupClickListener() {
        @Override
        public void onClick(View view, Object group, int position) {
//            Intent intent=new Intent(MyApplication.getMyApp(), ShopsActivity.class);
//            startActivity(intent);
        }
    };
    private ShopcatAdapter.OnChildClickListener onChildClickListener=new ShopcatAdapter.OnChildClickListener() {
        @Override
        public void onClick(View view, Object child, int position) {
//            Intent intent=new Intent(MyApplication.getMyApp(), ProductDetailsActivity.class);
//            startActivity(intent);
        }
    };
}
