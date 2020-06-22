package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import com.base.materialspinner.MaterialSpinner;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;

/**
 * Created by JELLY on 2017/11/3.
 */

public class SpinnerActivity extends BaseActivity {
    private MaterialSpinner sp_product_types;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinner_activity);
        iniView();
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
