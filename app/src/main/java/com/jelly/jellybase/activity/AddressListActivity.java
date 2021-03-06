package com.jelly.jellybase.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.recyclerViewUtil.ItemDecoration;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.AddressListAdapter;
import com.jelly.jellybase.databinding.AddressreceiverActivityBinding;
import com.jelly.jellybase.datamodel.RecevierAddress;
import com.jelly.mvp.contact.AddressContact;
import com.jelly.mvp.presenter.AddressPresenter;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigText;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.TextParams;
import com.yanzhenjie.album.impl.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2017/10/13.
 */

public class AddressListActivity extends BaseActivityImpl<AddressContact.View
        ,AddressContact.Presenter, AddressreceiverActivityBinding>
        implements AddressContact.View,View.OnClickListener{
    private LinearLayoutManager layoutManager;
    private AddressListAdapter adapter;
    private List<RecevierAddress> mList =new ArrayList<>();
    private int startRownumber=0;
    private int pageSize=10;
    private int addressid=0;
    private int postion=0;
    private int operatype=0;//操作类型(0新增；1修改；2删除)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
        iniXRefreshView();
    }
    @Override
    public AddressContact.Presenter initPresenter() {
        return new AddressPresenter();
    }
    @Override
    public AddressContact.View initIBView() {
        return this;
    }

    @Override
    public LifecycleOwner bindLifecycle() {
        return this;
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("sss","onResume");
        startRownumber=0;
        presenter.getAddressList(true);
    }

    private void iniView (){
        getBinding().leftBack.setOnClickListener(this);
        getBinding().addAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (AntiShake.check(v.getId()))return;
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.add_address:
                Intent intent=new Intent(this,AddressAddActivity.class);
                startActivity(intent);

                break;
        }
    }
    private void iniXRefreshView(){
        adapter=new AddressListAdapter(this,mList);
        getBinding().xrefreshview.setPullLoadEnable(true);
        getBinding().recyclerViewTestRv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        getBinding().recyclerViewTestRv.setLayoutManager(layoutManager);
        Rect rect=new Rect();
        rect.bottom=22;
        rect.top=0;
        rect.left=0;
        rect.right=0;
        getBinding().recyclerViewTestRv.addItemDecoration(new ItemDecoration(rect,1,-1, ItemDecoration.NONE));
        // 静默加载模式不能设置footerview
        getBinding().recyclerViewTestRv.setAdapter(adapter);
        getBinding().xrefreshview.setPinnedTime(1000);
        getBinding().xrefreshview.setMoveForHorizontal(true);
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.setDeleteAddress(new AddressListAdapter.DeleteAddress() {
            @Override
            public void delete(final int id,final int position) {

                CircleDialog.Builder circleDialog = new CircleDialog.Builder()
                        .configDialog(new ConfigDialog() {
                            @Override
                            public void onConfig(DialogParams params) {
                                params.width = 0.6f;
                            }
                        })
                        .setCanceledOnTouchOutside(false)
                        .setCancelable(false)
                        .setTitle("删除！")
                        .configText(new ConfigText() {
                            @Override
                            public void onConfig(TextParams params) {
                                params.gravity = Gravity.CENTER;
                                params.textColor = Color.parseColor("#FF1F50F1");
                                params.padding = new int[]{20, 0, 20, 0};
                            }
                        })
                        .setText("确定要删除该地址？")
                        .setPositive("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addressid=id;
                                operatype=2;
                                postion=position;
                                presenter.operaAddress();
                            }
                        })
                        .setNegative("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                circleDialog.show(getSupportFragmentManager());

            }
        });

        getBinding().xrefreshview.setXRefreshViewListener(simpleXRefreshListener);
    }
    /**
     * 滑动刷新
     */
    private XRefreshView.SimpleXRefreshListener simpleXRefreshListener =new XRefreshView.SimpleXRefreshListener() {

        @Override
        public void onRefresh(boolean isPullDown) {
            startRownumber=0;
            presenter.getAddressList(true);
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            startRownumber++;
            presenter.getAddressList(false);
        }
    };
    private OnItemClickListener onItemClickListener=new OnItemClickListener(){

        @Override
        public void onItemClick(View view,int position) {
            if (getIntent().getIntExtra("requestCode",-1)==-1)return;
            RecevierAddress recevierAddress=mList.get(position);
            Intent intent=new Intent();
            intent.putExtra("data",recevierAddress);
            setResult(getIntent().getIntExtra("requestCode",-1),intent);
            finish();
//            Intent intent=new Intent(MyApplication.getMyApp(), AddressAddActivity.class);
//            startActivity(intent);
        }
    };
    @Override
    public Object operaAddressParam() {
        Map map=new TreeMap();
        map.put("addressid",addressid);
        map.put("operatype",operatype);
        return map;
    }

    @Override
    public void operaAddressSuccess(Object mCallBackVo) {
        ToastUtils.showToast(this, (String) mCallBackVo);
        mList.remove(postion);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void operaAddressFailed(String message) {
        ToastUtils.showToast(this,message);
    }

    @Override
    public Object getAddressListParam() {
        Map map=new TreeMap();
        map.put("startRownumber",(startRownumber*pageSize+1)+"");
        map.put("pageSize",pageSize+"");
        return map;
    }

    @Override
    public void getAddressListSuccess(boolean isRefresh, Object mCallBackVo) {
        List list= (List) mCallBackVo;
        if (isRefresh){
            mList.clear();
            getBinding().xrefreshview.stopRefresh();
            if (list.size()<pageSize){
                getBinding().xrefreshview.setLoadComplete(true);
            }else {
                getBinding().xrefreshview.setLoadComplete(false);
            }
        }else {
            if (list.size()==0){
                getBinding().xrefreshview.setLoadComplete(true);
            }else
                getBinding().xrefreshview.stopLoadMore();
        }
        mList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getAddressListFailed(boolean isRefresh, String message) {
        if (isRefresh){
            getBinding().xrefreshview.stopRefresh();
        }else {
            getBinding().xrefreshview.stopLoadMore();
        }
        ToastUtils.showToast(this,message);
    }
}
