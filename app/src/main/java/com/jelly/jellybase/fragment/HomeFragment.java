package com.jelly.jellybase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.view.BaseFragment;


/**
 * Created by Administrator on 2017/9/18.
 */

public class HomeFragment extends BaseFragment {
    private View mRootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //mRootView = inflater.inflate(R.layout.home_fragment, container, false);
        return mRootView;
    }

    public View getRootView() {
        return mRootView;
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

    private View.OnClickListener listener=new View.OnClickListener() {
        Intent intent;
        @Override
        public void onClick(View v) {
            switch (v.getId()){
            }
        }
    };
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
