package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.jelly.baselibrary.androidPicker.AddressPickTask;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.AddaddressActivityBinding;
import com.jelly.mvp.contact.OperaAddressContact;
import com.jelly.mvp.presenter.OperaAddressPresenter;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/10/13.
 */

public class AddressAddActivity extends BaseActivityImpl<OperaAddressContact.View
        ,OperaAddressContact.Presenter, AddaddressActivityBinding>
        implements OperaAddressContact.View, View.OnClickListener {
    private String mProvince="";
    private String mCity="";
    private String mCounty="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    @Override
    public OperaAddressContact.Presenter initPresenter() {
        return new OperaAddressPresenter();
    }
    @Override
    public OperaAddressContact.View initIBView() {
        return this;
    }

    @Override
    public <T> ObservableTransformer<T, T> bindLifecycle() {
        return lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY);
    }
    private void iniView(){
        getBinding().leftBack.setOnClickListener(this);
        getBinding().addressTv.setOnClickListener(this);
        getBinding().commitTv.setOnClickListener(this);

        getBinding().addressTvs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String address=mProvince+mCity+mCounty;
                if (s.toString().trim().length()<address.length()){
                    getBinding().addressTvs.setText(address);
                    getBinding().addressTvs.setSelection(address.length());
                }

            }
        });
    }
    @Override
    public void onBackPressed() {
        closeProgress();
        super.onBackPressed();
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.address_tv:
                onAddressPicker();
                break;
            case R.id.commit_tv:
                String phone= getBinding().phoneEdit.getText().toString().trim();
                String name= getBinding().nameEdit.getText().toString();
                String address= getBinding().addressTvs.getText().toString();
                if (TextUtils.isEmpty(phone)||TextUtils.isEmpty(name)||TextUtils.isEmpty(address)){
                    ToastUtils.showToast(this,"姓名、电话、收货地址不能为空!");
                    return;
                }
                presenter.operaAddress();
                break;
        }
    }
    public void onAddressPicker() {
        AddressPickTask task = new AddressPickTask(this);
        task.setHideProvince(false);
        task.setHideCounty(false);
        task.setCallback(new AddressPickTask.Callback() {
            @Override
            public void onAddressInitFailed() {
                ToastUtils.showToast(AddressAddActivity.this, "数据初始化失败");
            }

            @Override
            public void onAddressPicked(Province province, City city, County county) {
                if (county == null) {
                    //showToast(province.getAreaName() + city.getAreaName());
                    String addre=province.getAreaName() + city.getAreaName();
                    getBinding().addressTv.setText(addre);
                    String str= getBinding().addressTvs.getText().toString().trim();
                    if(str.length()>0){
                        str=str.replace(mProvince,province.getAreaName());
                        str=str.replace(mCity,city.getAreaName());
                    }else {
                        str=addre;
                    }
                    getBinding().addressTvs.setText(str);
                    getBinding().addressTvs.setSelection(str.length());
                    mProvince=province.getAreaName();
                    mCity=city.getAreaName();
                } else {
                    //showToast(province.getAreaName() + city.getAreaName() + county.getAreaName());
                    String addre=province.getAreaName() + city.getAreaName()+county.getAreaName();
                    getBinding().addressTv.setText(addre);
                    String str= getBinding().addressTvs.getText().toString().trim();
                    if(str.length()>0){
                        str=str.replace(mProvince,province.getAreaName());
                        str=str.replace(mCity,city.getAreaName());
                        str=str.replace(mCounty,county.getAreaName());
                    }else {
                        str=addre;
                    }
                    getBinding().addressTvs.setText(str);
                    getBinding().addressTvs.setSelection(str.length());
                    mProvince=province.getAreaName();
                    mCity=city.getAreaName();
                    mCounty=county.getAreaName();
                }
            }
        });
        task.execute("福建", "厦门", "湖里");
    }

    @Override
    public Object operaAddressParam() {
        String phone= getBinding().phoneEdit.getText().toString().trim();
        String name= getBinding().nameEdit.getText().toString();
        String address= getBinding().addressTvs.getText().toString();
        Map map=new TreeMap();
        map.put("name",name);
        map.put("phone",phone);
        map.put("province",mProvince);
        map.put("city",mCity);
        map.put("area",mCounty);
        map.put("address",address.replace(mProvince+mCity+mCounty,""));
        map.put("isdefault", getBinding().defaultCheckBox.isChecked());
        map.put("operatype",0);
        return map;
    }

    @Override
    public void operaAddressSuccess(Object mCallBackVo) {
        ToastUtils.showToast(this, (String) mCallBackVo);
        finish(2000);
    }

    @Override
    public void operaAddressFailed(String message) {
        ToastUtils.showToast(this,message);
    }
}
