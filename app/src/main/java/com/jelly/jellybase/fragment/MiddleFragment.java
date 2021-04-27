package com.jelly.jellybase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.jelly.baselibrary.addressmodel.Address;
import com.base.BaseApplication;
import com.jelly.baselibrary.middleBar.MiddleBarItem;
import com.jelly.baselibrary.middleBar.MiddleBarLayout;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.BaseFragment;
import com.jelly.baselibrary.FragmentAdapter;
import com.google.gson.Gson;
import com.jelly.jellybase.R;
import com.jelly.jellybase.activity.BottomBarActivity;
import com.jelly.jellybase.activity.ChangeAddressActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import systemdb.PositionEntity;


/**
 * Created by Administrator on 2017/9/18.
 */

public class MiddleFragment extends BaseFragment{
    private static final int areaRresultCode=0;
    private PositionEntity entity;
    private Address address;
    @BindView(R.id.address_tv)
    TextView address_tv;
    @BindView(R.id.changeAddress_tv)
    TextView changeAddress_tv;
    @BindView(R.id.vp_content)
    ViewPager mVpContent;
    @BindView(R.id.bbl)
    MiddleBarLayout mBottomBarLayout;
    private FragmentAdapter myAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>();
    @Override
    public int getLayoutId() {
        return R.layout.location_fragment;
    }
    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void setData(String json) {
        entity=new Gson().fromJson(json,PositionEntity.class);
        if (getActivity()!=null&&isFragmentVisible())
            iniData();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniData();
        initViewPagerData();
        initViewPagerListener();
    }
    private void iniData(){
        if (address!=null){
            address_tv.setText(address.getDistrict().getAreaName());
        }else if (entity!=null) {
            address_tv.setText(entity.district);
        }
    }
    @OnClick({R.id.changeAddress_tv})
    public void onClick(View v){
        if (AntiShake.check(v.getId()))return;
        Intent intent;
        switch (v.getId()){
            case R.id.changeAddress_tv:
                intent=new Intent(BaseApplication.getInstance(),ChangeAddressActivity.class);
                intent.putExtra("entity",entity);
                intent.putExtra("address",address);
                intent.putExtra("rresultCode",areaRresultCode);
                startActivityForResult(intent,areaRresultCode);
                break;
        }
    }
    private void initViewPagerData() {
        mFragmentList.clear();
        LocaFragment locaFragment1 = new LocaFragment();
        mFragmentList.add(locaFragment1);

        LocaFragment locaFragment2 = new LocaFragment();
        mFragmentList.add(locaFragment2);

        LocaFragment locaFragment3 = new LocaFragment();
        mFragmentList.add(locaFragment3);
    }

    private void initViewPagerListener() {
        myAdapter= new FragmentAdapter(getChildFragmentManager(),mFragmentList);
        mVpContent.setAdapter(myAdapter);
        mBottomBarLayout.setViewPager(mVpContent);
        mBottomBarLayout.setOnItemSelectedListener(new MiddleBarLayout.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final MiddleBarItem bottomBarItem, int position) {
                BaseFragment baseFragment= (BaseFragment) mFragmentList.get(position);
                if (entity!=null)
                    baseFragment.setData(new Gson().toJson(entity));
            }
        });
    }
    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data==null)return;
        if (resultCode==this.areaRresultCode&&requestCode==this.areaRresultCode){
            ((BottomBarActivity)getActivity()).onActivityResult(requestCode, resultCode, data);
            address=data.getParcelableExtra("address");
            if (address!=null){
                address_tv.setText(address.getDistrict().getAreaName());
            }else if (entity!=null) {
                address_tv.setText(entity.district);
            }
        }
    }
}
