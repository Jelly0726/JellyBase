package com.jelly.jellybase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.addressmodel.Address;
import com.base.applicationUtil.MyApplication;
import com.base.middleBar.FragmentAdapter;
import com.base.middleBar.MiddleBarItem;
import com.base.middleBar.MiddleBarLayout;
import com.base.multiClick.AntiShake;
import com.base.view.BackInterface;
import com.base.view.BaseFragment;
import com.google.gson.Gson;
import com.jelly.jellybase.R;
import com.jelly.jellybase.activity.BottomBarActivity;
import com.jelly.jellybase.activity.ChangeAddressActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import systemdb.PositionEntity;


/**
 * Created by Administrator on 2017/9/18.
 */

public class MiddleFragment extends BaseFragment implements BackInterface {
    private static final int areaRresultCode=0;
    private View mRootView;
    private Unbinder unbinder;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.location_fragment, container, false);
        unbinder= ButterKnife.bind(this,mRootView);
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
    @Override
    public void setData(String json) {
        entity=new Gson().fromJson(json,PositionEntity.class);
        if (getActivity()!=null)
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
                intent=new Intent(MyApplication.getMyApp(),ChangeAddressActivity.class);
                intent.putExtra("entity",entity);
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
    private BaseFragment mBaseFragment;
    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        this.mBaseFragment = selectedFragment;
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
