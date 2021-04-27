package com.jelly.jellybase.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;

import com.jelly.baselibrary.materialspinner.MaterialSpinner;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.jellybase.R;

/**
 * Created by JELLY on 2017/11/3.
 */

public class SpinnerActivity extends BaseActivity {
    private MaterialSpinner sp_product_types;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.spinner_activity;
    }
    private void iniView(){
        sp_product_types= (MaterialSpinner) findViewById(R.id.sp_product_types);
        final String[] mItems = {"One", "Two", "Three", "Four", "Five"};
        sp_product_types.setItems(mItems);
        sp_product_types.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });
        sp_product_types.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
