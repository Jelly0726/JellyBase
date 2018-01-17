package com.jelly.jellybase.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.base.view.BaseFragment;

/**
 * Created by Administrator on 2017/9/18.
 */

public class OrderFragment extends BaseFragment{

    @Override
    protected int getLayoutResource() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {

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