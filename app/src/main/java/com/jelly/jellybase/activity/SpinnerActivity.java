package com.jelly.jellybase.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.materialspinner.MaterialSpinner;
import com.jelly.jellybase.databinding.SpinnerActivityBinding;

/**
 * Created by JELLY on 2017/11/3.
 */

public class SpinnerActivity extends BaseActivity<SpinnerActivityBinding> {
    private MaterialSpinner sp_product_types;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    private void iniView(){
        final String[] mItems = {"One", "Two", "Three", "Four", "Five"};
        getBinding().spProductTypes.setItems(mItems);
        getBinding().spProductTypes.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });
        getBinding().spProductTypes.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
