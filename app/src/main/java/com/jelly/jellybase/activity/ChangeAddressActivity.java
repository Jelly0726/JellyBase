package com.jelly.jellybase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.addressmodel.Address;
import com.jelly.baselibrary.addressmodel.Area;
import com.jelly.baselibrary.addressmodel.City;
import com.jelly.baselibrary.addressmodel.Province;
import com.jelly.baselibrary.androidPicker.AddressAdapter;
import com.jelly.baselibrary.moshi.JsonTool;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.recyclerViewUtil.SimpleItemDecoration;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.ChangeAddressActivityBinding;
import com.yanzhenjie.album.impl.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.util.ConvertUtils;
import systemdb.PositionEntity;

/**
 * Created by Administrator on 2018/1/11.
 * 地址选择
 */

public class ChangeAddressActivity extends BaseActivity<ChangeAddressActivityBinding> implements View.OnClickListener {
    private PositionEntity entity;
    private GridLayoutManager layoutManager;
    private AddressAdapter adapter;
    private List<Province> mList=new ArrayList<Province>();
    private Province province;
    private City city;
    private Area district;
    private Address address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entity= (PositionEntity) getIntent().getSerializableExtra("entity");
        address= (Address) getIntent().getParcelableExtra("address");
        iniData();
        iniXRefreshView();
    }
    private void iniData(){
        getBinding().provinceRb.setOnClickListener(this);
        getBinding().cityRb.setOnClickListener(this);
        getBinding().addressTv.setOnClickListener(this);
        if (entity!=null){
            getBinding().addressTv.setText(entity.district);
        }
    }
    public void onClick(View v){
        if (AntiShake.check(v.getId()))return;
        Intent intent;
        switch (v.getId()){
            case R.id.province_rb:
                getBinding().provinceRb.setChecked(true);
                getBinding().provinceRb.setText("请选择");
                province=null;
                city=null;
                district =null;
                getBinding().cityRb.setText("");
                getBinding().cityRb.setVisibility(View.GONE);
                getBinding().districtRb.setText("");
                getBinding().districtRb.setVisibility(View.GONE);
                adapter.setData(mList);
                break;
            case R.id.city_rb:
                getBinding().cityRb.setChecked(true);
                getBinding().cityRb.setText("请选择");
                city=null;
                district =null;
                getBinding().districtRb.setText("");
                getBinding().districtRb.setVisibility(View.GONE);
                adapter.setData(province.getCities());
                break;
            case R.id.address_tv:
                setResult(getIntent().getIntExtra("rresultCode",-1),new Intent());
                finish();
                break;
        }
    }
    private void iniXRefreshView(){
        try {
            String json = ConvertUtils.toString(getAssets().open("city.json"));
            mList.addAll(JsonTool.get().toList(json, Province.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter=new AddressAdapter(this,mList);
        adapter.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(View view,int position) {
                if (province==null){
                    province=mList.get(position);
                    adapter.setData(province.getCities());
                    getBinding().provinceRb.setText(province.getAreaName());
                    getBinding().cityRb.setChecked(true);
                    getBinding().cityRb.setVisibility(View.VISIBLE);
                    getBinding().cityRb.setText("请选择");
                }else  if (city==null){
                    city=province.getCities().get(position);
                    adapter.setData(city.getCounties());
                    getBinding().cityRb.setText(city.getAreaName());
                    getBinding().districtRb.setChecked(true);
                    getBinding().districtRb.setVisibility(View.VISIBLE);
                    getBinding().districtRb.setText("请选择");
                }else {
                    district =city.getCounties().get(position);
                    Address address=new Address();
                    address.setProvince(province);
                    address.setCity(city);
                    address.setDistrict(district);
                    getIntent().putExtra("address",address);
                    setResult(getIntent().getIntExtra("rresultCode",-1),getIntent());
                    finish();
                }
            }
        });
        if (address!=null){
            if (!TextUtils.isEmpty(address.getProvince().getAreaName())) {
                getBinding().provinceRb.setText(address.getProvince().getAreaName());
                getBinding().cityRb.setChecked(true);
                getBinding().cityRb.setVisibility(View.VISIBLE);
                getBinding().cityRb.setText("请选择");
                for (Province pro : mList) {
                    if (pro.getAreaName().contains(address.getProvince().getAreaName())) {
                        province = pro;
                        adapter.setData(province.getCities());
                        break;
                    }
                }
                if (!TextUtils.isEmpty(address.getCity().getAreaName())) {
                    getBinding().cityRb.setText(address.getCity().getAreaName());
                    getBinding().districtRb.setChecked(true);
                    getBinding().districtRb.setVisibility(View.VISIBLE);
                    getBinding().districtRb.setText("请选择");
                    for (City pro : province.getCities()) {
                        if (pro.getAreaName().contains(address.getCity().getAreaName())) {
                            city = pro;
                            adapter.setData(city.getCounties());
                            break;
                        }
                    }
                    if (!TextUtils.isEmpty(address.getDistrict().getAreaName())) {
                        getBinding().districtRb.setText(address.getDistrict().getAreaName());
                        for (Area pro : city.getCounties()) {
                            if (pro.getAreaName().contains(address.getDistrict().getAreaName())) {
                                district = pro;
                                break;
                            }
                        }
                    }
                }
            }
        }else if (entity!=null) {
            if (!TextUtils.isEmpty(entity.province)) {
                getBinding().provinceRb.setText(entity.province);
                getBinding().cityRb.setChecked(true);
                getBinding().cityRb.setVisibility(View.VISIBLE);
                getBinding().cityRb.setText("请选择");
                for (Province pro : mList) {
                    if (pro.getAreaName().contains(entity.province)) {
                        province = pro;
                        adapter.setData(province.getCities());
                        break;
                    }
                }
                if (!TextUtils.isEmpty(entity.city)) {
                    getBinding().cityRb.setText(entity.city);
                    getBinding().districtRb.setChecked(true);
                    getBinding().districtRb.setVisibility(View.VISIBLE);
                    getBinding().districtRb.setText("请选择");
                    for (City pro : province.getCities()) {
                        if (pro.getAreaName().contains(entity.city)) {
                            city = pro;
                            adapter.setData(city.getCounties());
                            break;
                        }
                    }
                    if (!TextUtils.isEmpty(entity.district)) {
                        getBinding().districtRb.setText(entity.district);
                        for (Area pro : city.getCounties()) {
                            if (pro.getAreaName().contains(entity.district)) {
                                district = pro;
                                break;
                            }
                        }
                    }
                }
            }
        }
        getBinding().recyclerView.setHasFixedSize(true);
        getBinding().recyclerView.setNestedScrollingEnabled(false);
        layoutManager = new GridLayoutManager(this,3);
        getBinding().recyclerView.setLayoutManager(layoutManager);
        getBinding().recyclerView.addItemDecoration(new SimpleItemDecoration(0,3, SimpleItemDecoration.NONE));
        getBinding().recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        setResult(getIntent().getIntExtra("rresultCode",-1),getIntent());
        super.onBackPressed();
    }
}
