package com.base.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/21.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragmentList = new ArrayList<>();
    public FragmentAdapter(FragmentManager fm,List<Fragment> mFragmentList) {
        super(fm);
        this.mFragmentList=mFragmentList;
    }

    public void setFragmentData(List<Fragment> mFragmentList){
        this.mFragmentList=mFragmentList;
        notifyDataSetChanged();
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment =mFragmentList.get(position);
        if (fragment instanceof BaseFragment){
            BaseFragment baseFragment= (BaseFragment) mFragmentList.get(position);
            if (!baseFragment.isReuseView())
                super.destroyItem(container, position, object);
        }else super.destroyItem(container, position, object);
    }
}
