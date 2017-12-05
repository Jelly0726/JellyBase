package com.jelly.jellybase.shopcar;

import android.os.Bundle;

import com.base.httpmvp.presenter.IBasePresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.jelly.jellybase.R;

/**
 * Created by Administrator on 2017/10/28.
 */

public class ShopCartActivity extends BaseActivityImpl {
    private ShopCarFragment shopCarFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopcart_activity);
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
    public IBasePresenter initPresenter() {
        return null;
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
