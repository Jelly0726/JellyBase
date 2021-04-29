package com.jelly.jellybase.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.jelly.baselibrary.BaseFragment;
import com.jelly.jellybase.databinding.WalletFragmentBinding;

/**
 * Created by Administrator on 2017/9/18.
 */

public class OrderFragment extends BaseFragment<WalletFragmentBinding>{
    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniView();
    }
    private void iniView (){
    }
    @Override
    public void setData(String json) {

    }
    @Override
    public boolean onBackPressed() {
        return false;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            return;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}