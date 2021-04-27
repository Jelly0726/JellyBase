package com.jelly.jellybase.shopcar;

import android.os.Bundle;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.jellybase.R;

/**
 * Created by Administrator on 2017/10/28.
 */

public class ShopCartActivity extends BaseActivity {
    private ShopCarFragment shopCarFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            shopCarFragment = (ShopCarFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, "shopCarFragment");
        } else {
            shopCarFragment = ShopCarFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, shopCarFragment)
                .commit();

    }
    @Override
    public int getLayoutId(){
        return R.layout.shopcart_fragment_activity;
    }

    /**
     * 当活动被回收时，存储当前的状态。
     *
     * @param outState 状态数据。
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 存储 Fragment 的状态。
        getSupportFragmentManager().putFragment(outState, "shopCarFragment", shopCarFragment);
    }
}
