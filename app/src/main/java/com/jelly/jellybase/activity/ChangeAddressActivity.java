package com.jelly.jellybase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.base.addressmodel.Address;
import com.base.addressmodel.Area;
import com.base.addressmodel.City;
import com.base.addressmodel.Province;
import com.base.multiClick.AntiShake;
import com.base.view.BaseActivity;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.view.SimpleItemDecoration;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.AddressAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.util.ConvertUtils;
import systemdb.PositionEntity;

/**
 * Created by Administrator on 2018/1/11.
 * 地址选择
 */

public class ChangeAddressActivity extends BaseActivity {
    private PositionEntity entity;
    @BindView(R.id.address_tv)
    TextView address_tv;
    @BindView(R.id.address_gp)
    RadioGroup address_gp;
    @BindView(R.id.province_rb)
    RadioButton province_rb;
    @BindView(R.id.city_rb)
    RadioButton city_rb;
    @BindView(R.id.district_rb)
    RadioButton district_rb;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
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
    @Override
    public int getLayoutId(){
        return R.layout.change_address_activity;
    }
    private void iniData(){
        if (entity!=null){
            address_tv.setText(entity.district);
        }
    }
    @OnClick({R.id.province_rb,R.id.city_rb,R.id.address_tv})
    public void onClick(View v){
        if (AntiShake.check(v.getId()))return;
        Intent intent;
        switch (v.getId()){
            case R.id.province_rb:
                province_rb.setChecked(true);
                province_rb.setText("请选择");
                province=null;
                city=null;
                district =null;
                city_rb.setText("");
                city_rb.setVisibility(View.GONE);
                district_rb.setText("");
                district_rb.setVisibility(View.GONE);
                adapter.setData(mList);
                break;
            case R.id.city_rb:
                city_rb.setChecked(true);
                city_rb.setText("请选择");
                city=null;
                district =null;
                district_rb.setText("");
                district_rb.setVisibility(View.GONE);
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
            mList.addAll(JSON.parseArray(json, Province.class));
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
                    province_rb.setText(province.getAreaName());
                    city_rb.setChecked(true);
                    city_rb.setVisibility(View.VISIBLE);
                    city_rb.setText("请选择");
                }else  if (city==null){
                    city=province.getCities().get(position);
                    adapter.setData(city.getCounties());
                    city_rb.setText(city.getAreaName());
                    district_rb.setChecked(true);
                    district_rb.setVisibility(View.VISIBLE);
                    district_rb.setText("请选择");
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
                province_rb.setText(address.getProvince().getAreaName());
                city_rb.setChecked(true);
                city_rb.setVisibility(View.VISIBLE);
                city_rb.setText("请选择");
                for (Province pro : mList) {
                    if (pro.getAreaName().contains(address.getProvince().getAreaName())) {
                        province = pro;
                        adapter.setData(province.getCities());
                        break;
                    }
                }
                if (!TextUtils.isEmpty(address.getCity().getAreaName())) {
                    city_rb.setText(address.getCity().getAreaName());
                    district_rb.setChecked(true);
                    district_rb.setVisibility(View.VISIBLE);
                    district_rb.setText("请选择");
                    for (City pro : province.getCities()) {
                        if (pro.getAreaName().contains(address.getCity().getAreaName())) {
                            city = pro;
                            adapter.setData(city.getCounties());
                            break;
                        }
                    }
                    if (!TextUtils.isEmpty(address.getDistrict().getAreaName())) {
                        district_rb.setText(address.getDistrict().getAreaName());
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
                province_rb.setText(entity.province);
                city_rb.setChecked(true);
                city_rb.setVisibility(View.VISIBLE);
                city_rb.setText("请选择");
                for (Province pro : mList) {
                    if (pro.getAreaName().contains(entity.province)) {
                        province = pro;
                        adapter.setData(province.getCities());
                        break;
                    }
                }
                if (!TextUtils.isEmpty(entity.city)) {
                    city_rb.setText(entity.city);
                    district_rb.setChecked(true);
                    district_rb.setVisibility(View.VISIBLE);
                    district_rb.setText("请选择");
                    for (City pro : province.getCities()) {
                        if (pro.getAreaName().contains(entity.city)) {
                            city = pro;
                            adapter.setData(city.getCounties());
                            break;
                        }
                    }
                    if (!TextUtils.isEmpty(entity.district)) {
                        district_rb.setText(entity.district);
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleItemDecoration(0,3, SimpleItemDecoration.NONE));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        setResult(getIntent().getIntExtra("rresultCode",-1),getIntent());
        super.onBackPressed();
    }
}
