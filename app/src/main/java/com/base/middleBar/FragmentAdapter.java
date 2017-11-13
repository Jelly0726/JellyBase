package com.base.middleBar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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
}
