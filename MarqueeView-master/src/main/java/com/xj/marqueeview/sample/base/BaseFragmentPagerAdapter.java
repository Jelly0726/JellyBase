package com.xj.marqueeview.sample.base;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * @author xujun  on 18/7/2018.
 */
public class BaseFragmentPagerAdapter<T extends Fragment> extends FragmentStatePagerAdapter {

    private final List<T> mDatas;
    protected String[] mTitles;

    public BaseFragmentPagerAdapter(FragmentManager fm, String[] titles, List<T> datas) {
        super(fm);
        mTitles = titles;
        if (titles == null) {
            mTitles = new String[]{};
        }
        mDatas = datas;
    }

    public void setTitles(String[] titles) {
        mTitles = titles;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position >= 0 && position < mTitles.length) {
            return mTitles[position];
        } else {
            return "";
        }

    }

    @Override
    public Fragment getItem(int position) {
        return mDatas.get(position);
    }
}
