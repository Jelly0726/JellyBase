package com.jelly.baselibrary;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.jelly.baselibrary.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/21.
 * 当Fragment包含ViewPage+Fragment时FragmentAdapter不要继承FragmentStatePagerAdapter
 */

public class FragmentStateAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragmentList = new ArrayList<>();
    private FragmentManager fm;
    public FragmentStateAdapter(FragmentManager fm, List<Fragment> mFragmentList) {
        super(fm);
        this.mFragmentList=mFragmentList;
        this.fm=fm;
    }

    public void setFragmentData(List<Fragment> mFragmentList){
        if(this.mFragmentList != null && fm != null){
            FragmentTransaction ft = fm.beginTransaction();
            for(Fragment f:this.mFragmentList){
                ft.remove(f);
            }
            ft.commit();
            ft=null;
            fm.executePendingTransactions();
        }
        this.mFragmentList=mFragmentList;
        notifyDataSetChanged();
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
    /**
     * @return 是否fragment被系统给detach或者销毁了
     */
    private boolean isFragmentsDetachedOrDestroyed() {
        if (getCount() > 0 && mFragmentList != null && mFragmentList.size() > 0) {
            for (int i = 0; i < mFragmentList.size(); i++) {
                if (mFragmentList.get(i) == null ||
                        mFragmentList.get(i).isDetached() ||
                        !mFragmentList.get(i).isAdded()) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }
    /**
     * @return 是否fragment被系统给detach或者销毁了
     */
    private boolean isFragmentsDetachedOrDestroyed(int position) {
        if (getCount() > 0 &&
                mFragmentList != null &&
                mFragmentList.size() > 0&&
                position<mFragmentList.size()) {
            if (mFragmentList.get(position) == null ||
                    mFragmentList.get(position).isDetached() ||
                    !mFragmentList.get(position).isAdded()) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }
    /**
     * @return 是否fragment被系统给detach或者销毁了
     */
    private boolean isFragmentsDetachedOrDestroyed(Fragment fragment) {
        if (getCount() > 0 &&
                mFragmentList != null &&
                mFragmentList.size() > 0) {
            if (fragment == null ||
                    fragment.isDetached() ||
                    !fragment.isAdded()) {
                return true;
            }
        } else {
            return true;
        }
        return false;
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
